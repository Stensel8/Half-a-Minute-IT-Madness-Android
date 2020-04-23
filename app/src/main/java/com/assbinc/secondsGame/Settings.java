package com.assbinc.secondsGame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    SharedPref sharedPref;
    SessionManager session;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Switch darkModeToggle;
        Switch notificationToggle;

        sharedPref = new SharedPref(this);

        //set dark theme that we configured
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        super.onCreate(savedInstanceState);
        loadLocale(); //load selected language
        setContentView(R.layout.settings);

        session = new SessionManager(this);
        Button btnLogin = findViewById(R.id.btnLoginSettings);
        btnLogin.setText(getResources().getString(session.checkLoggedIn()? R.string.logout: R.string.loginTitle));

        //allow notifications or not
        notificationToggle = findViewById(R.id.notifToggle);

        notificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                }else{

                }
            }
        });
        //switch between dark and light mode
        darkModeToggle = findViewById(R.id.darkModeToggle);
        if(sharedPref.loadNightMode() == true){
            darkModeToggle.setChecked(true);
        }
        darkModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sharedPref.setNightMode(true);
                    recreate();
                }else{
                    sharedPref.setNightMode(false);
                    recreate();
                }
//                Log.d("checked", "checked: " + isChecked + " | boolean: " + isDarkMode);
            }
        });

        //change language
        Button changeLang = findViewById(R.id.btnChangeLanguage);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAnimation(v);
                showChangeLanguageDialog();
            }
        });
    }

    public void login(View view){

        btnAnimation(view);
        if(!session.checkLoggedIn()){
            showLoginDialog();
        }else {
            session.loguut();
            recreate();
        }
    }

    //shows the login dialog
    private void showLoginDialog(){
        final android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(Settings.this);
        final View mView = getLayoutInflater().inflate(R.layout.login_dialog, null);
        final EditText etUsername = (EditText) mView.findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) mView.findViewById(R.id.etPassword);
        final Button btnToSignUp = (Button) mView.findViewById(R.id.btnToSignUp);
        final Button btnLogin = (Button) mView.findViewById(R.id.btnLogin);

        db = new DatabaseHelper(this);

        mBuilder.setView(mView);
        final android.app.AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etUsername.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()){

                    String username = etUsername.getText().toString().trim();
                    String pwd = etPassword.getText().toString().trim();
                    Boolean res = db.checkUser(username, pwd);

//or
                    if(res){
                        Toast.makeText(Settings.this,getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        session.createSession(username);
                        mDialog.dismiss();
                        recreate();
                    }else{
                        Toast.makeText(Settings.this,getResources().getString(R.string.loginError), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(Settings.this, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
                }
            }
        });
        btnToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSignUpDialog();
                mDialog.dismiss();
            }
        });

    }

    //shows the sign-up dialog
    private void showSignUpDialog(){
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(Settings.this);
        View mView = getLayoutInflater().inflate(R.layout.signup_dialog, null);
        final EditText etUsername = (EditText) mView.findViewById(R.id.etUsername);
        final EditText etPwdSignUp = (EditText) mView.findViewById(R.id.etPasswordSignUp);
        final  EditText etConfPassword = (EditText) mView.findViewById(R.id.etConfirmPwd);
        final Button btnToLogin = (Button) mView.findViewById(R.id.btnToLogin);
        final Button btnSignUp = (Button) mView.findViewById(R.id.btnSignUp);

        db = new DatabaseHelper(this);

        mBuilder.setView(mView);
        final android.app.AlertDialog mDialogSignUp = mBuilder.create();
        mDialogSignUp.show();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etUsername.getText().toString().isEmpty() && !etPwdSignUp.getText().toString().isEmpty() && !etConfPassword.getText().toString().isEmpty()){

                    String username = etUsername.getText().toString().trim();
                    String pwd = etPwdSignUp.getText().toString().trim();
                    String confirmPwd = etConfPassword.getText().toString().trim();

                    if(!(username.length() >= 15)){
                        if (!db.checkMultipleUsername(username)){
                            if(pwd.equals(confirmPwd)){
                                long val = db.addUser(username,pwd);

                                if (val > 0){
                                    Toast.makeText(Settings.this,getResources().getString(R.string.sign_up_succes), Toast.LENGTH_LONG).show();
                                    showLoginDialog();
                                    mDialogSignUp.dismiss();
                                }else{
                                    Toast.makeText(Settings.this,getResources().getString(R.string.sign_up_error), Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(Settings.this,getResources().getString(R.string.same_pwd), Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(Settings.this,getResources().getString(R.string.username_exists), Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(Settings.this, getResources().getString(R.string.username_too_long), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Settings.this, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
                }
            }
        });
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoginDialog();
                mDialogSignUp.dismiss();
            }
        });
    }

    private void showChangeLanguageDialog(){
        //Array of languages to display in alert dialog
        final String[] listOfLang = {"Français", "Nederlands", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this,sharedPref.loadNightMode()? R.style.Theme_AppCompat_DayNight_Dialog: R.style.Theme_AppCompat_Light_Dialog);
        mBuilder.setSingleChoiceItems(listOfLang, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0){
                    //set french
                    setLocale("fr");
                    recreate();
                }
                if (i == 1){
                    //set french
                    setLocale("nl");
                    recreate();
                }
                if (i == 2){
                    //set french
                    setLocale("en");
                    recreate();
                }
                //dismiss dialog when language selected
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //change language
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My lang", lang);
        editor.apply();
    }

    //load saved language
    public void loadLocale(){
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My lang", "");
        setLocale(language);
    }

    public void chooseDifficulty(View view){
        btnAnimation(view);

        SharedPreferences.Editor editor = getSharedPreferences("gameDifficulty", MODE_PRIVATE).edit();
        String difficulty = "";

        switch(view.getId()){
            case (R.id.difficultyEasyBtn):
                //save data to shared preferences
                editor.putString("difficulty", "easy");
                editor.apply();
                difficulty = getResources().getString(R.string.difficultyEasy);

                break;
            case (R.id.difficultyMediumBtn):
                editor.putString("difficulty", "medium");
                editor.apply();
                difficulty = getResources().getString(R.string.difficultyMedium);

                break;
            case (R.id.difficultyHardBtn):
                editor.putString("difficulty", "hard");
                editor.apply();
                difficulty = getResources().getString(R.string.difficultyHard);

                break;
        }


        Toast.makeText(Settings.this, getResources().getString(R.string.changeDfficultyTo) + difficulty ,Toast.LENGTH_SHORT).show();
    }

    public static void btnAnimation(View view){
        //little animation when button is clicked
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        view.startAnimation(animation1);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if(getIntent().getStringExtra("activity").equalsIgnoreCase("main")){
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else if(getIntent().getStringExtra("activity").equalsIgnoreCase("pause")){
            Intent intent = new Intent(Settings.this, PauseMenu.class);
            startActivity(intent);
            finish();
        }

    }

}
