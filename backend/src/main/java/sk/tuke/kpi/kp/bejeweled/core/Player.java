package sk.tuke.kpi.kp.bejeweled.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private final String username;
    private int score = 0;

    public Player(String username) {
        this.username = username;
    }

    public void updateScore(int points) { score += points; }
    public void resetScore() { score = 0; }
}
