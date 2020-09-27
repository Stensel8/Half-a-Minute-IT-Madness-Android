package com.assbinc.secondsGame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {

    final int MAX_TOP = 5;
    TextView tvPoints;
    SharedPreferences sharedPreferences;
    ImageView ivHighScore;
    TextView tvHighScore, tvChosenGame, tvDifficulty, tvTop1, tvTop2, tvTop3, tvTop4, tvTop5;
    ProgressBar progressBarScore;
    TableLayout tlScore;
    Button btnGoLogin;
    SharedPref sharedPref;
    SessionManager session;
    int points;
    String difficulty, chosenGame;
    private FirebaseFirestore fireDb;
    CollectionReference collection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //check dark mode
        sharedPref = new SharedPref(this);
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);

        session = new SessionManager(this);
        fireDb = FirebaseFirestore.getInstance();

        tlScore = findViewById(R.id.tlScore);
        ivHighScore = findViewById(R.id.ivHighScore);
        tvHighScore = findViewById(R.id.tvHighScore);
        btnGoLogin = findViewById(R.id.btnGoLoginGover);
        tvTop1 = findViewById(R.id.tvTop1);
        tvTop2 = findViewById(R.id.tvTop2);
        tvTop3 = findViewById(R.id.tvTop3);
        tvTop4 = findViewById(R.id.tvTop4);
        tvTop5 = findViewById(R.id.tvTop5);
        tvPoints = findViewById(R.id.tvPoints);
        tvChosenGame = findViewById(R.id.tvChosenGame);
        tvDifficulty = findViewById(R.id.tvDifficultyGOver);
        progressBarScore = findViewById(R.id.progressBarScore);

        //displays the top 5 if the user is connected
        tlScore.setVisibility(session.checkLoggedIn()? View.VISIBLE: View.INVISIBLE);
        btnGoLogin.setVisibility(session.checkLoggedIn()? View.GONE: View.VISIBLE);
        progressBarScore.setVisibility(View.GONE);

        points = getIntent().getExtras().getInt("points");
        difficulty = getIntent().getExtras().getString("difficulty");
        chosenGame = getIntent().getExtras().getString("chosenGame");

        sharedPreferences = getSharedPreferences("pref", 0);
        int pointsHC;
        collection = fireDb.collection("highscore");

        //we get the user's high-score if he's logged-in. if not we get the saved high-score
        if(!session.isLoggedIn()){
            pointsHC = sharedPreferences.getInt("pointsHC", 0);
        }else {
            pointsHC = session.getProfileScore();
        }

        if (session.isLoggedIn()){
            progressBarScore.setVisibility(View.VISIBLE);

            collection.orderBy("score", Query.Direction.DESCENDING).whereEqualTo("chosenGame", chosenGame).whereEqualTo("difficulty", difficulty).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    progressBarScore.setVisibility(View.GONE);

                    //check if collection exists
                    if(task.getResult().size() > 0){
                        Highscore minScore = null;

                        List<String> scoreList = new ArrayList<>();
                        List<Highscore> scoreObjects = new ArrayList<>();

                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            Highscore score = documentSnapshot.toObject(Highscore.class);
                            scoreList.add(score.getUsername() + ": " + score.getScore());
                            scoreObjects.add(score);

                            if(minScore == null || minScore.getScore() > score.getScore()){
                                minScore = score;
                            }
                            Log.d("min", minScore.getScore() + "");
                        }

                        int listIndex; //the index where we're gonna add the score to the list

                        if (points != 0){
                            if(scoreObjects.size() >= 1 && points > scoreObjects.get(0).getScore()){
                                listIndex = 0;
                                addToList(scoreList, listIndex, minScore, task);
                            }else if(scoreObjects.size() >= 2 && points > scoreObjects.get(1).getScore()){
                                listIndex = 1;
                                addToList(scoreList, listIndex, minScore, task);
                            }else if(scoreObjects.size() >= 3 && points > scoreObjects.get(2).getScore()){
                                listIndex = 2;
                                addToList(scoreList, listIndex, minScore, task);
                            }else if(scoreObjects.size() >= 4 && points > scoreObjects.get(3).getScore()){
                                listIndex = 3;
                                addToList(scoreList, listIndex, minScore, task);
                            } else if(scoreObjects.size() >= 5 && points > scoreObjects.get(4).getScore()){
                                listIndex = 4;
                                addToList(scoreList, listIndex, minScore, task);
                            }
                        }

                        showTop(scoreList);
                    }else{
                        Log.d("created", "collection created");
                        List<String> scoreList = new ArrayList<>();

                        if(points > 0){
                            setHighScoreToFirestore(points, difficulty, chosenGame);
                            scoreList.add(session.getUsername() + ": " + points);
                        }
                        showTop(scoreList);
                    }
                }else {
                    Log.e("order", task.getException().toString());
                    Toast.makeText(this, "Error, could not load Top 5", Toast.LENGTH_LONG).show();
                }
            });
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(points > pointsHC){
            //saves the high-score
            pointsHC = points;
            editor.putInt("pointsHC", pointsHC);
            editor.commit();

            //update the high-score if logged-in
            if(session.isLoggedIn()){
                  session.setProfileScore(points, fireDb);
            }

            ivHighScore.setVisibility(View.VISIBLE);
        }

        tvPoints.setText(""+ points);
        tvHighScore.setText(""+ pointsHC);
    }

    private void addToList(List<String> scoreList, int listIndex, Highscore minScore, Task<QuerySnapshot> task) {
        if (task.getResult().size() >= MAX_TOP) {
            //delete last score and add new highscore
            deleteLastDocument(minScore);
        }
        setHighScoreToFirestore(points, difficulty, chosenGame);
        scoreList.add(listIndex,session.getUsername() + ": " + points);
    }


    private void showTop(List<String> list) {
        if(!list.isEmpty()){
            if(list.size() >= 1){
                tvTop1.setText(list.get(0));
            }
            if(list.size() >= 2){
                tvTop2.setText(list.get(1));
            }
            if(list.size() >= 3){
                tvTop3.setText(list.get(2));
            }
            if(list.size() >= 4){
                tvTop4.setText(list.get(3));
            }
            if(list.size() >= 5){
                tvTop5.setText(list.get(4));
            }
        }

        //we change the String values to display them in multiple languages
        if(chosenGame.equalsIgnoreCase("languageGame")){
            chosenGame = getResources().getString(R.string.languageGameButton);
        }else{
            chosenGame = getResources().getString(R.string.mathGameButton);
        }

        switch (difficulty){
            case "easy":
                difficulty = getResources().getString(R.string.difficultyEasy);
                break;
            case "medium":
                difficulty = getResources().getString(R.string.difficultyMedium);
                break;
            case "hard":
                difficulty = getResources().getString(R.string.difficultyHard);
                break;
        }
        tvChosenGame.setText(getResources().getString(R.string.chosenGame) + chosenGame);
        tvDifficulty.setText(getResources().getString(R.string.difficultyTitle) + ": " + difficulty);
    }

    private void deleteLastDocument(Highscore minScore) {
        DocumentReference doc = fireDb.collection("highscore").document(minScore.getDocId());

        doc.delete().addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                Log.d("delete", "highscore deleted");
            }else{
                Log.e("no data", "No data deleted");
            }
        });
    }

    private void setHighScoreToFirestore(int points, String difficulty, String chosenGame) {
        DocumentReference hScoreDoc = fireDb.collection("highscore").document();
        Highscore highscore = new Highscore(session.getUsername(),difficulty,chosenGame,points, hScoreDoc.getId());

        hScoreDoc.set(highscore).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                Log.d("added", "highscore added");
            }else{
                Log.e("no data", "No data inserted");
            }
        });
    }

    public void restart(View view) {
        Settings.btnAnimation(view);

        Intent intent;
        SharedPreferences sharedPreferences = getSharedPreferences("actualGame", MODE_PRIVATE);
        String actual = sharedPreferences.getString("actualGame","");

        switch (actual){
            case("math"):
                intent = new Intent(this, MathGame.class);
                startActivity(intent);
                break;
            case("NlToEn"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","NlToEn");
                startActivity(intent);
                break;
            case("EnToNl"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","EnToNl");
                startActivity(intent);
                break;
            case("FrToEn"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","FrToEn");
                startActivity(intent);
                break;
            case("EnToFr"):

                intent = new Intent(this, LanguageGame.class);
                intent.putExtra("chosenGame","EnToFr");
                startActivity(intent);
                break;
        }
        finish();
    }

    public void changeGame(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(GameOver.this, ChooseGame.class);
        startActivity(intent);
        finish();
    }

    public void main(View view) {
        Settings.btnAnimation(view);

        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        Settings.btnAnimation(view);

        finish();
    }

    public void goToLogin(View view) {
        difficulty = getIntent().getExtras().getString("difficulty");
        chosenGame = getIntent().getExtras().getString("chosenGame");

        Intent intent = new Intent(GameOver.this, MyAccount.class);
        intent.putExtra("gameover", "gameover");
        intent.putExtra("points", points);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("chosenGame", chosenGame);
        startActivity(intent);
        finish();
    }
}
