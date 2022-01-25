package com.example.wackamole_grupopaola;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnPlay;
    SharedPreferences prefs;


    EditText nickEditText;
    ListView listViewNickname;
    ListView listViewScores;
//    TextView textViewHighScoreNick;
//    TextView textViewHighScoreScore;
    HighscoreDB highscoreDB;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        nickEditText.getText() == ""
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
//        edPrefs.putInt("Score", 0);
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

}