package com.nearchuckle.farcry.controls;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.Serializable;

/**
 * Base data model and layout coordinator for an on-screen control element.
 */
public class OscElement implements Serializable {
    public final String id;
    public final String label;
    public final String buttonText;
    public final int defaultXPercent; // 0 - 1000 (per mille)
    public final int defaultYPercent; // 0 - 1000 (per mille)
    public final int defaultSizeDp;
    public final float defaultOpacity;
    public final int iconRes;
    public final int sdlKeyCode;
    public final int mouseButton; // 0: not mouse, 1: left, 2: middle, 3: right

    public int xPercent;
    public int yPercent;
    public int sizeDp;
    public float opacity;
    public final boolean defaultVisible;
    public boolean visible;

    public View view;

    public OscElement(String id, String label, String buttonText, int defaultX, int defaultY, int defaultSizeDp,
                      float defaultOpacity, int iconRes, int sdlKeyCode, int mouseButton, boolean defaultVisible) {
        this.id = id;
        this.label = label;
        this.buttonText = (buttonText != null) ? buttonText : label;
        this.defaultXPercent = defaultX;
        this.defaultYPercent = defaultY;
        this.defaultSizeDp = defaultSizeDp;
        this.defaultOpacity = defaultOpacity;
        this.iconRes = iconRes;
        this.sdlKeyCode = sdlKeyCode;
        this.mouseButton = mouseButton;
        this.defaultVisible = defaultVisible;

        this.xPercent = defaultX;
        this.yPercent = defaultY;
        this.sizeDp = defaultSizeDp;
        this.opacity = defaultOpacity;
        this.visible = defaultVisible;
    }

    public OscElement(String id, String label, String buttonText, int defaultX, int defaultY, int defaultSizeDp,
                      float defaultOpacity, int iconRes, int sdlKeyCode, int mouseButton) {
        this(id, label, buttonText, defaultX, defaultY, defaultSizeDp, defaultOpacity, iconRes, sdlKeyCode, mouseButton, true);
    }

    public OscElement(String id, String label, int defaultX, int defaultY, int defaultSizeDp,
                      float defaultOpacity, int iconRes, int sdlKeyCode, int mouseButton) {
        this(id, label, label, defaultX, defaultY, defaultSizeDp, defaultOpacity, iconRes, sdlKeyCode, mouseButton, true);
    }

    public void load(SharedPreferences prefs) {
        xPercent = prefs.getInt("osc_" + id + "_x", defaultXPercent);
        yPercent = prefs.getInt("osc_" + id + "_y", defaultYPercent);
        sizeDp = prefs.getInt("osc_" + id + "_size", defaultSizeDp);
        opacity = prefs.getFloat("osc_" + id + "_opacity", defaultOpacity);
        visible = prefs.getBoolean("osc_" + id + "_visible", defaultVisible);
    }

    public void save(SharedPreferences.Editor editor) {
        editor.putInt("osc_" + id + "_x", xPercent);
        editor.putInt("osc_" + id + "_y", yPercent);
        editor.putInt("osc_" + id + "_size", sizeDp);
        editor.putFloat("osc_" + id + "_opacity", opacity);
        editor.putBoolean("osc_" + id + "_visible", visible);
    }

    public void reset() {
        xPercent = defaultXPercent;
        yPercent = defaultYPercent;
        sizeDp = defaultSizeDp;
        opacity = defaultOpacity;
        visible = defaultVisible;
    }

    public void changeSize(int deltaDp) {
        sizeDp = Math.max(32, Math.min(260, sizeDp + deltaDp));
    }

    public void changeOpacity(float delta) {
        opacity = Math.max(0.1f, Math.min(1.0f, opacity + delta));
    }

    public void toggleVisibility() {
        visible = !visible;
    }

    public void updateViewLayout(int screenWidth, int screenHeight, boolean isEditMode) {
        if (view == null) return;

        float density = view.getContext().getResources().getDisplayMetrics().density;
        int pxSize = (int) (sizeDp * density);

        int left = (xPercent * screenWidth) / 1000;
        int top = (yPercent * screenHeight) / 1000;

        // Keep inside bounds
        left = Math.max(0, Math.min(screenWidth - pxSize, left));
        top = Math.max(0, Math.min(screenHeight - pxSize, top));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(pxSize, pxSize);
        lp.leftMargin = left;
        lp.topMargin = top;
        view.setLayoutParams(lp);

        if (isEditMode) {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(visible ? opacity : Math.max(0.2f, opacity * 0.4f));
        } else {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            view.setAlpha(opacity);
        }
    }
}
