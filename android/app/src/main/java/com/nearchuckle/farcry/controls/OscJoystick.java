package com.nearchuckle.farcry.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import org.libsdl.app.SDLActivity;

/**
 * On-screen analog joystick for Far Cry player movement (W, A, S, D).
 * In Edit Mode, supports selecting, moving, resizing, and opacity adjustments.
 */
@SuppressLint("ViewConstructor")
public class OscJoystick extends View {
    private final OscElement element;
    private final OscManager manager;

    private final Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint editBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float knobX = 0f;
    private float knobY = 0f;
    private boolean isDown = false;
    private boolean isSelected = false;

    /**
     * Id of the finger that currently drives the stick. Multi-touch must be tracked by pointer id:
     * with plain {@code event.getX()} (index 0) the stick follows a foreign finger as soon as a
     * second touch exists (fire/aim with the other thumb), and the WASD keys stay pressed when the
     * stick finger is lifted first.
     */
    private int activePointerId = MotionEvent.INVALID_POINTER_ID;

    // Movement key states
    private boolean keyW = false;
    private boolean keyS = false;
    private boolean keyA = false;
    private boolean keyD = false;

    // Drag tracking in Edit Mode
    private float startRawX, startRawY;
    private int startXPercent, startYPercent;
    private boolean isDragging = false;

    public OscJoystick(Context context, OscElement element, OscManager manager) {
        super(context);
        this.element = element;
        this.manager = manager;
        element.view = this;

        initPaints();
    }

    private void initPaints() {
        basePaint.setColor(Color.argb(120, 20, 24, 30));
        basePaint.setStyle(Paint.Style.FILL);

        ringPaint.setColor(Color.argb(180, 80, 100, 130));
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(4f);

        thumbPaint.setStyle(Paint.Style.FILL);

        editBorderPaint.setStyle(Paint.Style.STROKE);
        editBorderPaint.setStrokeWidth(5f);
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

        float centerX = w / 2f;
        float centerY = h / 2f;
        float baseRadius = Math.min(centerX, centerY) - 8f;
        float thumbRadius = baseRadius * 0.38f;

        // Base circle
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint);

        // Edit mode outline
        if (manager.isEditMode()) {
            if (isSelected) {
                editBorderPaint.setColor(Color.rgb(0, 255, 128));
                editBorderPaint.setPathEffect(null);
                canvas.drawCircle(centerX, centerY, baseRadius, editBorderPaint);
            } else if (!element.visible) {
                editBorderPaint.setColor(Color.rgb(255, 70, 70));
                editBorderPaint.setPathEffect(new DashPathEffect(new float[]{10, 8}, 0));
                canvas.drawCircle(centerX, centerY, baseRadius, editBorderPaint);
            } else {
                ringPaint.setColor(Color.argb(200, 100, 150, 255));
                canvas.drawCircle(centerX, centerY, baseRadius, ringPaint);
            }
        } else {
            ringPaint.setColor(isDown ? Color.rgb(255, 160, 30) : Color.argb(180, 80, 100, 130));
            canvas.drawCircle(centerX, centerY, baseRadius, ringPaint);
        }

        // Thumb knob position
        float curThumbX = centerX + (isDown ? knobX : 0f);
        float curThumbY = centerY + (isDown ? knobY : 0f);

        // Thumb gradient
        int c1 = isDown ? Color.rgb(255, 180, 40) : Color.rgb(90, 110, 140);
        int c2 = isDown ? Color.rgb(200, 100, 10) : Color.rgb(40, 50, 65);
        RadialGradient grad = new RadialGradient(
                curThumbX, curThumbY, thumbRadius,
                c1, c2, Shader.TileMode.CLAMP);
        thumbPaint.setShader(grad);

        canvas.drawCircle(curThumbX, curThumbY, thumbRadius, thumbPaint);
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
            case MotionEvent.ACTION_DOWN: {
                activePointerId = event.getPointerId(0);
                requestParentNotToIntercept();
                applyKnob(event, 0);
                return true;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                // Take over the new finger only when the stick is free right now.
                if (activePointerId == MotionEvent.INVALID_POINTER_ID) {
                    int index = event.getActionIndex();
                    activePointerId = event.getPointerId(index);
                    applyKnob(event, index);
                }
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                int index = event.findPointerIndex(activePointerId);
                if (index >= 0) {
                    applyKnob(event, index);
                }
                return true;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // Some finger was lifted. Release the stick only when it was OUR finger,
                // otherwise movement would keep going after the thumb is already off the stick.
                if (event.getPointerId(event.getActionIndex()) == activePointerId) {
                    releaseStick();
                }
                return true;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                releaseStick();
                return true;
        }

        return super.onTouchEvent(event);
    }

    /** Moves the knob to the given pointer position and updates the WASD key states. */
    private void applyKnob(MotionEvent event, int pointerIndex) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float maxRadius = Math.min(centerX, centerY) - 10f;
        if (maxRadius <= 0f) {
            return;
        }

        isDown = true;
        float dx = event.getX(pointerIndex) - centerX;
        float dy = event.getY(pointerIndex) - centerY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > maxRadius) {
            dx = (dx / dist) * maxRadius;
            dy = (dy / dist) * maxRadius;
        }

        knobX = dx;
        knobY = dy;
        updateMovementKeys(dx / maxRadius, dy / maxRadius);
        invalidate();
    }

    /** Centers the knob and releases every movement key. */
    private void releaseStick() {
        activePointerId = MotionEvent.INVALID_POINTER_ID;
        isDown = false;
        knobX = 0f;
        knobY = 0f;
        clearMovementKeys();
        invalidate();
    }

    private void requestParentNotToIntercept() {
        android.view.ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // The overlay only goes away when the activity is destroyed (GameActivity kills the
        // process right after), so just drop the local state here. No native key events are
        // sent on purpose - SDL has already been shut down at this point.
        activePointerId = MotionEvent.INVALID_POINTER_ID;
        isDown = false;
        knobX = 0f;
        knobY = 0f;
        keyW = false;
        keyS = false;
        keyA = false;
        keyD = false;
        super.onDetachedFromWindow();
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

    private void updateMovementKeys(float normX, float normY) {
        float deadzone = 0.25f;
        boolean newW = normY < -deadzone;
        boolean newS = normY > deadzone;
        boolean newA = normX < -deadzone;
        boolean newD = normX > deadzone;

        setKeyState(KeyEvent.KEYCODE_W, newW, keyW);
        setKeyState(KeyEvent.KEYCODE_S, newS, keyS);
        setKeyState(KeyEvent.KEYCODE_A, newA, keyA);
        setKeyState(KeyEvent.KEYCODE_D, newD, keyD);

        keyW = newW;
        keyS = newS;
        keyA = newA;
        keyD = newD;
    }

    private void clearMovementKeys() {
        if (keyW) SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_W);
        if (keyS) SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_S);
        if (keyA) SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_A);
        if (keyD) SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_D);
        keyW = false;
        keyS = false;
        keyA = false;
        keyD = false;
    }

    private void setKeyState(int keyCode, boolean desired, boolean current) {
        if (desired && !current) {
            SDLActivity.onNativeKeyDown(keyCode);
        } else if (!desired && current) {
            SDLActivity.onNativeKeyUp(keyCode);
        }
    }
}
