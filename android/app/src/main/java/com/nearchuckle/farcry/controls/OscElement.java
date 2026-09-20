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
    /**
     * Sticky ("toggle") behaviour. When true, the first tap presses the key / mouse button and
     * keeps it pressed after the finger leaves the screen; the second tap releases it. This is
     * used for aiming: keeping a finger on AIM while looking around with the other hand is
     * awkward, so the player just taps AIM once and shoots at leisure.
     */
    public final boolean toggle;

    public int xPercent;
    public int yPercent;
    public int sizeDp;
    public float opacity;
    public final boolean defaultVisible;
    public boolean visible;

    public View view;

    public OscElement(String id, String label, String buttonText, int defaultX, int defaultY, int defaultSizeDp,
                      float defaultOpacity, int iconRes, int sdlKeyCode, int mouseButton, boolean defaultVisible) {
        this(id, label, buttonText, defaultX, defaultY, defaultSizeDp, defaultOpacity, iconRes, sdlKeyCode,
                mouseButton, defaultVisible, false);
    }

    public OscElement(String id, String label, String buttonText, int defaultX, int defaultY, int defaultSizeDp,
                      float defaultOpacity, int iconRes, int sdlKeyCode, int mouseButton, boolean defaultVisible,
                      boolean toggle) {
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
        this.toggle = toggle;

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
        // Normalize everything that comes from the preferences: values saved by older
        // builds or edited on another screen must not be able to break the overlay
        // (giant pads, off-screen positions, invisible alpha).
        xPercent = clampInt(prefs.getInt("osc_" + id + "_x", defaultXPercent), 0, 1000);
        yPercent = clampInt(prefs.getInt("osc_" + id + "_y", defaultYPercent), 0, 1000);
        // Upper bound is the editor limit (260), but never below the element's own default
        // (the camera look area defaults to 480 design units and the editor can only shrink it).
        sizeDp = clampInt(prefs.getInt("osc_" + id + "_size", defaultSizeDp), 32, Math.max(260, defaultSizeDp));
        opacity = clampFloat(prefs.getFloat("osc_" + id + "_opacity", defaultOpacity), 0.1f, 1.0f);
        visible = prefs.getBoolean("osc_" + id + "_visible", defaultVisible);
    }

    private static int clampInt(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static float clampFloat(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
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
        if (view == null) {
            return;
        }
        if (screenWidth <= 0 || screenHeight <= 0) {
            return;
        }

        // The overlay must look the same on every smartphone screen. Positions below are
        // stored in per-mille of the screen, and the size is stored in "design units" of the
        // reference 960x540 landscape layout and scaled with the current screen. The old
        // scheme used physical dp (sizeDp * density), which is an absolute value: on screens
        // with unusual density or with a weird saved size a pad could grow to cover the whole
        // screen (giant F9 / ~ buttons), so layouts differed from phone to phone.
        float scale = Math.min(screenWidth / 960f, screenHeight / 540f);
        if (scale <= 0f) {
            scale = 1f;
        }
        int pxSize = Math.round(sizeDp * scale);

        // Hard sanity limits: whatever is stored in the preferences, a pad may neither
        // vanish nor cover the screen. Covering would need pxSize >= the long side, so the
        // 90%-of-short-side cap always keeps it strictly inside, while big pads like the
        // camera look area (480 design units) stay large as intended.
        int minSide = Math.min(screenWidth, screenHeight);
        int minPx = Math.max(24, (int) (minSide * 0.05f));
        int maxPx = (int) (minSide * 0.90f);
        if (pxSize < minPx) {
            pxSize = minPx;
        } else if (pxSize > maxPx) {
            pxSize = maxPx;
        }

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
