package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyAccount extends AppCompatActivity {

    SharedPref sharedPref;
    SessionManager session;
    DatabaseHelper db;
    int points;
    String difficulty;
    String chosenGame;
    TextView tvUsername;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);

        //set dark theme that we configured
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        points = getIntent().getExtras().getInt("points");
        difficulty = getIntent().getExtras().getString("difficulty");
        chosenGame = getIntent().getExtras().getString("chosenGame");

        session = new SessionManager(this);
        btnLogin = findViewById(R.id.btnLoginAccount);
        tvUsername = findViewById(R.id.tvUsernameAccount);
        btnLogin.setText(getResources().getString(session.checkLoggedIn()? R.string.logout: R.string.loginTitle));

        if(session.isLoggedIn()){
            tvUsername.setText(session.getUsername() + "");
        }

        if (!(getIntent().getStringExtra("gameover") == null) && !session.isLoggedIn()){
            showLoginDialog(getApplicationContext());
        }
    }

    public void login(View view){

        Settings.btnAnimation(view);
        if(!session.checkLoggedIn()){
            showLoginDialog(getApplicationContext());
        }else {
            session.logout();
            recreate();
        }
    }

    //shows the goMyAccount dialog
    public void showLoginDialog(final Context context){
        final android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(MyAccount.this);
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

                    if(res){
                        Toast.makeText(context,getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        session.createSession(username);

                        if ((getIntent().getStringExtra("gameover").equals("gameover"))) {
                            Intent intent = new Intent(MyAccount.this, GameOver.class);
                            intent.putExtra("gameover", "gameover");
                            intent.putExtra("points", points);
                            intent.putExtra("difficulty", difficulty);
                            intent.putExtra("chosenGame", chosenGame);
                            startActivity(intent);
                            finish();
                        }else{
                            mDialog.dismiss();
                            recreate();
                        }
                    }else{
                        Toast.makeText(context,getResources().getString(R.string.loginError), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(context, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
                }
            }
        });
        btnToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSignUpDialog(getApplicationContext());
            }
        });

    }

    //shows the sign-up dialog
    private void showSignUpDialog(final Context context){
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(MyAccount.this);
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
                                    Toast.makeText(context,getResources().getString(R.string.sign_up_succes), Toast.LENGTH_LONG).show();
                                    showLoginDialog(context);
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
            }
        });
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialogSignUp.dismiss();
            }
        });
    }

    public void addFriends(View view){

        Settings.btnAnimation(view);
        if(session.checkLoggedIn()){

        }else {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if ((getIntent().getStringExtra("gameover").equals("gameover"))) {
            Intent intent = new Intent(MyAccount.this, GameOver.class);
            intent.putExtra("gameover", "gameover");
            intent.putExtra("points", points);
            intent.putExtra("difficulty", difficulty);
            intent.putExtra("chosenGame", chosenGame);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(MyAccount.this, Settings.class);
            startActivity(intent);
            finish();
        }
    }
}
