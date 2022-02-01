package com.example.wackamole_grupopaola;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnPlay;
    SharedPreferences prefs;


    EditText nickEditText;
    ListView listViewNickname;
    ListView listViewScores;

    HighscoreDB highscoreDB;


    private GoogleSignInClient googleSignInClient;
    private PlayersClient playersClient;
    private static final int RC_SIGNIN = 4002;
    private AchievementsClient achievementsClient ;
    private LeaderboardsClient leaderboardsClient;
    private static final int RC_LEADERBOARD = 0000;
    private static String leaderboardCode ="CgkI6ra7oIAbEAIQAQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///////////////////////
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);


        //////////////////////


        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);

        btnPlay = findViewById(R.id.btnPlay);
        nickEditText = findViewById(R.id.editTextNickname);
        listViewNickname = findViewById(R.id.ListViewNick);
        listViewScores = findViewById(R.id.ListViewScores);

//        textViewHighScoreNick = findViewById(R.id.textViewHighScoreNick);
//        textViewHighScoreScore = findViewById(R.id.textViewHighscoreScore);

        highscoreDB = new HighscoreDB(getApplicationContext());

    }

    //////////////////////////////////////
    private void showLeaderboard() {
        leaderboardsClient.getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult (intent,RC_LEADERBOARD);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Leaderboard", e.getMessage());
            }
        });
    }

    public void ShowAchievements() {
        achievementsClient.getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD);
                    }
                });
    }
///////////////////////////////////


    @Override
    protected void onResume() {
        super.onResume();
        /////////////////////////
        SignInSilently();

        if(leaderboardsClient != null) {
            leaderboardsClient.submitScore( leaderboardCode ,highscoreDB.ReturnFirstOneScore());
            Log.i("SAVE LEADER", "SavePlayerProps:"+ highscoreDB.ReturnFirstOneScore());

        }
        ////////////////////////

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, nickEditText.getText(), Toast.LENGTH_SHORT).show();

        UpdateRanking();
        //ATIVA UM ACHTIVEMENT




    }

    public void onClickRanking(View view) {
        if(leaderboardsClient != null) {
            leaderboardsClient.submitScore( leaderboardCode ,highscoreDB.ReturnFirstOneScore());
        }
        showLeaderboard();
    }

    public void onClickRAchievements(View view) {
        ShowAchievements();
    }
    public void onClickStart(View view) {

        if(!isEditTextVoid()) {
            Intent intent = new Intent(this, GameplayActivity.class);
            startActivity(intent);
            SetPprefsProps(nickEditText.getText().toString());
        }




    }

    //  RETORNA SE O CAMPO DE ENTRADA DE TEXTO ESTÁ VAZIO
    private boolean isEditTextVoid() {
//        nickEditText.getText().toString() == ""
        if(nickEditText.getText().equals("")) {
            Toast.makeText(this, "Define Nickname", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }

    }

    //DEFINE AS PROPRIEDADES DO PLAYER
    private void SetPprefsProps(String nickname) {
        SharedPreferences.Editor edPrefs = prefs.edit();
        edPrefs.putString("Nickname", nickname );
        edPrefs.putInt("Score", 0);
        edPrefs.apply();




    }


    private void UpdateRanking() {
        HighscoreDB highscoreDB = new HighscoreDB(getApplicationContext());
        List<DataPlayer> players = highscoreDB.FindRanking();
        List<String> nicknames = new ArrayList<String>();
        List<Integer> scores = new ArrayList<Integer>();

        for(DataPlayer p : players) {
            nicknames.add(p.getNickname());
            scores.add(p.getScore());
        }

        ArrayAdapter<String> nickArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, nicknames);
        listViewNickname.setAdapter(nickArrayAdapter);
        ArrayAdapter<Integer> scoreArrayAdapter = new ArrayAdapter<Integer>(getApplicationContext(), android.R.layout.simple_list_item_1, scores);
        listViewScores.setAdapter(scoreArrayAdapter);
    }









    ////////////////////
    public void onClickSign(View view) {
        if(view ==findViewById(R.id.SignInButton)) {
            StartSignInIntent();
        } else if (view == findViewById(R.id.SignOutButton)) {
            if(GoogleSignIn.getLastSignedInAccount(this) != null) {
                googleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i("GMSConnection", task.isSuccessful() ? "sucess" : "failed");
                            }
                        });
            }
        }
//        else {
//            Intent i = new Intent(this, GameplayActivity.class);
//            startActivity(i);
//        }
    }
    private  void StartSignInIntent() {
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGNIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGNIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                onConnected(account);
                Log.i("GMSConnection", "onActivityResult: OK!");


            } catch (ApiException e) {
                Log.i("GMSConnection", e.getMessage());
                onDisconnected();
                Log.i("GMSConnection", "onActivityResult: OK!");

            }
        }
    }

    public  void SignInSilently() {
        googleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if(task.isSuccessful()){
                            onConnected(task.getResult());
                        } else {
                            Log.i("GMSConnection", task.getException().toString());
                            onDisconnected();

                        }
                    }
                });
    }

    private  void onConnected(GoogleSignInAccount googleSignInAccount) {
        playersClient = Games.getPlayersClient(this, googleSignInAccount);
        GamesClient gamesClient = Games.getGamesClient(this, googleSignInAccount);

        gamesClient.setViewForPopups(findViewById(R.id.popupTextView));
        gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        achievementsClient = Games.getAchievementsClient(this, googleSignInAccount);
        leaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);

        playersClient.getCurrentPlayer().addOnCompleteListener(this,
                new OnCompleteListener<Player>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.android.gms.games.Player> task) {
                        String playerName = "";
                        if(task.isSuccessful()) {
                            playerName = task.getResult().getDisplayName();
                            Log.i("GMSConnection", playerName);
                            nickEditText.setText(playerName);
                        } else {
                            Exception e = task.getException();
                            Log.i("GMSConnection", e.toString());
                        }
                    }
                });

        findViewById(R.id.SignInButton).setEnabled(false);
        findViewById(R.id.SignOutButton).setEnabled(true);

    }

    private  void onDisconnected() {
        findViewById(R.id.SignInButton).setEnabled(true);
        findViewById(R.id.SignOutButton).setEnabled(false);
    }
///////////////////////////


}