package com.halfminute.itmadness;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.halfminute.itmadness.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class Settings extends AppCompatActivity {

    SharedPref sharedPref;
    SessionManager session;
    private FirebaseFirestore fireDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Switch darkModeToggle;
        Switch notificationToggle;
        Switch soundToggle;

        sharedPref = new SharedPref(this);

        //set dark theme that we configured
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        //load selected language
        sharedPref.loadLocale(this);
        session = new SessionManager(this);
        fireDb = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        ImageView imgIdea = (ImageView) findViewById(R.id.imgIdea);
        imgIdea.setImageResource(sharedPref.loadNightMode()? R.drawable.idea_box_white: R.drawable.idea_box);


        //switch between dark and light mode
        darkModeToggle = findViewById(R.id.darkModeToggle);

        if(sharedPref.loadNightMode()){
            darkModeToggle.setChecked(true);
        }
        darkModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                sharedPref.setNightMode(true);
                recreate();
            }else{
                sharedPref.setNightMode(false);
                recreate();
            }
        });

        //sound toggle
        soundToggle = findViewById(R.id.soundToggle);

        if(sharedPref.getSound()){
            soundToggle.setChecked(true);
        }
        soundToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                sharedPref.setSound(true);
                recreate();
            }else{
                sharedPref.setSound(false);
                recreate();
            }
        });

        //change language
        Button changeLang = findViewById(R.id.btnChangeLanguage);
        changeLang.setOnClickListener(v -> {
            btnAnimation(v);
            showChangeLanguageDialog(v);
        });
    }

    public void goMyAccount(View view){

        btnAnimation(view);
        Intent intent = new Intent(Settings.this, MyAccount.class);
        intent.putExtra("gameover", "settings");
        startActivity(intent);
        finish();
    }



    public void showChangeLanguageDialog(View view){
        //Array of languages to display in alert dialog
        final String[] listOfLang = {"Français", "Nederlands", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this,sharedPref.loadNightMode()? androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog: androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog);
        mBuilder.setSingleChoiceItems(listOfLang, -1, (dialog, i) -> {
            if (i == 0){
                //set french
                sharedPref.setLocale("fr", getApplicationContext());
                recreate();
            }
            if (i == 1){
                //set dutch
                sharedPref.setLocale("nl", getApplicationContext());
                recreate();
            }
            if (i == 2){
                //set english
                sharedPref.setLocale("en", getApplicationContext());
                recreate();
            }
            //dismiss dialog when language selected
            dialog.dismiss();
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void chooseDifficulty(View view){
        btnAnimation(view);

        SharedPreferences.Editor editor = getSharedPreferences("gameDifficulty", MODE_PRIVATE).edit();
        String difficulty = "";

        switch (view.getId()) {
            case (R.id.difficultyEasyBtn) -> {
                //save data to shared preferences
                editor.putString("difficulty", "easy");
                editor.apply();
                difficulty = getResources().getString(R.string.difficultyEasy);
            }
            case (R.id.difficultyMediumBtn) -> {
                editor.putString("difficulty", "medium");
                editor.apply();
                difficulty = getResources().getString(R.string.difficultyMedium);
            }
            case (R.id.difficultyHardBtn) -> {
                editor.putString("difficulty", "hard");
                editor.apply();
                difficulty = getResources().getString(R.string.difficultyHard);
            }
        }


        Toast.makeText(Settings.this, getResources().getString(R.string.changeDfficultyTo) + difficulty ,Toast.LENGTH_SHORT).show();
    }

    public void suggestionBox(View view){
        btnAnimation(view);

        if(session.isLoggedIn()){
            final android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(Settings.this);
            String username = session.getUsername();
            String userId = session.getUid();

            if(username.equals("nassimassb")){
                final View mViewShowSuggestions = getLayoutInflater().inflate(R.layout.add_friend_list_dialog, null);
                final ListView lvSuggestions = (ListView) mViewShowSuggestions.findViewById(R.id.lvFriends);
                final TextView title = (TextView) mViewShowSuggestions.findViewById(R.id.tvAddFriends2);
                final ImageButton btnClose = (ImageButton) mViewShowSuggestions.findViewById(R.id.btnClose5);
                final ProgressBar progressBar = (ProgressBar) mViewShowSuggestions.findViewById(R.id.progressBarFriends);

                mBuilder.setView(mViewShowSuggestions);
                final android.app.AlertDialog mDialogShowSuggestion = mBuilder.create();
                mDialogShowSuggestion.show();

                title.setText("Suggestions");

                ArrayList<String> suggestionList = new ArrayList<>();
                progressBar.setVisibility(View.VISIBLE);

                //show suggestions only when logged with admin account
                fireDb.collection("suggestions").get().addOnCompleteListener(t ->{
                    if(t.isSuccessful()){
                        for (DocumentSnapshot doc : t.getResult()){
                            progressBar.setVisibility(View.GONE);

                            suggestionList.add("\n-" + doc.get("username") + "-\n\n\"" + doc.get("idea") + "\"");
                        }
                            ListAdapter listAdapter = new ArrayAdapter<>(this,R.layout.listrow, suggestionList);
                            lvSuggestions.setAdapter(listAdapter);
                    }else{
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(this, Objects.requireNonNull(t.getException()).toString(), Toast.LENGTH_LONG).show();
                    }
                });

                btnClose.setOnClickListener(v -> { mDialogShowSuggestion.dismiss(); });
            }else{
                final View mViewSuggestion = getLayoutInflater().inflate(R.layout.suggestion_box_layout, null);
                final ImageButton btnCloseDialog = (ImageButton) mViewSuggestion.findViewById(R.id.btnCloseIdea);
                final EditText etSuggestion = (EditText) mViewSuggestion.findViewById(R.id.etSuggestion);
                final Button btnConfirmIdea = (Button) mViewSuggestion.findViewById(R.id.btnConfirmIdea);

                mBuilder.setView(mViewSuggestion);
                final android.app.AlertDialog mDialogSuggestion = mBuilder.create();
                mDialogSuggestion.show();

                btnCloseDialog.setOnClickListener(vUpdate -> mDialogSuggestion.dismiss());
                btnConfirmIdea.setOnClickListener(vConfirm -> {

                    //switch between true and false
                    sharedPref.setAdShown(!sharedPref.adShown());

                    String suggestionText = etSuggestion.getText().toString();

                    if(!suggestionText.isEmpty()){
                        Suggestions suggestion = new Suggestions(username, userId ,suggestionText);

                        fireDb.collection("suggestions").document().set(suggestion).addOnCompleteListener(task -> {
                            mDialogSuggestion.dismiss();
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.thanks_idea),Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }

        }else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.not_connected_friends),Toast.LENGTH_SHORT).show();
        }
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

        SharedPreferences sharedPreferences = getSharedPreferences("activity", Activity.MODE_PRIVATE);
        String actual = sharedPreferences.getString("activity","");

        if(actual.equalsIgnoreCase("main")){
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
        }else if(actual.equalsIgnoreCase("pause")){
            Intent intent = new Intent(Settings.this, PauseMenu.class);
            startActivity(intent);
        }
        finish();
    }

}
