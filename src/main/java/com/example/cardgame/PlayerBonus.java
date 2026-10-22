package com.example.cardgame;

public class PlayerBonus {
    private Player player;
    private String bonusType;
    private int points;

    public PlayerBonus(Player player, String bonusType, int points) {
        this.player = player;
        this.bonusType = bonusType;
        this.points = points;
    }

    public Player getPlayer() { return player; }
    public String getBonusType() { return bonusType; }
    public int getPoints() { return points; }
}