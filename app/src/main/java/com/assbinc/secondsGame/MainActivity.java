package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SharedPref sharedPref;
    DatabaseHelper db;
    SessionManager session;
    String className;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        session = new SessionManager(this);
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);
        className = this.getClass().getName().replace("$",".").split("\\.")[3];

        //dialog shows-up when the user is not connected yet
        if (!session.checkLoggedIn()){
            showLoginDialog();
        }
    }

    //shows the login dialog
    private void showLoginDialog(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.login_dialog, null);
        final EditText etUsername = (EditText) mView.findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) mView.findViewById(R.id.etPassword);
        final Button btnToSignUp = (Button) mView.findViewById(R.id.btnToSignUp);
        final Button btnLogin = (Button) mView.findViewById(R.id.btnLogin);

        db = new DatabaseHelper(this);

        mBuilder.setView(mView);
        final AlertDialog mDialog = mBuilder.create();
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
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        session.createSession(username);
                        mDialog.dismiss();
                    }else{
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.loginError), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.signup_dialog, null);
        final EditText etUsername = (EditText) mView.findViewById(R.id.etUsername);
        final EditText etPwdSignUp = (EditText) mView.findViewById(R.id.etPasswordSignUp);
        final  EditText etConfPassword = (EditText) mView.findViewById(R.id.etConfirmPwd);
        final Button btnToLogin = (Button) mView.findViewById(R.id.btnToLogin);
        final Button btnSignUp = (Button) mView.findViewById(R.id.btnSignUp);

        db = new DatabaseHelper(this);

        mBuilder.setView(mView);
        final AlertDialog mDialogSignUp = mBuilder.create();
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
                                    Toast.makeText(MainActivity.this,getResources().getString(R.string.sign_up_succes), Toast.LENGTH_LONG).show();
                                    showLoginDialog();
                                    mDialogSignUp.dismiss();
                                }else{
                                    Toast.makeText(MainActivity.this,getResources().getString(R.string.sign_up_error), Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(MainActivity.this,getResources().getString(R.string.same_pwd), Toast.LENGTH_LONG).show();
                            }
                        }else {
                            Toast.makeText(MainActivity.this,getResources().getString(R.string.username_exists), Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.username_too_long), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
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

    //set saved language
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    //load saved language
    public void loadLocale(){
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My lang", "");
        setLocale(language);
    }

    public void chooseGamePage(View view) {
        Settings.btnAnimation(view);
        Intent intent = new Intent(MainActivity.this, ChooseGame.class);
        startActivity(intent);
        finish();
    }

    public void goSettings(View view) {
        Settings.btnAnimation(view);
        Intent intent = new Intent(MainActivity.this, Settings.class);
        intent.putExtra("activity","main");
        startActivity(intent);
        finish();
    }

}
