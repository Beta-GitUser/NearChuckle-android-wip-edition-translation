package com.nearchuckle.farcry.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import org.libsdl.app.SDLActivity;

/**
 * On-screen touch camera look pad. Translates touch drags into relative mouse motion for Far Cry.
 */
@SuppressLint("ViewConstructor")
public class OscTouchLook extends View {
    private final OscElement element;
    private final OscManager manager;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint editBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF bounds = new RectF();

    private float lastX = 0f;
    private float lastY = 0f;
    private int activePointerId = MotionEvent.INVALID_POINTER_ID;
    private boolean isSelected = false;

    // Drag tracking in Edit Mode
    private float startRawX, startRawY;
    private int startXPercent, startYPercent;
    private boolean isDragging = false;

    public OscTouchLook(Context context, OscElement element, OscManager manager) {
        super(context);
        this.element = element;
        this.manager = manager;
        element.view = this;

        initPaints();
    }

    private void initPaints() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);

        editBorderPaint.setStyle(Paint.Style.STROKE);
        editBorderPaint.setStrokeWidth(5f);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(36f);
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
        float corner = 20f;

        if (manager.isEditMode()) {
            if (isSelected) {
                editBorderPaint.setColor(Color.rgb(0, 255, 128));
                editBorderPaint.setPathEffect(null);
                canvas.drawRoundRect(bounds, corner, corner, editBorderPaint);
            } else if (!element.visible) {
                editBorderPaint.setColor(Color.rgb(255, 70, 70));
                editBorderPaint.setPathEffect(new DashPathEffect(new float[]{10, 8}, 0));
                canvas.drawRoundRect(bounds, corner, corner, editBorderPaint);
            } else {
                paint.setColor(Color.argb(120, 100, 150, 255));
                paint.setPathEffect(new DashPathEffect(new float[]{12, 8}, 0));
                canvas.drawRoundRect(bounds, corner, corner, paint);
            }

            boolean isRu = java.util.Locale.getDefault().getLanguage().equals("ru");
            canvas.drawText(isRu ? "Область обзора (Камера)" : "Camera / Look Area", w / 2f, h / 2f, textPaint);
        }
        // In gameplay mode, the look pad is transparent to not block view
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
                lastX = event.getX(0);
                lastY = event.getY(0);
                return true;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                // Secondary touch on look area
                int pointerIndex = event.getActionIndex();
                if (activePointerId == MotionEvent.INVALID_POINTER_ID) {
                    activePointerId = event.getPointerId(pointerIndex);
                    lastX = event.getX(pointerIndex);
                    lastY = event.getY(pointerIndex);
                }
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                int pointerIndex = event.findPointerIndex(activePointerId);
                if (pointerIndex >= 0) {
                    float curX = event.getX(pointerIndex);
                    float curY = event.getY(pointerIndex);

                    float dx = curX - lastX;
                    float dy = curY - lastY;

                    float sensitivity = manager.getMouseSensitivity();
                    int relX = Math.round(dx * sensitivity);
                    int relY = Math.round(dy * sensitivity);

                    if (relX != 0 || relY != 0) {
                        SDLActivity.onNativeMouse(0, MotionEvent.ACTION_MOVE, relX, relY, true);
                    }

                    lastX = curX;
                    lastY = curY;
                }
                return true;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = event.getActionIndex();
                int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    activePointerId = MotionEvent.INVALID_POINTER_ID;
                }
                return true;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                activePointerId = MotionEvent.INVALID_POINTER_ID;
                return true;
            }
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
}
