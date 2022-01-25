package com.example.wackamole_grupopaola;

public class Player {

    String nickname;
    Integer score = 0;
    Integer lostMole = 0;
    Integer hitMole = 0;

    public Integer getScore() {
//        score = (hitMole-lostMole);
//        return (score);
        return hitMole-lostMole;

    }
    public void setScore(Integer value) { score = value; }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        nickname = nickname;
    }

    public Integer getHitMole() {
        return hitMole;
    }
    public void setHitMole(Integer value) {
        hitMole = value;
    }

    public Integer getLostMole() {
        return lostMole;
    }
    public void setLostMole(Integer value) { lostMole = value; }
}
