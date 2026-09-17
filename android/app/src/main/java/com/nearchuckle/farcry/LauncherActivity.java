package com.nearchuckle.farcry;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nearchuckle.farcry.driver.DriverHook;
import com.nearchuckle.farcry.driver.DriverInfo;
import com.nearchuckle.farcry.driver.TurnipDriverManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Main launcher activity for Far Cry (NearChuckle) on Android.
 * Provides game file configuration, Mesa Zink settings, Turnip driver management, and controls setup.
 */
public class LauncherActivity extends Activity {
    public static final String PREFS_NAME = "farcry_prefs";
    public static final String KEY_GAME_PATH = "game_path";
    public static final String KEY_USE_ZINK = "use_zink";
    public static final String KEY_RES_MODE = "resolution_mode";
    public static final String KEY_FOV = "game_fov";
    public static final String KEY_DEVMODE = "devmode";
    public static final String KEY_CUSTOM_ARGS = "custom_args";
    public static final String KEY_HIDE_CONTROLS = "hide_controls";
    public static final String KEY_MOUSE_SENSITIVITY = "mouse_sensitivity";

    private static final int REQ_CODE_ZIP = 1001;
    private static final int REQ_CODE_STORAGE_PERMISSION = 1002;

    private EditText editGamePath;
    private TextView tvGamePathStatus;
    private TextView tvGpuDetect;
    private Spinner spinnerDrivers;
    private Button btnDeleteDriver;
    private Switch switchGpuTurbo;
    private Switch switchUseZink;
    private Spinner spinnerResolution;
    private TextView tvFovLabel;
    private SeekBar seekbarFov;
    private Switch switchDevmode;
    private EditText editCustomArgs;
    private TextView tvSensLabel;
    private SeekBar seekbarSensitivity;
    private Switch switchHideControls;

    private List<DriverInfo> installedDrivers = new ArrayList<>();
    private ArrayAdapter<String> driverAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        requestStoragePermissions();

        initViews();
        loadPreferences();
        setupGpuDetection();
        setupDriverSpinner();
        setupListeners();
    }

    private void initViews() {
        editGamePath = findViewById(R.id.edit_game_path);
        tvGamePathStatus = findViewById(R.id.tv_game_path_status);
        tvGpuDetect = findViewById(R.id.tv_gpu_detect);
        spinnerDrivers = findViewById(R.id.spinner_drivers);
        btnDeleteDriver = findViewById(R.id.btn_delete_driver);
        switchGpuTurbo = findViewById(R.id.switch_gpu_turbo);
        switchUseZink = findViewById(R.id.switch_use_zink);
        spinnerResolution = findViewById(R.id.spinner_resolution);
        tvFovLabel = findViewById(R.id.tv_fov_label);
        seekbarFov = findViewById(R.id.seekbar_fov);
        switchDevmode = findViewById(R.id.switch_devmode);
        editCustomArgs = findViewById(R.id.edit_custom_args);
        tvSensLabel = findViewById(R.id.tv_sensitivity_label);
        seekbarSensitivity = findViewById(R.id.seekbar_sensitivity);
        switchHideControls = findViewById(R.id.switch_hide_controls);

        // Resolution options
        String[] resOptions = new String[]{
                "Автоматически (Разрешение экрана)",
                "1920x1080 (FHD)",
                "1280x720 (HD - Рекомендуется)",
                "960x540 (qHD - Для слабых устройств)"
        };
        ArrayAdapter<String> resAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, resOptions);
        spinnerResolution.setAdapter(resAdapter);
    }

    private void setupGpuDetection() {
        boolean isAdreno = DriverHook.isQualcommAdreno();
        if (isAdreno) {
            tvGpuDetect.setText(R.string.gpu_adreno_detected);
            tvGpuDetect.setTextColor(getColor(R.color.status_green));
            switchGpuTurbo.setEnabled(true);
        } else {
            tvGpuDetect.setText(R.string.gpu_other_detected);
            tvGpuDetect.setTextColor(getColor(R.color.text_secondary));
            switchGpuTurbo.setEnabled(false);
        }
    }

    private void setupDriverSpinner() {
        installedDrivers = TurnipDriverManager.getInstalledDrivers(this);
        List<String> names = new ArrayList<>();
        int selectedIndex = 0;
        DriverInfo selected = TurnipDriverManager.getSelectedDriver(this);

        for (int i = 0; i < installedDrivers.size(); i++) {
            DriverInfo d = installedDrivers.get(i);
            names.add(d.toString());
            if (d.getId().equals(selected.getId())) {
                selectedIndex = i;
            }
        }

        driverAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, names);
        spinnerDrivers.setAdapter(driverAdapter);
        spinnerDrivers.setSelection(selectedIndex);

        btnDeleteDriver.setEnabled(!selected.isSystem());
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Default Far Cry path guess
        String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FarCry";
        String path = prefs.getString(KEY_GAME_PATH, defaultPath);
        editGamePath.setText(path);
        validateGamePath(path);

        switchUseZink.setChecked(prefs.getBoolean(KEY_USE_ZINK, true));
        spinnerResolution.setSelection(prefs.getInt(KEY_RES_MODE, 0));

        int fov = prefs.getInt(KEY_FOV, 90);
        seekbarFov.setProgress(Math.max(0, Math.min(50, fov - 70)));
        tvFovLabel.setText("Поле зрения (FOV): " + fov + "°");

        switchDevmode.setChecked(prefs.getBoolean(KEY_DEVMODE, false));
        editCustomArgs.setText(prefs.getString(KEY_CUSTOM_ARGS, ""));

        switchGpuTurbo.setChecked(TurnipDriverManager.isTurboEnabled(this));

        float sens = prefs.getFloat(KEY_MOUSE_SENSITIVITY, 1.0f);
        int sensProgress = Math.round((sens - 0.5f) * 10f);
        seekbarSensitivity.setProgress(Math.max(0, Math.min(25, sensProgress)));
        tvSensLabel.setText(String.format("Чувствительность: %.1fx", sens));

        switchHideControls.setChecked(prefs.getBoolean(KEY_HIDE_CONTROLS, false));
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_GAME_PATH, editGamePath.getText().toString().trim());
        editor.putBoolean(KEY_USE_ZINK, switchUseZink.isChecked());
        editor.putInt(KEY_RES_MODE, spinnerResolution.getSelectedItemPosition());

        int fov = seekbarFov.getProgress() + 70;
        editor.putInt(KEY_FOV, fov);
        editor.putBoolean(KEY_DEVMODE, switchDevmode.isChecked());
        editor.putString(KEY_CUSTOM_ARGS, editCustomArgs.getText().toString().trim());

        float sens = 0.5f + (seekbarSensitivity.getProgress() / 10.0f);
        editor.putFloat(KEY_MOUSE_SENSITIVITY, sens);
        editor.putBoolean(KEY_HIDE_CONTROLS, switchHideControls.isChecked());
        editor.apply();

        TurnipDriverManager.setTurboEnabled(this, switchGpuTurbo.isChecked());
    }

    private void setupListeners() {
        // Path input change validation
        editGamePath.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                validateGamePath(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Folder browse button
        findViewById(R.id.btn_browse_folder).setOnClickListener(v -> showFolderPickerDialog());

        // Driver selection
        spinnerDrivers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < installedDrivers.size()) {
                    DriverInfo chosen = installedDrivers.get(position);
                    TurnipDriverManager.setSelectedDriver(LauncherActivity.this, chosen.getId());
                    btnDeleteDriver.setEnabled(!chosen.isSystem());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Install Driver ZIP button
        findViewById(R.id.btn_install_driver_zip).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/zip");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent, "Выберите ZIP архив Turnip драйвера"), REQ_CODE_ZIP);
            } catch (Exception e) {
                Toast.makeText(this, "Файловый менеджер не найден", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete Driver button
        btnDeleteDriver.setOnClickListener(v -> {
            DriverInfo sel = TurnipDriverManager.getSelectedDriver(this);
            if (sel.isSystem()) return;

            new AlertDialog.Builder(this)
                    .setTitle(R.string.btn_delete_driver)
                    .setMessage("Удалить драйвер " + sel.getName() + "?")
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        TurnipDriverManager.deleteDriver(this, sel.getId());
                        setupDriverSpinner();
                        Toast.makeText(this, "Драйвер удален", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });

        // FOV seekbar
        seekbarFov.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int fov = progress + 70;
                tvFovLabel.setText("Поле зрения (FOV): " + fov + "°");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Sensitivity seekbar
        seekbarSensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float sens = 0.5f + (progress / 10.0f);
                tvSensLabel.setText(String.format("Чувствительность: %.1fx", sens));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Configure Controls button (opens fullscreen touch editor)
        findViewById(R.id.btn_configure_controls).setOnClickListener(v -> {
            savePreferences();
            Intent intent = new Intent(this, ConfigureControlsActivity.class);
            startActivity(intent);
        });

        // Launch Game Button
        findViewById(R.id.btn_launch_game).setOnClickListener(v -> launchGame());
    }

    private void validateGamePath(String path) {
        if (path == null || path.isEmpty()) {
            tvGamePathStatus.setText(R.string.status_files_missing);
            tvGamePathStatus.setTextColor(getColor(R.color.status_red));
            return;
        }

        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) {
            tvGamePathStatus.setText("⚠ Папка не существует");
            tvGamePathStatus.setTextColor(getColor(R.color.status_red));
            return;
        }

        // Check for FCData or Levels or pak files
        File fcData = new File(folder, "FCData");
        File levels = new File(folder, "Levels");
        boolean hasFcData = fcData.exists() && fcData.isDirectory();
        boolean hasLevels = levels.exists() && levels.isDirectory();

        if (hasFcData || hasLevels) {
            tvGamePathStatus.setText(R.string.status_files_found);
            tvGamePathStatus.setTextColor(getColor(R.color.status_green));
        } else {
            tvGamePathStatus.setText(R.string.status_files_missing);
            tvGamePathStatus.setTextColor(getColor(R.color.status_orange));
        }
    }

    private void showFolderPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_folder_picker, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        TextView tvCurrent = dialogView.findViewById(R.id.tv_picker_current_path);
        ListView lvItems = dialogView.findViewById(R.id.lv_folder_items);
        Button btnSelect = dialogView.findViewById(R.id.btn_picker_select);
        Button btnCancel = dialogView.findViewById(R.id.btn_picker_cancel);

        final File[] currentDir = new File[]{Environment.getExternalStorageDirectory()};
        final List<String> itemNames = new ArrayList<>();
        final List<File> itemFiles = new ArrayList<>();

        Runnable updateList = () -> {
            tvCurrent.setText(currentDir[0].getAbsolutePath());
            itemNames.clear();
            itemFiles.clear();

            if (currentDir[0].getParentFile() != null) {
                itemNames.add(".. (На уровень выше)");
                itemFiles.add(currentDir[0].getParentFile());
            }

            File[] files = currentDir[0].listFiles(File::isDirectory);
            if (files != null) {
                Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                for (File f : files) {
                    itemNames.add(f.getName());
                    itemFiles.add(f);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, itemNames);
            lvItems.setAdapter(adapter);
        };

        updateList.run();

        lvItems.setOnItemClickListener((parent, view, position, id) -> {
            if (position < itemFiles.size()) {
                currentDir[0] = itemFiles.get(position);
                updateList.run();
            }
        });

        btnSelect.setOnClickListener(v -> {
            editGamePath.setText(currentDir[0].getAbsolutePath());
            validateGamePath(currentDir[0].getAbsolutePath());
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_ZIP && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                installDriverZip(uri);
            }
        }
    }

    private void installDriverZip(Uri uri) {
        String fileName = "Turnip_Driver.zip";
        try {
            android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        } catch (Exception ignored) {}

        Toast.makeText(this, "Установка драйвера Turnip из " + fileName + "...", Toast.LENGTH_SHORT).show();

        try {
            DriverInfo installed = TurnipDriverManager.installDriverFromUri(this, uri, fileName);
            TurnipDriverManager.setSelectedDriver(this, installed.getId());
            setupDriverSpinner();

            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_driver_installed)
                    .setMessage(String.format("Драйвер '%s' успешно распакован и готов к использованию с Mesa Zink!",
                            installed.getName()))
                    .setPositiveButton(R.string.ok, null)
                    .show();
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("Ошибка установки")
                    .setMessage(getString(R.string.dialog_driver_install_failed) + e.getMessage())
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }

    private boolean checkGameFilesExist(String path) {
        if (path == null || path.isEmpty()) return false;
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) return false;
        File fcData = new File(folder, "FCData");
        File levels = new File(folder, "Levels");
        if ((fcData.exists() && fcData.isDirectory()) || (levels.exists() && levels.isDirectory())) {
            return true;
        }
        File[] paks = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pak"));
        return paks != null && paks.length > 0;
    }

    private void launchGame() {
        savePreferences();
        String gamePath = editGamePath.getText().toString().trim();
        File f = new File(gamePath);
        if (!f.exists()) {
            new AlertDialog.Builder(this)
                    .setTitle("Папка не найдена")
                    .setMessage("Папка с файлами Far Cry (" + gamePath + ") не существует. Выберите правильную папку с установленной игрой Far Cry.")
                    .setPositiveButton(R.string.ok, null)
                    .show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            new AlertDialog.Builder(this)
                    .setTitle("Доступ к файлам игры")
                    .setMessage("Для чтения файлов игры Far Cry из памяти устройства требуется предоставить разрешение 'Доступ ко всем файлам'.")
                    .setPositiveButton("Предоставить", (dialog, which) -> {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        } catch (Exception e) {
                            startGameActivity();
                        }
                    })
                    .setNegativeButton("Продолжить", (dialog, which) -> startGameActivity())
                    .show();
            return;
        }

        if (!checkGameFilesExist(gamePath)) {
            new AlertDialog.Builder(this)
                    .setTitle("Файлы игры не обнаружены")
                    .setMessage("В папке:\n" + gamePath + "\n\nне найдены файлы игры Far Cry (папка FCData, Levels или файлы *.pak).\n\nСкопируйте файлы из оригинальной игры Far Cry (версия для ПК) в эту папку.\n\nПопробовать запустить все равно?")
                    .setPositiveButton("Запустить", (dialog, which) -> startGameActivity())
                    .setNegativeButton("Отмена", null)
                    .show();
            return;
        }

        startGameActivity();
    }

    private void startGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception ignored) {}
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQ_CODE_STORAGE_PERMISSION);
            }
        }
    }
}
