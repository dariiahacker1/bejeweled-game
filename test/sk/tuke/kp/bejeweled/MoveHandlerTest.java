package test.sk.tuke.kp.bejeweled;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.sk.tuke.kpi.kp.bejeweled.core.Field;
import src.sk.tuke.kpi.kp.bejeweled.core.MoveHandler;

import static org.junit.jupiter.api.Assertions.*;

public class MoveHandlerTest {
    private Field field;
    private MoveHandler moveHandler;

    @BeforeEach
    public void setUp() {
        field = new Field(8, 8);
        moveHandler = new MoveHandler(field);
    }

    @Test
    public void testAdjacentSwap() {
        assertTrue(moveHandler.isValidMove(2, 2, 2, 3));
        assertTrue(moveHandler.isValidMove(5, 5, 6, 5));
    }

    @Test
    public void testInvalidMoveOutOfBounds() {
        assertFalse(moveHandler.isValidMove(-1, 0, 0, 0));
        assertFalse(moveHandler.isValidMove(0, 8, 0, 7));
        assertFalse(moveHandler.isValidMove(7, 7, 8, 7));
    }

    @Test
    public void testInvalidNonAdjacentSwap() {
        assertFalse(moveHandler.isValidMove(2, 2, 4, 2));
        assertFalse(moveHandler.isValidMove(3, 3, 3, 6));
    }


    @Test
    public void testHasPossibleMove() {
        assertTrue(moveHandler.hasPossibleMove());
    }

}
