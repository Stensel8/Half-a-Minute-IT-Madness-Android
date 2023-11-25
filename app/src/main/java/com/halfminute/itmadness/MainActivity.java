package com.halfminute.itmadness;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.halfminute.itmadness.R;


public class MainActivity extends AppCompatActivity {

    SharedPref sharedPref;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        sharedPreferences.edit().remove("updates").apply();
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        boolean updateDialog = sharedPreferences.getBoolean("update2", true);

        if (firstStart)
            showStartDialog();
        if (updateDialog){
            showUpdateDialog();
        }

    }

    private void showUpdateDialog() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mViewUpdate = getLayoutInflater().inflate(R.layout.updates_msg, null);
        final ImageButton btnCloseDialog = (ImageButton) mViewUpdate.findViewById(R.id.btnCloseUpdate);

        mBuilder.setView(mViewUpdate);
        final AlertDialog mDialogUpdate = mBuilder.create();
        mDialogUpdate.show();

        btnCloseDialog.setOnClickListener(vUpdate -> mDialogUpdate.dismiss());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("update2", false);
        editor.apply();
    }

    private void showStartDialog() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.welcome_message, null);
        final Button btnToSignUp = (Button) mView.findViewById(R.id.btnSignUpWelcome);
        final ImageButton btnClose = (ImageButton) mView.findViewById(R.id.btnClose);

        mBuilder.setView(mView);
        final AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        btnToSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyAccount.class);
            intent.putExtra("main","main");
            startActivity(intent);
            finish();
        });

        btnClose.setOnClickListener(v -> mDialog.dismiss());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }


    public void chooseGamePage(View view) {
        Settings.btnAnimation(view);
        Intent intent = new Intent(MainActivity.this, ChooseGame.class);
        startActivity(intent);
        finish();
    }

    public void goSettings(View view) {
        Settings.btnAnimation(view);

        SharedPreferences sharedPreferences = getSharedPreferences("activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("activity", "main");
        editor.apply();
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
        finish();
    }

}
