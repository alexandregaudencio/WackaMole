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
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
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

    @Override
    protected void onResume() {
        super.onResume();
        /////////////////////////
        SignInSilently();
        ////////////////////////
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, nickEditText.getText(), Toast.LENGTH_SHORT).show();

        UpdateRanking();

//       String status = Environment.getExternalStorageState();
//        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();

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
        List<Player> players = highscoreDB.FindHighscore();
        List<String> nicknames = new ArrayList<String>();
        List<Integer> scores = new ArrayList<Integer>();

        for(Player p : players) {
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

            } catch (ApiException e) {
                Log.i("GMSConnection", e.getMessage());
                onDisconnected();
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

        playersClient.getCurrentPlayer().addOnCompleteListener(this,
                new OnCompleteListener<com.google.android.gms.games.Player>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.android.gms.games.Player> task) {
                        String playerName = "";
                        if(task.isSuccessful()) {
                            playerName = task.getResult().getDisplayName();
                            Log.i("GMSConnection", playerName);
                        } else {
                            Exception e = task.getException();
                            Log.i("GMSConnection", e.toString()+"fUDEU!!!");
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