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
        super(context, "HighscoreDB", null, 15);

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
    public void AddPlayer(Player player) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();



        values.put("Nickname", player.getNickname() );
        values.put("Score", player.getScore());
        db.insert("Highscore",null, values);


    }

    public List<Player> FindHighscore() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM Highscore;";

        Cursor cursor = db.rawQuery(sql, null);

        List<Player> allPlayers = new ArrayList<Player>();

        while(cursor.moveToNext() ) {
            String nick = cursor.getString((cursor.getColumnIndex("Nickname")));
            Integer score = Integer.valueOf(cursor.getString((Integer)cursor.getColumnIndex("Score")));
            //DB: https://www.youtube.com/watch?v=9ztGeljlMgs&list=PLHI7bDSQYkJjP1hURxnMWNOjMbLyr_MFu&index=2
            //DO ZE: https://www.youtube.com/watch?v=ymMFK9FndYo

            Player player = new Player();
            player.setNickname(nick);
            player.setScore(score);
            allPlayers.add(player);
        }

        Player[] playerArray = new Player[allPlayers.size()];
        return HighPlayersSort(allPlayers.toArray(playerArray));

    }

    private List<Player> HighPlayersSort(Player[] players) {
        Player playerTranspose;
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
            for(int j= 0; j < players.length -1; j++){
                if(players[j].getScore() < players[j+1].getScore()) {
                    playerTranspose = players[j];
                    players[j] =  players[j+1];
                    players[j+1] = playerTranspose;
                    flag = true;
                }
            }
        }



        List<Player> playerList = new ArrayList(Arrays.asList(players));
        return  playerList;
    }


//    private List<Player> TenFirst(List<Player> p) {
//        return
//    }




}
