package sk.tuke.kpi.kp.bejeweled.core;

import lombok.Getter;

@Getter
public class Player {
    private final String username;
    private int score = 0;

    public Player(String username) {
        this.username = username;
    }

    public void updateScore(int points) { score += points; }
    public void resetScore() { score = 0; }
}
