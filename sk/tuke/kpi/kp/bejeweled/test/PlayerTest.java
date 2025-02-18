package sk.tuke.kpi.kp.bejeweled.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.bejeweled.src.core.Player;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player("dariiahacker");
    }

    @Test
    public void testInitialPlayerScore() {
        assertEquals(0, player.getScore(), "Initial score should be 0");
    }

    @Test
    public void testUpdateScore() {
        player.updateScore(10);
        assertEquals(10, player.getScore(), "Score should be updated to 10");

        player.updateScore(20);
        assertEquals(30, player.getScore(), "Score should be updated to 30");
    }

}
