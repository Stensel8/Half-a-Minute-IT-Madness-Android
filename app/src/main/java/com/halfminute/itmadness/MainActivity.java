package com.halfminute.itmadness;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupThemeAndLocale();
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);

        // Check if the update dialog has been shown
        boolean updateDialogShown = sharedPreferences.getBoolean("updateDialogShown", false);
        if (!updateDialogShown) {
            DialogUtils.showUpdateDialog(this);

            // Set the preference to true to indicate that the update dialog has been shown
            sharedPreferences.edit().putBoolean("updateDialogShown", true).apply();
        }

        // Check if it's the first start
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        if (firstStart) {
            DialogUtils.showStartDialog(this);

            // Set the preference to false to indicate that the start dialog has been shown
            sharedPreferences.edit().putBoolean("firstStart", false).apply();
        }
    }

    private void setupThemeAndLocale() {
        SharedPref sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme);
        sharedPref.loadLocale(this);
    }

    public static class DialogUtils {
        public static void showUpdateDialog(Activity activity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View viewUpdate = activity.getLayoutInflater().inflate(R.layout.updates_msg, null);
            ImageButton btnCloseDialog = viewUpdate.findViewById(R.id.btnCloseUpdate);
            TextView textViewUpdate = viewUpdate.findViewById(R.id.textViewUpdate);

            builder.setView(viewUpdate);
            AlertDialog dialogUpdate = builder.create();
            dialogUpdate.show();

            btnCloseDialog.setOnClickListener(vUpdate -> dialogUpdate.dismiss());

            String patchNotes = activity.getString(R.string.patch_notes);
            Spanned spannedPatchNotes = Html.fromHtml(patchNotes, Html.FROM_HTML_MODE_LEGACY);
            textViewUpdate.setText(spannedPatchNotes);

            SharedPreferences.Editor editor = activity.getSharedPreferences("updatePreferences", Context.MODE_PRIVATE).edit();
            editor.putBoolean("update2", false);
            editor.apply();
        }

        public static void showStartDialog(Activity activity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View view = activity.getLayoutInflater().inflate(R.layout.welcome_message, null);
            ImageButton btnClose = view.findViewById(R.id.btnClose);

            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.show();

            btnClose.setOnClickListener(v -> dialog.dismiss());

            SharedPreferences.Editor editor = activity.getSharedPreferences("welcomePreferences", Context.MODE_PRIVATE).edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }
    }

    public void chooseGamePage(View view) {
        Settings.btnAnimation(view);
        startActivity(new Intent(MainActivity.this, ChooseGame.class));
        finish();
    }

    public void goSettings(View view) {
        Settings.btnAnimation(view);

        SharedPreferences.Editor editor = getSharedPreferences("activity", MODE_PRIVATE).edit();
        editor.putString("activity", "main");
        editor.apply();

        startActivity(new Intent(MainActivity.this, Settings.class));
        finish();
    }
}
