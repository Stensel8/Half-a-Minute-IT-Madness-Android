package com.assbinc.secondsGame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyAccount extends AppCompatActivity {

    SharedPref sharedPref;
    SessionManager session;
    int points;
    String difficulty;
    String chosenGame;
    TextView tvUsername, tvAccountScore, tvTotalFriends;
    Button btnLogin;
    private final String channelId = "notificationGame";
    private final int notificationId = 001;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);

        //set dark theme that we configured
        setTheme(sharedPref.loadNightMode()? R.style.darkTheme: R.style.lightTheme);
        sharedPref.loadLocale(this); //loads the saved language

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        points = getIntent().getExtras().getInt("points");
        difficulty = getIntent().getExtras().getString("difficulty");
        chosenGame = getIntent().getExtras().getString("chosenGame");

        session = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();
        fireDb = FirebaseFirestore.getInstance();

        btnLogin = findViewById(R.id.btnLoginAccount);
        tvUsername = findViewById(R.id.tvUsernameAccount);
        tvAccountScore = findViewById(R.id.tvhScoreAccount);
        tvTotalFriends = findViewById(R.id.tvTotalFriends);
        btnLogin.setText(getResources().getString(session.checkLoggedIn()? R.string.logout: R.string.loginTitle));

        //change the user's info if logged-in
        if(session.isLoggedIn()){
            tvUsername.setText(session.getUsername() + "");
            tvAccountScore.setText(session.getProfileScore() + "");
            tvTotalFriends.setText(session.getNbFriends() + "");
        }

        //if the previous activity was the GameOver activity
        if (!(getIntent().getStringExtra("gameover") == null) && !session.isLoggedIn()){
            showLoginDialog(getApplicationContext());
        }else if (!(getIntent().getStringExtra("main") == null) && !session.isLoggedIn()){ //if the previous activity was the MainActivity
            showSignUpDialog(getApplicationContext());
        }
    }

    public void login(View view){

        Settings.btnAnimation(view);
        if(!session.checkLoggedIn()){
            showLoginDialog(getApplicationContext());
        }else {
            showLogoutDialog();
        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyAccount.this);
        builder.setMessage(getResources().getString(R.string.logoutMsg)).setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
            session.logout();
            //firebase logout
            mAuth.signOut();
            dialog.dismiss();
            recreate();
        }).setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //shows the login dialog
    public void showLoginDialog(final Context context){
        final android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(MyAccount.this);
        final View mView = getLayoutInflater().inflate(R.layout.login_dialog, null);
        final EditText etEmail = (EditText) mView.findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) mView.findViewById(R.id.etPassword);
        final Button btnToSignUp = (Button) mView.findViewById(R.id.btnToSignUp);
        final Button btnLogin = (Button) mView.findViewById(R.id.btnLogin);
        final ImageButton btnCloseLogin = (ImageButton) mView.findViewById(R.id.btnClose2);
        final ProgressBar progressBarLogin = (ProgressBar) mView.findViewById(R.id.progressBarLogin);

        progressBarLogin.setVisibility(mView.GONE);

        mBuilder.setView(mView);
        final android.app.AlertDialog mDialog = mBuilder.create();
        mDialog.show();

        btnLogin.setOnClickListener(v -> {
            if (!etEmail.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()){

                String email = etEmail.getText().toString().trim();
                String pwd = etPassword.getText().toString().trim();

                progressBarLogin.setVisibility(mView.VISIBLE);

                mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(task -> {
                    //signIn
                    if(task.isSuccessful()){
                        String uid = mAuth.getCurrentUser().getUid();

                        DocumentReference doc = fireDb.collection("users").document(uid);
                        doc.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                progressBarLogin.setVisibility(mView.GONE);

                                DocumentSnapshot document = task1.getResult();
                                if (document.exists()) {
                                    User user = document.toObject(User.class);

                                    session.createSession(user.getUsername(), uid, user.getProfileScore(), user.getFriends());
                                    Toast.makeText(context,getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                                    if (getIntent().getStringExtra("gameover") != null) {
                                        Intent intent = new Intent(MyAccount.this, GameOver.class);
                                        intent.putExtra("gameover", "gameover");
                                        intent.putExtra("points", points);
                                        intent.putExtra("difficulty", difficulty);
                                        intent.putExtra("chosenGame", chosenGame);
                                        startActivity(intent);
                                        finish();
                                    }else if (getIntent().getStringExtra("main") != null){
                                        Intent intent = new Intent(MyAccount.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        mDialog.dismiss();
                                        recreate();
                                    }
                                } else {
                                    Snackbar.make(mView, "No such document",Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(mView, "get failed with ",Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        progressBarLogin.setVisibility(mView.GONE);

                        Toast.makeText(context, getResources().getString(R.string.loginError), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(context, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
            }
        });
        btnToSignUp.setOnClickListener(v -> showSignUpDialog(getApplicationContext()));
        btnCloseLogin.setOnClickListener(v -> mDialog.dismiss());
    }

    //shows the sign-up dialog
    private void showSignUpDialog(final Context context){
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(MyAccount.this);
        View mView = getLayoutInflater().inflate(R.layout.signup_dialog, null);
        final EditText etUsername = (EditText) mView.findViewById(R.id.etUsername);
        final EditText etEmail = (EditText) mView.findViewById(R.id.etEmail);
        final EditText etPwdSignUp = (EditText) mView.findViewById(R.id.etPasswordSignUp);
        final  EditText etConfPassword = (EditText) mView.findViewById(R.id.etConfirmPwd);
        final Button btnToLogin = (Button) mView.findViewById(R.id.btnToLogin);
        final Button btnSignUp = (Button) mView.findViewById(R.id.btnSignUp);
        final ImageButton btnCloseSignUp = (ImageButton) mView.findViewById(R.id.btnClose3);
        final ProgressBar progressBarSignUp = (ProgressBar) mView.findViewById(R.id.progressBarSignUp);

        progressBarSignUp.setVisibility(mView.GONE);

        mBuilder.setView(mView);
        final android.app.AlertDialog mDialogSignUp = mBuilder.create();
        mDialogSignUp.show();

        btnSignUp.setOnClickListener(v -> {
            if (!etEmail.getText().toString().isEmpty() && !etUsername.getText().toString().isEmpty() && !etPwdSignUp.getText().toString().isEmpty() && !etConfPassword.getText().toString().isEmpty()){

                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pwd = etPwdSignUp.getText().toString().trim();
                String confirmPwd = etConfPassword.getText().toString().trim();

                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //if email format is correct
                    if(!(username.length() >= 15)){
                        if(pwd.length() >= 6){
                            if(pwd.equals(confirmPwd)){
                                if(pwd.equals(confirmPwd)){
                                    progressBarSignUp.setVisibility(mView.VISIBLE);

                                    fireDb.collection("users").whereEqualTo("username", username).limit(1).get().addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            if (task.getResult().size() > 0){ //username already in use

                                                progressBarSignUp.setVisibility(mView.GONE);

                                                Toast.makeText(this, getResources().getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                                            }else{
                                                mAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(task1 -> {
                                                    if(task1.isSuccessful()){
                                                        progressBarSignUp.setVisibility(mView.GONE);
                                                        int profileScore = 0;
                                                        int nbFriends = 0;
                                                        User user = new User(username,email,pwd, profileScore, nbFriends, mAuth.getCurrentUser().getUid());
                                                        DocumentReference doc = fireDb.collection("users").document(mAuth.getCurrentUser().getUid());
                                                        doc.set(user); //creates users collection in Firestore with uid as document name
                                                        Toast.makeText(context,getResources().getString(R.string.sign_up_succes), Toast.LENGTH_LONG).show();
                                                        displayNotification(getApplicationContext());
                                                        showLoginDialog(context);
                                                        mDialogSignUp.dismiss();

                                                    }else{
                                                        progressBarSignUp.setVisibility(mView.GONE);

                                                        try{
                                                            throw task1.getException();
                                                        }catch(FirebaseAuthUserCollisionException existEmail){ //throw an error if email is already in use
                                                            Log.d("email", "onComplete: exist_email");

                                                            Toast.makeText(context,getResources().getString(R.string.emailExist), Toast.LENGTH_SHORT).show();
                                                        }
                                                        catch (Exception e) {
                                                            Log.d("error", "onComplete: " + e.getMessage());
                                                        }
                                                    }
                                                });
                                            }
                                        }else{
                                            Log.e("request", "request fail");
                                            Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(context,getResources().getString(R.string.same_pwd), Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(context,getResources().getString(R.string.same_pwd), Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(context,getResources().getString(R.string.pwdLenght), Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(context, getResources().getString(R.string.username_too_long), Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, getResources().getString(R.string.validEmail), Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(context, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
            }
        });

        btnToLogin.setOnClickListener(v -> {
            if (getIntent().getStringExtra("main") != null){
                showLoginDialog(context);
            }else{
                mDialogSignUp.dismiss();
            }
        });
        btnCloseSignUp.setOnClickListener(v -> {
            if (getIntent().getStringExtra("main") != null){
                finish();
            }else{
                mDialogSignUp.dismiss();
            }
        });

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

    public void addFriends(View view){

        Settings.btnAnimation(view);

        //add friends only when logged-in
        if(session.checkLoggedIn()){
            searchFriendsDialog();
        }else {
            Toast.makeText(this, getResources().getString(R.string.not_connected_friends), Toast.LENGTH_SHORT).show();
        }
    }

    public void searchFriendsDialog(){
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(MyAccount.this);
        View mView = getLayoutInflater().inflate(R.layout.add_friends_dialog, null);
        final EditText etAddFriends = (EditText) mView.findViewById(R.id.etAddFriends);
        final Button btnAdd = (Button) mView.findViewById(R.id.btnAdd);
        final ImageButton btnCloseSearch = (ImageButton) mView.findViewById(R.id.btnClose4);

        mBuilder.setView(mView);
        final android.app.AlertDialog mDialogSearch = mBuilder.create();
        mDialogSearch.show();

        btnAdd.setOnClickListener(v -> {

            if(!etAddFriends.getText().toString().isEmpty()){
                String friendsUsername = etAddFriends.getText().toString().trim();

                etAddFriends.onEditorAction(EditorInfo.IME_ACTION_DONE); //close the keyboard
                mDialogSearch.dismiss();
                addFriendsDialog(friendsUsername);

            }else{
                Toast.makeText(this, getResources().getString(R.string.login_empty_msg), Toast.LENGTH_LONG).show();
            }
        });

        btnCloseSearch.setOnClickListener(v -> mDialogSearch.dismiss());
    }

    public void addFriendsDialog(String friendsUsername){
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(MyAccount.this);
        View mView = getLayoutInflater().inflate(R.layout.add_friend_list_dialog, null);
        final ListView lvAddFriends = (ListView) mView.findViewById(R.id.lvFriends);
        final ImageButton btnCloseAddFriends = (ImageButton) mView.findViewById(R.id.btnClose5);
        final ProgressBar progressBarFriends = (ProgressBar) mView.findViewById(R.id.progressBarFriends);

        mBuilder.setView(mView);
        final android.app.AlertDialog mDialogAddFriends = mBuilder.create();
        mDialogAddFriends.show();

        progressBarFriends.setVisibility(mView.VISIBLE);

        ArrayList<String> friendsList = new ArrayList<>();

        //get friendslist from firestore
        fireDb.collection("users").orderBy("username").startAt(friendsUsername).endAt(friendsUsername + "\uf8ff").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                progressBarFriends.setVisibility(mView.GONE);
                for (DocumentSnapshot doc : task.getResult()){
                    if(!doc.getString("username").equalsIgnoreCase(session.getUsername())){
                        friendsList.add(doc.getString("username"));
                    }
                }
                showFriendList(lvAddFriends,friendsList, "add");
            }
        }).addOnFailureListener(e -> {
            progressBarFriends.setVisibility(mView.GONE);

            showFriendList(lvAddFriends, friendsList, "show");
            Log.e("No friends", e.getMessage());
        });

        btnCloseAddFriends.setOnClickListener(v -> mDialogAddFriends.dismiss());
    }

    private void showFriendList(ListView lvAddFriends, ArrayList<String> friendsList, String addOrShow) {
        ListAdapter listAdapter = new ArrayAdapter<>(this,R.layout.listrow, friendsList);
        lvAddFriends.setAdapter(listAdapter);

        if(!friendsList.isEmpty()){
            if(addOrShow.equalsIgnoreCase("add")){
                lvAddFriends.setOnItemClickListener((parent, view, position, id) -> {
                    //check if friends and adds new friend if necessary
                    alreadyFriends(friendsList.get(position));
                });
            }else if(addOrShow.equalsIgnoreCase("show")){
                //show profile
                lvAddFriends.setOnItemClickListener((parent, v, position, id) -> {
                    android.app.AlertDialog.Builder mBuilderFriend = new android.app.AlertDialog.Builder(MyAccount.this);
                    View mViewFriend = getLayoutInflater().inflate(R.layout.friend_profile_dialog, null);
                    final TextView tvFriendUsername = mViewFriend.findViewById(R.id.tvFriendUsername);
                    final TextView tvFriendHC = mViewFriend.findViewById(R.id.tvFriendHC);
                    final TextView textView3 = mViewFriend.findViewById(R.id.textView3);
                    final ProgressBar progressBarFriendProfile = mViewFriend.findViewById(R.id.progressBarFriendProfile);

                    mBuilderFriend.setView(mViewFriend);
                    final android.app.AlertDialog mDialogFriendProfile = mBuilderFriend.create();
                    mDialogFriendProfile.show();

                    tvFriendUsername.setText(friendsList.get(position));
                    //show friend's score
                    progressBarFriendProfile.setVisibility(mViewFriend.VISIBLE);
                    textView3.setVisibility(mViewFriend.INVISIBLE);
                    tvFriendHC.setVisibility(mViewFriend.INVISIBLE);
                    fireDb.collection("users").whereEqualTo("username", friendsList.get(position)).limit(1).get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            progressBarFriendProfile.setVisibility(mViewFriend.GONE);
                            textView3.setVisibility(mViewFriend.VISIBLE);
                            tvFriendHC.setVisibility(mViewFriend.VISIBLE);

                            for (DocumentSnapshot doc : task.getResult()){
                                tvFriendHC.setText(doc.get("profileScore") + "");
                            }
                        }else{
                            progressBarFriendProfile.setVisibility(mViewFriend.GONE);
                        }
                    });
                });
            }

        }else{
            friendsList.add(getResources().getString(R.string.no_data));
        }
    }

    private void addFriendToFirestore(String friend) {
        Friends newFriend = new Friends(session.getUsername(), friend);
        Friends me = new Friends(friend, session.getUsername());
        fireDb.collection("friends").document().set(newFriend).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                fireDb.collection("friends").document().set(me).addOnCompleteListener(task1 -> {
                    Toast.makeText(this, getResources().getString(R.string.friend_added), Toast.LENGTH_SHORT).show();
                    session.setNbFriends(fireDb, tvTotalFriends);

                    fireDb.collection("users").whereEqualTo("username", friend).limit(1).get().addOnSuccessListener(task2 -> {
                        for (DocumentSnapshot docUser : task2.getDocuments()){
                            String uid = docUser.getString("uid");
                            fireDb.collection("users").document(uid).update("friends", FieldValue.increment(1));
                        }
                    });
                });
            }else{
                Toast.makeText(this, "Could not add this friend", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void alreadyFriends(String friend) {
        fireDb.collection("friends").whereEqualTo("username", session.getUsername()).whereEqualTo("friends", friend).limit(1).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if (task.getResult().size() > 0){
                    Toast.makeText(this, getResources().getString(R.string.already_friend), Toast.LENGTH_LONG).show();
                }else{
                    addFriendToFirestore(friend);
                }
            }else{
                Toast.makeText(this, task.getException().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showFriends(View view) {
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(MyAccount.this);
        View mView = getLayoutInflater().inflate(R.layout.add_friend_list_dialog, null);
        final ListView lvAddFriends = (ListView) mView.findViewById(R.id.lvFriends);
        final TextView tvAddFriends = (TextView) mView.findViewById(R.id.tvAddFriends2);
        final ImageButton btnCloseShowFriends = (ImageButton) mView.findViewById(R.id.btnClose5);
        final ProgressBar progressBarFriends = (ProgressBar) mView.findViewById(R.id.progressBarFriends);

        mBuilder.setView(mView);
        final android.app.AlertDialog mDialogAddFriends = mBuilder.create();
        mDialogAddFriends.show();

        tvAddFriends.setText(getResources().getString(R.string.friends));

        ArrayList<String> friendsList = new ArrayList<>();
        progressBarFriends.setVisibility(mView.VISIBLE);
        //showFriends from firestore
        fireDb.collection("friends").whereEqualTo("username", session.getUsername()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                progressBarFriends.setVisibility(mView.GONE);

                for (DocumentSnapshot doc : task.getResult()){
                    friendsList.add(doc.getString("friends"));
                }
                showFriendList(lvAddFriends,friendsList, "show");
            }else{
                progressBarFriends.setVisibility(mView.GONE);

                Toast.makeText(this, task.getException().toString(), Toast.LENGTH_LONG).show();
            }
        });

        btnCloseShowFriends.setOnClickListener(v -> mDialogAddFriends.dismiss());
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
