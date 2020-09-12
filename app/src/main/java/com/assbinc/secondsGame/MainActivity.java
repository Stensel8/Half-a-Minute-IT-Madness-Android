package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SharedPref sharedPref;
    SharedPreferences sharedPreferences;
    DatabaseHelper db;
    SessionManager session;
    private final String channelId = "notificationGame";
    private final int notificationId = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        session = new SessionManager(this);
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);

        if (firstStart)
            showStartDialog();

        //Ad
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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
            if (!session.checkLoggedIn()){ //to prevent errors
                showSignUpDialog(getApplicationContext());
                mDialog.dismiss();
            }
        });

        btnClose.setOnClickListener(v -> mDialog.dismiss());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    //shows the login dialog
    private void showLoginDialog(final Context context){
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.login_dialog, null);
        final EditText etUsername = (EditText) mView.findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) mView.findViewById(R.id.etPassword);
        final Button btnToSignUp = (Button) mView.findViewById(R.id.btnToSignUp);
        final Button btnLogin = (Button) mView.findViewById(R.id.btnLogin);
        final ImageButton btnCloseLogin = (ImageButton) mView.findViewById(R.id.btnClose2);

        db = new DatabaseHelper(this);

        mBuilder.setView(mView);
        final AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        btnLogin.setOnClickListener(v -> {
            if (!etUsername.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()){

                String username = etUsername.getText().toString().trim();
                String pwd = etPassword.getText().toString().trim();
                Boolean res = db.checkUser(username, pwd);

//or
                if(res){
                    Toast.makeText(context,getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    session.createSession(username);
                    mDialog.dismiss();
                }else{
                    Toast.makeText(context,getResources().getString(R.string.loginError), Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(context, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
            }
        });
        btnToSignUp.setOnClickListener(v -> mDialog.dismiss());

        btnCloseLogin.setOnClickListener(v -> mDialog.dismiss());
    }

    //shows the sign-up dialog
    private void showSignUpDialog(final Context context){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.signup_dialog, null);
        final EditText etUsername = (EditText) mView.findViewById(R.id.etUsername);
        final EditText etPwdSignUp = (EditText) mView.findViewById(R.id.etPasswordSignUp);
        final  EditText etConfPassword = (EditText) mView.findViewById(R.id.etConfirmPwd);
        final Button btnToLogin = (Button) mView.findViewById(R.id.btnToLogin);
        final Button btnSignUp = (Button) mView.findViewById(R.id.btnSignUp);
        final ImageButton btnCloseSignUp = (ImageButton) mView.findViewById(R.id.btnClose3);

        db = new DatabaseHelper(this);

        mBuilder.setView(mView);
        final AlertDialog mDialogSignUp = mBuilder.create();
        mDialogSignUp.show();

        btnSignUp.setOnClickListener(v -> {

            if (!etUsername.getText().toString().isEmpty() && !etPwdSignUp.getText().toString().isEmpty() && !etConfPassword.getText().toString().isEmpty()){

                String username = etUsername.getText().toString().trim();
                String pwd = etPwdSignUp.getText().toString().trim();
                String confirmPwd = etConfPassword.getText().toString().trim();

                if(!(username.length() >= 15)){
                    if (!db.checkMultipleUsername(username)){
                        if(pwd.equals(confirmPwd)){
                            long val = db.addUser(username,pwd);

                            if (val > 0){
                                Toast.makeText(context,getResources().getString(R.string.sign_up_succes), Toast.LENGTH_LONG).show();
                                displayNotification(getApplicationContext());

                                //login automatically
                                Toast.makeText(context,getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                session.createSession(username);
                                mDialogSignUp.dismiss();

                            }else{
                                Toast.makeText(context,getResources().getString(R.string.sign_up_error), Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(context,getResources().getString(R.string.same_pwd), Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context,getResources().getString(R.string.username_exists), Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(context, getResources().getString(R.string.username_too_long), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(context, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
            }
        });
        btnToLogin.setOnClickListener(v -> showLoginDialog(getApplicationContext()));

        btnCloseSignUp.setOnClickListener(v -> mDialogSignUp.dismiss());

    }

    private void displayNotification(Context context) {

        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.inscriptionNotification))
                .setContentText(context.getResources().getString(R.string.notificationText))
                .setTicker("THE 30 seconds game")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "THE 30 Seconds game";
            String description = getResources().getString(R.string.display_notifications);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel1 = new NotificationChannel(channelId,channelName,importance);

            channel1.setDescription(description);

            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel1);

        }
    }



    //load saved language
    public void loadLocale(){
        SharedPreferences pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("My lang", "");
        setLocale(language);
    }

    //set saved language
    private void setLocale(String lang) {
        Locale locale;
        if(lang.equals("")){ //if there's no saved language
            locale = new Locale(Locale.getDefault().getLanguage()); //get default language of the device
        }else{
            locale = new Locale(lang);
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
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
        editor.commit();
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
        finish();
    }

}
