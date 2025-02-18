package sk.tuke.kpi.kp.bejeweled.src.core;

public class Player {
    private final String username;
    private int score;

    public Player(String username) {
        this.username = username;
        this.score = 0;
    }

    public void updateScore(int points) { score += points; }
    public int getScore() { return score; }
    public void resetScore() { score = 0; }
}
