package com.halfminute.itmadness;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public class Settings extends AppCompatActivity {

    private SharedPref sharedPref;
    private SharedPreferences sharedPreferences;
    private ImageView darkModeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme);
        sharedPref.loadLocale(this);

        setContentView(R.layout.settings);

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);

        // Initialize UI components
        darkModeIcon = findViewById(R.id.darkModeIcon);
        updateDarkModeIcon(sharedPref.loadNightMode());

        SwitchCompat darkModeToggle = findViewById(R.id.darkModeToggle);
        darkModeToggle.setChecked(sharedPref.loadNightMode());
        darkModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setNightMode(isChecked);
            updateDarkModeIcon(isChecked);
            recreate();
        });

        SwitchCompat soundToggle = findViewById(R.id.soundToggle);
        soundToggle.setChecked(sharedPref.getSound());
        soundToggle.setOnCheckedChangeListener((buttonView, isChecked) -> sharedPref.setSound(isChecked));

        Button changeLang = findViewById(R.id.btnChangeLanguage);
        changeLang.setOnClickListener(this::showChangeLanguageDialog);

        Button btnShowWelcomeAndUpdates = findViewById(R.id.btnShowWelcomeAndUpdates);
        btnShowWelcomeAndUpdates.setOnClickListener(this::showWelcomeAndUpdates);

        // Check if the app is launched for the first time
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        if (firstStart) {
            sharedPreferences.edit().putBoolean("firstStart", false).apply();
            showWelcomeAndUpdates(btnShowWelcomeAndUpdates);
        }
    }

    public void showChangeLanguageDialog(View view) {
        final String[] listOfLang = {"German", "Français", "Nederlands", "English"};
        int dialogTheme = sharedPref.loadNightMode() ? R.style.darkTheme_Dialog : R.style.lightTheme_Dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, dialogTheme);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listOfLang) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                int textColorRes = sharedPref.loadNightMode() ? android.R.color.white : android.R.color.black;
                textView.setTextColor(ContextCompat.getColor(getContext(), textColorRes));
                return view;
            }
        };

        builder.setSingleChoiceItems(adapter, -1, (dialog, i) -> {
            String languageCode = switch (i) {
                case 0 -> "de";
                case 1 -> "fr";
                case 2 -> "nl";
                default -> "en";
            };
            sharedPref.setLocale(languageCode, getApplicationContext());
            recreate();
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    public void chooseDifficulty(View view) {
        btnAnimation(view);
        SharedPreferences.Editor editor = getSharedPreferences("gameDifficulty", MODE_PRIVATE).edit();
        String difficulty = "";
        int viewId = view.getId();

        if (viewId == R.id.difficultyEasyBtn) {
            difficulty = "easy";
        } else if (viewId == R.id.difficultyMediumBtn) {
            difficulty = "medium";
        } else if (viewId == R.id.difficultyHardBtn) {
            difficulty = "hard";
        }

        editor.putString("difficulty", difficulty);
        editor.apply();
        Toast.makeText(this, getString(R.string.changeDfficultyTo) + difficulty, Toast.LENGTH_SHORT).show();
    }

    public void showWelcomeAndUpdates(View view) {
        boolean fromSettingsButton = view.getId() == R.id.btnShowWelcomeAndUpdates;
        boolean shouldShowDialog = sharedPreferences.getBoolean("firstStart", true) || fromSettingsButton;

        if (shouldShowDialog) {
            // Display welcome and update dialogs
            MainActivity.DialogUtils.showStartDialog(this);
            MainActivity.DialogUtils.showUpdateDialog(this);
            if (!fromSettingsButton) {
                sharedPreferences.edit().putBoolean("firstStart", false).apply();
            }
        }
    }

    public static void btnAnimation(View view) {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("activity", Activity.MODE_PRIVATE);
        String actual = sharedPreferences.getString("activity", "");
        Class<?> destinationClass = actual.equalsIgnoreCase("main") ? MainActivity.class : PauseMenu.class;
        startActivity(new Intent(Settings.this, destinationClass));
        finish();
    }

    private void updateDarkModeIcon(boolean isNightMode) {
        if (isNightMode) {
            darkModeIcon.setImageResource(R.drawable.dark_mode);
        } else {
            darkModeIcon.setImageResource(R.drawable.light_mode);
        }
    }
}
