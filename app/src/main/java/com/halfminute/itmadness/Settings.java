package com.halfminute.itmadness;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Button btnShowWelcomeAndUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupThemeAndLocale();
        setContentView(R.layout.settings);

        // Initialize sharedPreferences and sharedPref
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        sharedPref = new SharedPref(this);

        // Initialize UI components
        initUI();

        // Initialize click listeners
        initClickListeners();

        // Check if the app is launched for the first time
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        if (firstStart) {
            // Set firstStart to false to ensure this block doesn't execute on subsequent launches
            sharedPreferences.edit().putBoolean("firstStart", false).apply();

            // Call showWelcomeAndUpdates only on the first launch
            showWelcomeAndUpdates(btnShowWelcomeAndUpdates);
        }
    }


    private void initUI() {
        SwitchCompat darkModeToggle = findViewById(R.id.darkModeToggle);
        darkModeToggle.setChecked(sharedPref.loadNightMode());

        SwitchCompat soundToggle = findViewById(R.id.soundToggle);
        soundToggle.setChecked(sharedPref.getSound());

        Button changeLang = findViewById(R.id.btnChangeLanguage);
        btnShowWelcomeAndUpdates = findViewById(R.id.btnShowWelcomeAndUpdates);
    }

    private void initClickListeners() {
        SwitchCompat darkModeToggle = findViewById(R.id.darkModeToggle);
        darkModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setNightMode(isChecked);
            recreate();
        });

        SwitchCompat soundToggle = findViewById(R.id.soundToggle);
        soundToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setSound(isChecked);
            recreate();
        });

        Button changeLang = findViewById(R.id.btnChangeLanguage);
        changeLang.setOnClickListener(this::showChangeLanguageDialog);

        btnShowWelcomeAndUpdates.setOnClickListener(this::showWelcomeAndUpdates);
    }

    private void setupThemeAndLocale() {
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme);
        sharedPref.loadLocale(this);
    }

    public void showChangeLanguageDialog(View view) {
        final String[] listOfLang = {"German", "Français", "Nederlands", "English"};

        // Use ContextThemeWrapper to apply the correct theme
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this,
                sharedPref.loadNightMode() ? androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog :
                        androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(contextThemeWrapper);

        // Set a custom adapter with a custom layout to control text color
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contextThemeWrapper,
                android.R.layout.simple_list_item_1, android.R.id.text1, listOfLang) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = view.findViewById(android.R.id.text1);
                int textColorRes = sharedPref.loadNightMode() ? android.R.color.white : android.R.color.black;
                int textColor = ContextCompat.getColor(getContext(), textColorRes);
                textView.setTextColor(textColor); // Set text color based on dark mode

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
        dialog.show();
    }


    public void chooseDifficulty(View view) {
        btnAnimation(view);

        SharedPreferences.Editor editor = getSharedPreferences("gameDifficulty", MODE_PRIVATE).edit();
        String difficulty = "";

        int viewId = view.getId();
        if (viewId == R.id.difficultyEasyBtn) {
            editor.putString("difficulty", "easy");
            difficulty = getString(R.string.difficultyEasy);
        } else if (viewId == R.id.difficultyMediumBtn) {
            editor.putString("difficulty", "medium");
            difficulty = getString(R.string.difficultyMedium);
        } else if (viewId == R.id.difficultyHardBtn) {
            editor.putString("difficulty", "hard");
            difficulty = getString(R.string.difficultyHard);
        }

        editor.apply();
        Toast.makeText(this, getString(R.string.changeDfficultyTo) + difficulty, Toast.LENGTH_SHORT).show();
    }

    public void showWelcomeAndUpdates(View view) {
        // Check if the "Show welcome and updates" button is pressed
        boolean fromSettingsButton = view.getId() == R.id.btnShowWelcomeAndUpdates;

        // Check if it's the first start or if the "Show welcome and updates" button in settings is pressed
        boolean shouldShowDialog = sharedPreferences.getBoolean("firstStart", true) || fromSettingsButton;

        if (shouldShowDialog) {
            // Call the showWelcomeAndUpdates dialog
            MainActivity.DialogUtils.showStartDialog(this);
            MainActivity.DialogUtils.showUpdateDialog(this);

            // Update the firstStart flag only if it's not from the settings button
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
}
