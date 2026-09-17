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
    private final RectF bounds = new RectF();

    private Drawable iconDrawable;
    private boolean isPressed = false;
    private boolean isSelected = false;

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
            bgPaint.setColor(isPressed ? Color.argb(220, 255, 140, 0) : Color.argb(160, 25, 30, 36));
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
            borderPaint.setColor(isPressed ? Color.rgb(255, 200, 50) : Color.argb(180, 70, 80, 95));
            canvas.drawRoundRect(bounds, radius, radius, borderPaint);
        }

        // Icon or text
        if (iconDrawable != null) {
            int iconPadding = (int) (w * 0.22f);
            iconDrawable.setBounds(iconPadding, iconPadding, w - iconPadding, h - iconPadding);
            iconDrawable.setTint(isPressed ? Color.BLACK : Color.WHITE);
            iconDrawable.draw(canvas);
        } else {
            // Draw element short name/label
            textPaint.setTextSize(w * 0.28f);
            float textY = h / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
            canvas.drawText(element.label, w / 2f, textY, textPaint);
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
                isPressed = true;
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                sendInputDown();
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isPressed = false;
                sendInputUp();
                invalidate();
                return true;
        }

        return super.onTouchEvent(event);
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
