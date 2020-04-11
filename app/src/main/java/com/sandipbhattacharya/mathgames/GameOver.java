package com.sandipbhattacharya.mathgames;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {

    TextView tvPoints;
    SharedPreferences sharedPreferences;
    ImageView ivHighScore;
    TextView tvHighScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        ivHighScore = findViewById(R.id.ivHighScore);
        tvHighScore = findViewById(R.id.tvHighScore);
//        getSupportActionBar().hide();
        int points = getIntent().getExtras().getInt("points");
        tvPoints = findViewById(R.id.tvPoints);

        sharedPreferences = getSharedPreferences("pref", 0);
        int pointsSP = sharedPreferences.getInt("pointsSP", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(points > pointsSP){
            pointsSP = points;
            editor.putInt("pointsSP", pointsSP);
            editor.commit();
            ivHighScore.setVisibility(View.VISIBLE);
        }
        tvPoints.setText(""+ points);
        tvHighScore.setText(""+ pointsSP);
    }

    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, MathGame.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        finish();
    }
}
