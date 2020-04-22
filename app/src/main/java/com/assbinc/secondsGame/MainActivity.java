package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        //we need to make the dialog show-up when the user is not connected yet
        showLoginDialog();
    }

    //shows the login dialog
    private void showLoginDialog(){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.login_dialog, null);
        final EditText mEmail = (EditText) mView.findViewById(R.id.etEmail);
        final  EditText mPassword = (EditText) mView.findViewById(R.id.etPassword);
        final Button mToSignUp = (Button) mView.findViewById(R.id.btnToSignUp);
        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);

        mBuilder.setView(mView);
        final AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
                }
            }
        });
        mToSignUp.setOnClickListener(new View.OnClickListener() {
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
        final EditText mUsername = (EditText) mView.findViewById(R.id.etUsername);
        final EditText mEmail = (EditText) mView.findViewById(R.id.etEmailSignUp);
        final  EditText mPassword = (EditText) mView.findViewById(R.id.etPwdSignUp);
        final Button mToLogin = (Button) mView.findViewById(R.id.btnToLogin);
        Button mSignUp = (Button) mView.findViewById(R.id.btnSignUp);

        mBuilder.setView(mView);
        final AlertDialog mDialogSignUp = mBuilder.create();
        mDialogSignUp.show();

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mUsername.getText().toString().isEmpty() && !mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
                }
            }
        });
        mToLogin.setOnClickListener(new View.OnClickListener() {
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
