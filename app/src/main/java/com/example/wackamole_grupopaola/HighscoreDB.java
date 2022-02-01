package com.example.wackamole_grupopaola;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HighscoreDB extends SQLiteOpenHelper {


    public HighscoreDB(@Nullable Context context) {
        super(context, "HighscoreDB", null, 18);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Highscore (Nickname TEXT NOT NULL, Score INTEGER NOT NULL);";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Highscore";
        db.execSQL(sql);
        onCreate(db);
    }


    //TODO: passar objeto "PLAYER" como parâmetro
    public void AddPlayer(DataPlayer dataPlayer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();



        values.put("Nickname", dataPlayer.getNickname() );
        values.put("Score", dataPlayer.getScore());
        db.insert("Highscore",null, values);


    }

    public List<DataPlayer> FindRanking() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM Highscore;";

        Cursor cursor = db.rawQuery(sql, null);

        List<DataPlayer> allDataPlayers = new ArrayList<DataPlayer>();

        while(cursor.moveToNext() ) {
            String nick = cursor.getString((cursor.getColumnIndex("Nickname")));
            Integer score = Integer.valueOf(cursor.getString((Integer)cursor.getColumnIndex("Score")));
            //DB: https://www.youtube.com/watch?v=9ztGeljlMgs&list=PLHI7bDSQYkJjP1hURxnMWNOjMbLyr_MFu&index=2
            //DO ZE: https://www.youtube.com/watch?v=ymMFK9FndYo

            DataPlayer dataPlayer = new DataPlayer();
            dataPlayer.setNickname(nick);
            dataPlayer.setScore(score);
            allDataPlayers.add(dataPlayer);
        }

        DataPlayer[] dataPlayerArray = new DataPlayer[allDataPlayers.size()];
        return HighPlayersSort(allDataPlayers.toArray(dataPlayerArray));

    }

    private List<DataPlayer> HighPlayersSort(DataPlayer[] dataPlayers) {
        DataPlayer dataPlayerTranspose;
//        for(int i = players.length-1; i > 0; i--) {
//            for (int j = 0; j < i; j++) {
//                if(players[j].getScore() > players[j+1].getScore()) {
////                    sortedList.add(players.get(j));
//                    playerTranspose = players[j];
//                    players[j] = players[j + 1];
//                    players[j+1] = playerTranspose;
//
//                }
//            }
//        }

        boolean flag = true;

        while (flag) {
            flag = false;
            for(int j = 0; j < dataPlayers.length -1; j++){
                if(dataPlayers[j].getScore() < dataPlayers[j+1].getScore()) {
                    dataPlayerTranspose = dataPlayers[j];
                    dataPlayers[j] =  dataPlayers[j+1];
                    dataPlayers[j+1] = dataPlayerTranspose;
                    flag = true;
                }
            }
        }

        List<DataPlayer> dataPlayerList = new ArrayList(Arrays.asList(dataPlayers));
        return dataPlayerList;
    }


    public int ReturnFirstOneScore() {

        return FindRanking().size() > 0 ?  FindRanking().get(0).score : 0  ;
    }


}
