package com.example.wackamole_grupopaola;

public class Player {

    String nickname;
    Integer score = 0;
    Integer lostMole = 0;
    Integer hitMole = 0;

    public Integer getScore() { return score;}
    public void setScore(Integer value) { score = value; }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String value) {
        nickname = value;
    }

    public Integer getHitMole() {
        return hitMole;
    }
    public void setHitMole(Integer value) {
        hitMole = value;
        score = hitMole-lostMole;
    }

    public Integer getLostMole() {
        return lostMole;
    }
    public void setLostMole(Integer value) {
        lostMole = value;
        score = hitMole-lostMole;
    }

}
