package sk.tuke.kpi.kp.bejeweled;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.bejeweled.core.Field;
import sk.tuke.kpi.kp.bejeweled.core.GameState;
import sk.tuke.kpi.kp.bejeweled.core.JewelState;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {
    private Field field;

    @BeforeEach
    public void setUp() {
        field = new Field(8, 8);
    }

    @Test
    public void testFieldInitialization() {
        assertNotNull(field, "Field should be initialized.");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                assertEquals(JewelState.ADDED,field.getJewel(i, j).getState(),"Each jewel should have been added.");
            }
        }
    }

    @Test
    public void testInitialCheckMatches() {
        assertFalse(field.checkMatches(), "Field should not contain matches after initialization.");
    }

    @Test
    public void testInitialGameState() {
        assertEquals(GameState.PLAYING, field.getGameState(), "Initial game state should be PLAYING.");
    }

}