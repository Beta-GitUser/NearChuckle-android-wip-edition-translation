package com.nearchuckle.farcry.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Floating toolbar shown during Edit Mode allowing user to resize, adjust opacity,
 * toggle visibility, reset, and save the custom touch layout.
 */
@SuppressLint("ViewConstructor")
public class EditToolbarView extends LinearLayout {
    private final OscManager manager;
    private final TextView titleView;
    private final Button btnSizePlus;
    private final Button btnSizeMinus;
    private final Button btnOpacityPlus;
    private final Button btnOpacityMinus;
    private final Button btnVisibility;
    private final Button btnReset;
    private final Button btnDone;

    public EditToolbarView(Context context, OscManager manager) {
        super(context);
        this.manager = manager;

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setBackgroundColor(Color.argb(220, 20, 25, 32));
        int pad = dpToPx(8);
        setPadding(pad, pad, pad, pad);

        boolean isRu = java.util.Locale.getDefault().getLanguage().equals("ru");

        // Header Title
        titleView = new TextView(context);
        titleView.setTextColor(Color.rgb(0, 255, 128));
        titleView.setTextSize(14f);
        titleView.setGravity(Gravity.CENTER);
        titleView.setText(isRu ? "Режим редактирования: нажмите на кнопку для настройки"
                               : "Edit Mode: tap a button to configure position and size");
        addView(titleView);

        // Controls row
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(HORIZONTAL);
        row.setGravity(Gravity.CENTER);

        btnSizePlus = createButton(isRu ? "Размер +" : "Size +", v -> manager.changeSelectedSize(6));
        btnSizeMinus = createButton(isRu ? "Размер -" : "Size -", v -> manager.changeSelectedSize(-6));
        btnOpacityPlus = createButton(isRu ? "Прозр. +" : "Alpha +", v -> manager.changeSelectedOpacity(0.1f));
        btnOpacityMinus = createButton(isRu ? "Прозр. -" : "Alpha -", v -> manager.changeSelectedOpacity(-0.1f));
        btnVisibility = createButton(isRu ? "Скрыть" : "Hide", v -> manager.toggleSelectedVisibility());
        btnReset = createButton(isRu ? "Сброс" : "Reset", v -> manager.resetLayout());
        btnDone = createButton(isRu ? "ГОТОВО" : "DONE", v -> manager.toggleEditMode());

        btnDone.setBackgroundColor(Color.rgb(0, 150, 80));
        btnDone.setTextColor(Color.WHITE);

        row.addView(btnSizeMinus);
        row.addView(btnSizePlus);
        row.addView(btnOpacityMinus);
        row.addView(btnOpacityPlus);
        row.addView(btnVisibility);
        row.addView(btnReset);
        row.addView(btnDone);

        addView(row);
    }

    private Button createButton(String text, OnClickListener listener) {
        Button b = new Button(getContext());
        b.setText(text);
        b.setTextSize(11f);
        b.setPadding(dpToPx(6), dpToPx(4), dpToPx(6), dpToPx(4));
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.argb(180, 50, 60, 75));
        b.setOnClickListener(listener);

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, dpToPx(38));
        lp.setMargins(dpToPx(3), dpToPx(4), dpToPx(3), dpToPx(2));
        b.setLayoutParams(lp);
        return b;
    }

    public void updateSelectedInfo(OscElement element) {
        boolean isRu = java.util.Locale.getDefault().getLanguage().equals("ru");
        if (element == null) {
            titleView.setText(isRu ? "Нажмите на любую кнопку на экране, чтобы изменить её размер или положение"
                                   : "Tap any on-screen button to modify its position or size");
            btnVisibility.setText(isRu ? "Скрыть" : "Hide");
            return;
        }

        String visText = element.visible ? (isRu ? "Скрыть" : "Hide") : (isRu ? "Показать" : "Show");
        btnVisibility.setText(visText);

        int opacityPct = (int) (element.opacity * 100);
        if (isRu) {
            titleView.setText(String.format("Выбрано: %s | Размер: %ddp | Прозрачность: %d%% | %s",
                    element.label, element.sizeDp, opacityPct, (element.visible ? "Видима" : "Скрыта")));
        } else {
            titleView.setText(String.format("Selected: %s | Size: %ddp | Opacity: %d%% | %s",
                    element.label, element.sizeDp, opacityPct, (element.visible ? "Visible" : "Hidden")));
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
