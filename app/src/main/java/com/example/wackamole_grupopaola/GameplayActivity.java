package com.example.wackamole_grupopaola;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Random;

public class GameplayActivity extends AppCompatActivity {



    ArrayList<ImageView> imageViewList = new ArrayList<>();

    float gameTimer = 50;
    float maxGameTimer = 100;
    float spawnInterval = 3f;
    float spawnMaxInterval = 3f;
    private TextView timerTextView;
    private TextView lostTextView;
    private  TextView pointTextView;
    private  TextView scoreTextView;
    private  TextView nickTextView;

    HighscoreDB highscoreDB;

    MediaPlayer mediaPlayer;
    SharedPreferences prefs;
    SharedPreferences.Editor edPrefs;

    Player player = new Player();
    Random random = new Random();


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_gameplay);

        prefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        edPrefs = prefs.edit();

        PlayMusic();

        timerTextView = findViewById(R.id.timerText);
        lostTextView = findViewById(R.id.lostScore);
        pointTextView = findViewById(R.id.pointSocre);
        scoreTextView = findViewById(R.id.score);
        nickTextView = findViewById(R.id.nickTextView);

        imageViewList.add(findViewById(R.id.imageButton));
        imageViewList.add(findViewById(R.id.imageButton2));
        imageViewList.add(findViewById(R.id.imageButton3));
        imageViewList.add(findViewById(R.id.imageButton4));
        imageViewList.add(findViewById(R.id.imageButton5));
        imageViewList.add(findViewById(R.id.imageButton6));
        imageViewList.add(findViewById(R.id.imageButton7));
        imageViewList.add(findViewById(R.id.imageButton8));

        for (ImageView image: imageViewList) {
            image.setImageResource(R.drawable.buraco);
            image.setClickable(false);
        }

        highscoreDB = new HighscoreDB(getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();

        nickTextView.setText( prefs.getString("Nickname", "CHAVE NÃO EXISTE." ));

    }

    @Override
    protected void onResume() {
        super.onResume();




        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameTimer -= 1;
                Update();

                new Handler().postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void PlayMusic() {
        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.musics);
            mediaPlayer.setLooping(true);
        }
        mediaPlayer.start();
    }

    public void Update() {
        SetScore();

        CheckTimeToSpawn();
        timerTextView.setText("Time: "+Integer.toString((int)gameTimer));

        if(gameTimer == 0) {
            GoOutSceneGameplay();
        }

    }




    private  void CheckTimeToSpawn() {
        if(spawnInterval  == 1) {
            MoleDown();
        }

        if(spawnInterval == 0) {
            MoleUp();
            spawnInterval =  spawnMaxInterval;
        }

        spawnInterval -= 1;

    }


    private  void MoleUp() {
        ImageView targetImageView = imageViewList.get(random.nextInt(imageViewList.size()));

        targetImageView.setClickable(true);
        targetImageView.setImageResource(R.drawable.topeira);

    }

    private void MoleDown() {
        for (ImageView imgView: imageViewList) {
            if(imgView.isClickable()) {
                imgView.setClickable(false);
                imgView.setImageResource(R.drawable.buraco);
                ResetRoles();
            }
        }

    }

    private  void ResetRoles(){
         player.setLostMole(player.getLostMole()+1);
        SetUIText(lostTextView,"Perdidos: ", player.getLostMole());
    }

    public void GoOutSceneGameplay() {
        mediaPlayer.stop();
        SavePlayerProps();


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    public  void onClickExit(View view) {
        if(view == findViewById(R.id.exitButton)) {
            GoOutSceneGameplay();
        }
    }


    public void onMoleClicked(View iView) {
        iView.setClickable(false);
        ImageView image = (ImageView) iView;
        image.setImageResource(R.drawable.buraco);
        player.setHitMole(player.getHitMole()+1);
        SetUIText(pointTextView, "Acertos: ",player.getHitMole());
    }



    private  void SetScore() {
    SetUIText(scoreTextView,"Score: ", player.getScore());
    edPrefs.putInt("Score", player.getScore());
    edPrefs.apply();
}

private void SetUIText(TextView textView, String preText,Integer value) {
        textView.setText(preText+Integer.toString(value));
}



private void SavePlayerProps() {
    player.setNickname(prefs.getString("Nickname", "CHAVE NÃO EXISTE." ));
//    Toast.makeText(this, "Nick:"+player.getNickname()+" Score:"+player.getScore(), Toast.LENGTH_SHORT).show();
    highscoreDB.AddPlayer(player);
    highscoreDB.close();
}


}
