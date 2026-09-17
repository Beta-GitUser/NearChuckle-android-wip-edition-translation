package com.nearchuckle.farcry;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity presenting the Far Cry crash report / diagnostics log to the user.
 * Enables one-tap copying of the entire report to the clipboard for sharing with developers.
 */
public class CrashReportActivity extends Activity {
    public static final String EXTRA_CRASH_REPORT = "extra_crash_report";

    private TextView tvSummary;
    private TextView tvLogText;
    private Button btnCopy;
    private Button btnShare;
    private Button btnClear;
    private Button btnClose;

    private String currentReportText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_report);

        initViews();
        loadReport();
        setupListeners();

        // Mark unread crash as viewed
        CrashHandler.markCrashAsRead(this);
    }

    private void initViews() {
        tvSummary = findViewById(R.id.tv_crash_summary);
        tvLogText = findViewById(R.id.tv_crash_log_text);
        btnCopy = findViewById(R.id.btn_copy_report);
        btnShare = findViewById(R.id.btn_share_report);
        btnClear = findViewById(R.id.btn_clear_report);
        btnClose = findViewById(R.id.btn_close_report);
    }

    private void loadReport() {
        String report = getIntent().getStringExtra(EXTRA_CRASH_REPORT);
        if (report == null || report.trim().isEmpty()) {
            report = CrashHandler.getLastCrashReport(this);
        }
        if (report == null || report.trim().isEmpty()) {
            // Generate live diagnostics if no crash report was recorded
            report = CrashHandler.generateDiagnosticsReport(this);
            tvSummary.setText("Диагностические данные и логи работы движка Far Cry");
        } else if (!report.contains("CRYENGINE LOG")) {
            // Signal handler generated raw crash dump: enrich with system specs, engine log tail, and logcat
            report = CrashHandler.buildCrashReport(this, "FAR CRY ANDROID NATIVE CRASH", report);
            CrashHandler.saveCrashReport(this, report);
            tvSummary.setText("Нажмите 'Скопировать весь отчет', чтобы отправить его для анализа");
        } else {
            tvSummary.setText("Нажмите 'Скопировать весь отчет', чтобы отправить его для анализа");
        }

        currentReportText = report;
        tvLogText.setText(report);
    }

    private void setupListeners() {
        btnCopy.setOnClickListener(v -> copyReportToClipboard());

        btnShare.setOnClickListener(v -> {
            if (currentReportText.isEmpty()) {
                Toast.makeText(this, R.string.toast_no_logs_to_copy, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Far Cry Android Crash Report");
            shareIntent.putExtra(Intent.EXTRA_TEXT, currentReportText);
            startActivity(Intent.createChooser(shareIntent, "Отправить отчет через..."));
        });

        btnClear.setOnClickListener(v -> {
            CrashHandler.clearCrashReport(this);
            currentReportText = "";
            tvLogText.setText("(Отчет об ошибке очищен)");
            Toast.makeText(this, "Отчет очищен", Toast.LENGTH_SHORT).show();
        });

        btnClose.setOnClickListener(v -> finish());
    }

    private void copyReportToClipboard() {
        if (currentReportText.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_logs_to_copy, Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Far Cry Crash Report", currentReportText);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.toast_report_copied, Toast.LENGTH_LONG).show();
        }
    }
}
