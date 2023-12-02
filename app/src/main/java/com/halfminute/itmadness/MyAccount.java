package com.halfminute.itmadness;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyAccount extends AppCompatActivity {

    SharedPref sharedPref;
    SessionManager session;
    int points;
    String difficulty;
    String chosenGame;
    TextView tvUsername, tvAccountScore, tvTotalFriends;
    Button btnLogin;
    private final String channelId = "notificationGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);

        //set dark theme that we configured
        setTheme(sharedPref.loadNightMode() ? R.style.darkTheme : R.style.lightTheme);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);

        points = getIntent().getExtras().getInt("points");
        difficulty = getIntent().getExtras().getString("difficulty");
        chosenGame = getIntent().getExtras().getString("chosenGame");

        }

    private void displayNotification(Context context) {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.inscriptionNotification))
                .setContentText(context.getResources().getString(R.string.notificationText))
                .setTicker("Half a Minute IT Madness")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            //
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(){
        CharSequence channelName = "Half a Minute IT Madness";
        String description = getResources().getString(R.string.display_notifications);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel1 = new NotificationChannel(channelId,channelName,importance);

        channel1.setDescription(description);

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel1);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!(getIntent().getStringExtra("gameover") == null) && (getIntent().getStringExtra("gameover").equals("gameover"))) {
            Intent intent = new Intent(MyAccount.this, GameOver.class);
            intent.putExtra("gameover", "gameover");
            intent.putExtra("points", points);
            intent.putExtra("difficulty", difficulty);
            intent.putExtra("chosenGame", chosenGame);
            startActivity(intent);
            finish();
        }else if(!(getIntent().getStringExtra("main") == null) &&(getIntent().getStringExtra("main").equals("main"))){
            finish();
        }else{
            Intent intent = new Intent(MyAccount.this, Settings.class);
            startActivity(intent);
            finish();
        }
    }
}
