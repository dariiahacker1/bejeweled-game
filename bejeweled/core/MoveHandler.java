package bejeweled.core;

public class MoveHandler {

    private Field field;

    public MoveHandler(Field field) {
        this.field = field;
    }

    public boolean isValidMove(int x1, int y1, int x2, int y2) {
        // If the jewels are horizontally adjacent, Math.abs(x1 - x2) == 1 and Math.abs(y1 - y2) == 0.
        // If the jewels are vertically adjacent, Math.abs(x1 - x2) == 0 and Math.abs(y1 - y2) == 1.
        if (x1 < 0 || x1 >= field.getWidth() || y1 < 0 || y1 >= field.getHeight() || x2 < 0 || x2 >= field.getWidth() || y2 < 0 || y2 >= field.getHeight()) {
            return false;
        }
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1;
    }

    public void swapJewels(int x1, int y1, int x2, int y2) {
        field.swapJewels(x1, y1, x2, y2);
    }

}
