package com.nearchuckle.farcry.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import org.libsdl.app.SDLActivity;

/**
 * High quality on-screen touch button for Far Cry.
 * Dispatches key down/up or mouse click to SDLActivity, or allows drag-to-move in Edit Mode.
 */
@SuppressLint("ViewConstructor")
public class OscButton extends View {
    private final OscElement element;
    private final OscManager manager;

    private final Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint editBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF bounds = new RectF();

    private Drawable iconDrawable;
    private boolean isPressed = false;
    private boolean isSelected = false;

    /**
     * Set while a sticky (toggle) button is locked ON: the key / mouse button stays pressed
     * without a finger on the screen until the button is tapped again (or the overlay gives up
     * input, see {@link #forceRelease()}).
     */
    private boolean latched = false;

    /** Finger that pressed this button - needed so the key is released on multi-touch pointer up. */
    private int activePointerId = MotionEvent.INVALID_POINTER_ID;

    // Drag tracking in Edit Mode
    private float startRawX, startRawY;
    private int startXPercent, startYPercent;
    private boolean isDragging = false;

    public OscButton(Context context, OscElement element, OscManager manager) {
        super(context);
        this.element = element;
        this.manager = manager;
        element.view = this;

        initPaints();
        loadIcon();
    }

    private void initPaints() {
        bgPaint.setColor(Color.argb(160, 25, 30, 36));
        bgPaint.setStyle(Paint.Style.FILL);

        borderPaint.setColor(Color.argb(180, 70, 80, 95));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f);

        editBorderPaint.setStyle(Paint.Style.STROKE);
        editBorderPaint.setStrokeWidth(5f);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        ledPaint.setColor(Color.WHITE);
        ledPaint.setStyle(Paint.Style.FILL);
    }

    private void loadIcon() {
        if (element.iconRes != 0) {
            try {
                iconDrawable = ContextCompat.getDrawable(getContext(), element.iconRes);
            } catch (Exception ignored) {}
        }
    }

    public OscElement getElement() {
        return element;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        bounds.set(4, 4, w - 4, h - 4);
        float radius = Math.min(w, h) / 2f;

        // Background
        if (manager.isEditMode()) {
            if (!element.visible) {
                bgPaint.setColor(Color.argb(90, 80, 30, 30));
            } else {
                bgPaint.setColor(Color.argb(160, 25, 30, 36));
            }
        } else {
            // Three states: idle (dark), finger on it (orange), sticky button locked ON (green).
            if (latched) {
                bgPaint.setColor(Color.argb(230, 0, 190, 85));
            } else if (isPressed) {
                bgPaint.setColor(Color.argb(220, 255, 140, 0));
            } else {
                bgPaint.setColor(Color.argb(160, 25, 30, 36));
            }
        }
        canvas.drawRoundRect(bounds, radius, radius, bgPaint);

        // Border
        if (manager.isEditMode()) {
            if (isSelected) {
                editBorderPaint.setColor(Color.rgb(0, 255, 128)); // Bright neon green for selected
                editBorderPaint.setPathEffect(null);
                canvas.drawRoundRect(bounds, radius, radius, editBorderPaint);
            } else if (!element.visible) {
                editBorderPaint.setColor(Color.rgb(255, 70, 70));
                editBorderPaint.setPathEffect(new DashPathEffect(new float[]{10, 8}, 0));
                canvas.drawRoundRect(bounds, radius, radius, editBorderPaint);
            } else {
                borderPaint.setColor(Color.argb(200, 100, 150, 255));
                canvas.drawRoundRect(bounds, radius, radius, borderPaint);
            }
        } else {
            if (latched) {
                borderPaint.setColor(Color.rgb(0, 255, 128));
                borderPaint.setStrokeWidth(6f);
            } else {
                borderPaint.setColor(isPressed ? Color.rgb(255, 200, 50) : Color.argb(180, 70, 80, 95));
                borderPaint.setStrokeWidth(3f);
            }
            canvas.drawRoundRect(bounds, radius, radius, borderPaint);
        }

        // Draw button letter / text label in center
        String text = (element.buttonText != null && !element.buttonText.isEmpty())
                ? element.buttonText : element.label;
        float textSize = w * 0.34f;
        if (text.length() >= 5) {
            textSize = w * 0.20f;
        } else if (text.length() >= 4) {
            textSize = w * 0.24f;
        } else if (text.length() >= 2) {
            textSize = w * 0.29f;
        }
        textPaint.setTextSize(textSize);
        textPaint.setColor((isPressed || latched) ? Color.BLACK : Color.WHITE);
        float textY = h / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(text, w / 2f, textY, textPaint);

        if (latched) {
            // Small "LED" at the bottom: the action stays active even though no finger is here.
            ledPaint.setColor(Color.WHITE);
            canvas.drawCircle(w / 2f, h - (h * 0.09f), Math.max(3f, w * 0.045f), ledPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (manager.isEditMode()) {
            handleEditTouch(event);
            return true;
        }

        if (!element.visible) {
            return false;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                if (element.toggle) {
                    // Sticky button: this tap flips the state. Locking ON presses the key / mouse
                    // button and keeps it pressed after the finger leaves the screen; the next tap
                    // releases it. No pointer is tracked - the finger is free to go anywhere.
                    activePointerId = MotionEvent.INVALID_POINTER_ID;
                    latched = !latched;
                    if (latched) {
                        sendInputDown();
                    } else {
                        sendInputUp();
                    }
                    invalidate();
                    return true;
                }

                activePointerId = event.getPointerId(0);
                isPressed = true;
                sendInputDown();
                invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                // Another finger went up. Release the button only if it was the finger
                // that pressed it, otherwise fire/aim/keys stay held down.
                if (event.getPointerId(event.getActionIndex()) == activePointerId) {
                    releaseButton();
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // A sticky button is released by its next tap, not by lifting the finger.
                if (!element.toggle) {
                    releaseButton();
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void releaseButton() {
        activePointerId = MotionEvent.INVALID_POINTER_ID;
        if (!isPressed) {
            return;
        }
        isPressed = false;
        sendInputUp();
        invalidate();
    }

    /**
     * Drops whatever this button holds right now - a normal press or a sticky (latched) one - and
     * sends the matching release event. Used by {@link OscManager#releaseAllPressed()} when the
     * overlay stops being the input source, so a locked AIM can never stay pressed in the game.
     */
    public void forceRelease() {
        boolean wasActive = isPressed || latched;
        isPressed = false;
        latched = false;
        activePointerId = MotionEvent.INVALID_POINTER_ID;
        if (wasActive) {
            sendInputUp();
            invalidate();
        }
    }

    /** True while a sticky button is locked ON. */
    public boolean isLatched() {
        return latched;
    }

    private void handleEditTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startRawX = event.getRawX();
                startRawY = event.getRawY();
                startXPercent = element.xPercent;
                startYPercent = element.yPercent;
                isDragging = false;
                manager.selectElement(element);
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - startRawX;
                float dy = event.getRawY() - startRawY;
                if (Math.abs(dx) > 6 || Math.abs(dy) > 6) {
                    isDragging = true;
                    int parentW = manager.getScreenWidth();
                    int parentH = manager.getScreenHeight();
                    if (parentW > 0 && parentH > 0) {
                        int deltaXPerMille = (int) ((dx / parentW) * 1000);
                        int deltaYPerMille = (int) ((dy / parentH) * 1000);

                        element.xPercent = Math.max(0, Math.min(950, startXPercent + deltaXPerMille));
                        element.yPercent = Math.max(0, Math.min(950, startYPercent + deltaYPerMille));
                        element.updateViewLayout(parentW, parentH, true);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    manager.saveLayout();
                }
                isDragging = false;
                break;
        }
    }

    private void sendInputDown() {
        if ("btn_edit".equals(element.id)) {
            manager.toggleEditMode();
            return;
        }

        if (element.mouseButton != 0) {
            int buttonState = (element.mouseButton == 1) ? MotionEvent.BUTTON_PRIMARY : MotionEvent.BUTTON_SECONDARY;
            SDLActivity.onNativeMouse(buttonState, MotionEvent.ACTION_DOWN, 0, 0, false);
        } else if (element.sdlKeyCode != 0) {
            SDLActivity.onNativeKeyDown(element.sdlKeyCode);
        }
    }

    private void sendInputUp() {
        if ("btn_edit".equals(element.id)) {
            return;
        }

        if (element.mouseButton != 0) {
            SDLActivity.onNativeMouse(0, MotionEvent.ACTION_UP, 0, 0, false);
        } else if (element.sdlKeyCode != 0) {
            SDLActivity.onNativeKeyUp(element.sdlKeyCode);
        }
    }
}
