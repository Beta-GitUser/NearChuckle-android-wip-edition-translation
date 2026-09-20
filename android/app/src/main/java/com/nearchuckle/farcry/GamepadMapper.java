package com.nearchuckle.farcry;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.libsdl.app.SDLActivity;

import java.util.HashSet;
import java.util.Set;

/**
 * Maps a hardware gamepad to the same key / mouse injections the on-screen controls use
 * (SDLActivity.onNativeKeyDown / onNativeKeyUp / onNativeMouse). Far Cry (CryEngine 1) has no
 * native gamepad support on non-Windows builds, so the pad is translated into WASD + mouse look
 * + mouse buttons here, in Java. The engine and the SDL layer are not modified at all.
 *
 * Layout:
 *   Left stick / D-Pad      - movement (W A S D)
 *   Right stick             - camera look (relative mouse)
 *   RT / R2 / R1            - fire (left mouse button)
 *   LT / L2                 - aim (right mouse button)
 *   A                       - jump (Space)
 *   B                       - crouch (C)
 *   X                       - reload (R)
 *   Y                       - use / action (F)
 *   L1                      - sprint (Left Shift)
 *   Left stick click        - prone (V)
 *   Right stick click       - grenade (G)
 *   Start / Mode            - menu (Esc)
 *   Select / Back           - objectives / PDA (Tab)
 *
 * The mapper only reacts to events whose source is a gamepad / joystick / D-Pad, so hardware
 * keyboards and mice are completely unaffected.
 */
public final class GamepadMapper {

    private static final float STICK_DEADZONE = 0.22f;
    private static final float TRIGGER_PRESS = 0.35f;
    private static final float TRIGGER_RELEASE = 0.20f;
    /** Mouse pixels produced per full stick deflection per motion event. */
    private static final float LOOK_GAIN = 16f;

    private final Context context;

    // Current injected state, so keys are never sent twice or left pressed.
    private final Set<Integer> heldKeys = new HashSet<>();
    private boolean moveW, moveS, moveA, moveD;
    private boolean fireFromButton, fireFromAxis;
    private boolean aimFromButton, aimFromAxis;

    public GamepadMapper(Context context) {
        this.context = context;
    }

    // -----------------------------------------------------------------------------------------
    // Buttons and D-Pad (key events)
    // -----------------------------------------------------------------------------------------

    /** @return true when the event came from a gamepad and was consumed. */
    public boolean handleKeyEvent(KeyEvent event) {
        if (!isGamepadSource(event.getSource())) {
            return false;
        }

        final boolean down = event.getAction() == KeyEvent.ACTION_DOWN;
        if (event.getAction() != KeyEvent.ACTION_DOWN && event.getAction() != KeyEvent.ACTION_UP) {
            return true; // long-press etc. - swallow for pads
        }

        switch (event.getKeyCode()) {
            // Triggers arriving as buttons: fire / aim.
            case KeyEvent.KEYCODE_BUTTON_R1:
            case KeyEvent.KEYCODE_BUTTON_R2:
                setFire(down, true);
                return true;
            case KeyEvent.KEYCODE_BUTTON_L2:
                setAim(down, true);
                return true;
        }

        int mapped = mapButton(event.getKeyCode());
        if (mapped == 0) {
            // Unknown pad button: do not let it leak into the game as a random key.
            return true;
        }
        if (down) {
            if (event.getRepeatCount() == 0) {
                pressKey(mapped);
            }
        } else {
            releaseKey(mapped);
        }
        return true;
    }

    private static int mapButton(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A:      return KeyEvent.KEYCODE_SPACE;       // jump
            case KeyEvent.KEYCODE_BUTTON_B:      return KeyEvent.KEYCODE_C;           // crouch
            case KeyEvent.KEYCODE_BUTTON_X:      return KeyEvent.KEYCODE_R;           // reload
            case KeyEvent.KEYCODE_BUTTON_Y:      return KeyEvent.KEYCODE_F;           // use
            case KeyEvent.KEYCODE_BUTTON_L1:     return KeyEvent.KEYCODE_SHIFT_LEFT;  // sprint
            case KeyEvent.KEYCODE_BUTTON_THUMBL: return KeyEvent.KEYCODE_V;           // prone
            case KeyEvent.KEYCODE_BUTTON_THUMBR: return KeyEvent.KEYCODE_G;           // grenade
            case KeyEvent.KEYCODE_BUTTON_START:
            case KeyEvent.KEYCODE_BUTTON_MODE:   return KeyEvent.KEYCODE_ESCAPE;      // menu
            case KeyEvent.KEYCODE_BUTTON_SELECT:
            case KeyEvent.KEYCODE_BACK:          return KeyEvent.KEYCODE_TAB;         // objectives
            case KeyEvent.KEYCODE_DPAD_UP:       return KeyEvent.KEYCODE_W;
            case KeyEvent.KEYCODE_DPAD_DOWN:     return KeyEvent.KEYCODE_S;
            case KeyEvent.KEYCODE_DPAD_LEFT:     return KeyEvent.KEYCODE_A;
            case KeyEvent.KEYCODE_DPAD_RIGHT:    return KeyEvent.KEYCODE_D;
            default:                             return 0;
        }
    }

    // -----------------------------------------------------------------------------------------
    // Sticks and triggers (motion events)
    // -----------------------------------------------------------------------------------------

    /** @return true when the event came from a joystick-class device and was consumed. */
    public boolean handleMotionEvent(MotionEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK) == 0) {
            return false;
        }

        // Left stick -> WASD
        float lx = applyDeadzone(event.getAxisValue(MotionEvent.AXIS_X));
        float ly = applyDeadzone(event.getAxisValue(MotionEvent.AXIS_Y));
        setMovementKey(KeyEvent.KEYCODE_W, ly < -STICK_DEADZONE, moveW); moveW = ly < -STICK_DEADZONE;
        setMovementKey(KeyEvent.KEYCODE_S, ly >  STICK_DEADZONE, moveS); moveS = ly >  STICK_DEADZONE;
        setMovementKey(KeyEvent.KEYCODE_A, lx < -STICK_DEADZONE, moveA); moveA = lx < -STICK_DEADZONE;
        setMovementKey(KeyEvent.KEYCODE_D, lx >  STICK_DEADZONE, moveD); moveD = lx >  STICK_DEADZONE;

        // Right stick -> relative mouse look (honours the launcher mouse sensitivity)
        float rx = applyDeadzone(event.getAxisValue(MotionEvent.AXIS_Z));
        float ry = applyDeadzone(event.getAxisValue(MotionEvent.AXIS_RZ));
        if (rx != 0f || ry != 0f) {
            float sens = getLookSensitivity();
            int dx = Math.round(rx * LOOK_GAIN * sens);
            int dy = Math.round(ry * LOOK_GAIN * sens);
            if (dx != 0 || dy != 0) {
                SDLActivity.onNativeMouse(0, MotionEvent.ACTION_MOVE, dx, dy, true);
            }
        }

        // Triggers as axes -> fire / aim
        float rt = triggerValue(event, MotionEvent.AXIS_RTRIGGER, MotionEvent.AXIS_GAS);
        float lt = triggerValue(event, MotionEvent.AXIS_LTRIGGER, MotionEvent.AXIS_BRAKE);
        setFireAxis(rt > (fireFromAxis ? TRIGGER_RELEASE : TRIGGER_PRESS));
        setAimAxis(lt > (aimFromAxis ? TRIGGER_RELEASE : TRIGGER_PRESS));

        return true;
    }

    private static float applyDeadzone(float v) {
        if (v > -STICK_DEADZONE && v < STICK_DEADZONE) {
            return 0f;
        }
        return v;
    }

    private static float triggerValue(MotionEvent event, int axis, int fallbackAxis) {
        float v = event.getAxisValue(axis);
        if (v <= 0f && fallbackAxis != 0) {
            v = event.getAxisValue(fallbackAxis);
        }
        return v;
    }

    // -----------------------------------------------------------------------------------------
    // Injected state helpers
    // -----------------------------------------------------------------------------------------

    private void setMovementKey(int keyCode, boolean want, boolean current) {
        if (want && !current) {
            pressKey(keyCode);
        } else if (!want && current) {
            releaseKey(keyCode);
        }
    }

    private void pressKey(int keyCode) {
        if (heldKeys.add(keyCode)) {
            SDLActivity.onNativeKeyDown(keyCode);
        }
    }

    private void releaseKey(int keyCode) {
        if (heldKeys.remove(keyCode)) {
            SDLActivity.onNativeKeyUp(keyCode);
        }
    }

    private void setFire(boolean pressed, boolean fromButton) {
        fireFromButton = fromButton ? pressed : fireFromButton;
        boolean want = fireFromButton || fireFromAxis;
        if (pressed && fromButton) {
            pressMouse(SDL_BUTTON_LEFT);
        } else if (!want) {
            releaseMouse(SDL_BUTTON_LEFT);
        }
    }

    private void setFireAxis(boolean pressed) {
        if (pressed == fireFromAxis) {
            return;
        }
        fireFromAxis = pressed;
        if (pressed || !fireFromButton) {
            if (pressed) {
                pressMouse(SDL_BUTTON_LEFT);
            } else {
                releaseMouse(SDL_BUTTON_LEFT);
            }
        }
    }

    private void setAim(boolean pressed, boolean fromButton) {
        aimFromButton = fromButton ? pressed : aimFromButton;
        boolean want = aimFromButton || aimFromAxis;
        if (pressed && fromButton) {
            pressMouse(SDL_BUTTON_RIGHT);
        } else if (!want) {
            releaseMouse(SDL_BUTTON_RIGHT);
        }
    }

    private void setAimAxis(boolean pressed) {
        if (pressed == aimFromAxis) {
            return;
        }
        aimFromAxis = pressed;
        if (pressed || !aimFromButton) {
            if (pressed) {
                pressMouse(SDL_BUTTON_RIGHT);
            } else {
                releaseMouse(SDL_BUTTON_RIGHT);
            }
        }
    }

    // Mouse button injection (the same way OscButton does it).
    private static final int SDL_BUTTON_LEFT = MotionEvent.BUTTON_PRIMARY;
    private static final int SDL_BUTTON_RIGHT = MotionEvent.BUTTON_SECONDARY;
    private boolean leftDown, rightDown;

    private void pressMouse(int button) {
        if (button == SDL_BUTTON_LEFT) {
            if (!leftDown) {
                leftDown = true;
                SDLActivity.onNativeMouse(button, MotionEvent.ACTION_DOWN, 0, 0, false);
            }
        } else if (!rightDown) {
            rightDown = true;
            SDLActivity.onNativeMouse(button, MotionEvent.ACTION_DOWN, 0, 0, false);
        }
    }

    private void releaseMouse(int button) {
        if (button == SDL_BUTTON_LEFT) {
            if (leftDown) {
                leftDown = false;
                SDLActivity.onNativeMouse(button, MotionEvent.ACTION_UP, 0, 0, false);
            }
        } else if (rightDown) {
            rightDown = false;
            SDLActivity.onNativeMouse(button, MotionEvent.ACTION_UP, 0, 0, false);
        }
    }

    // -----------------------------------------------------------------------------------------
    // Safety
    // -----------------------------------------------------------------------------------------

    /** Releases everything (pause, focus loss, pad unplug) - no key may stay stuck. */
    public void releaseAll() {
        for (int keyCode : new HashSet<>(heldKeys)) {
            releaseKey(keyCode);
        }
        moveW = moveS = moveA = moveD = false;
        fireFromButton = fireFromAxis = false;
        aimFromButton = aimFromAxis = false;
        releaseMouse(SDL_BUTTON_LEFT);
        releaseMouse(SDL_BUTTON_RIGHT);
    }

    public static boolean isGamepadSource(int source) {
        return (source & InputDevice.SOURCE_GAMEPAD) != 0
                || (source & InputDevice.SOURCE_JOYSTICK) != 0
                || (source & InputDevice.SOURCE_DPAD) != 0;
    }

    private float getLookSensitivity() {
        try {
            SharedPreferences prefs = context.getSharedPreferences(LauncherActivity.PREFS_NAME, Context.MODE_PRIVATE);
            return prefs.getFloat(LauncherActivity.KEY_MOUSE_SENSITIVITY, 1.0f);
        } catch (Exception e) {
            return 1.0f;
        }
    }
}
