package sk.tuke.kpi.kp.bejeweled.core;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MoveHandler {

    private final Field field;

    public boolean isValidMove(int x1, int y1, int x2, int y2) {
        return (x1 >= 0 && x1 < field.getWidth() && y1 >= 0 && y1 < field.getHeight() &&
                x2 >= 0 && x2 < field.getWidth() && y2 >= 0 && y2 < field.getHeight()) &&
                Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1;
    }

    public boolean hasPossibleMove() {
        for (int x = 0; x < field.getWidth(); x++) {
            for (int y = 0; y < field.getHeight(); y++) {
                if ((x + 1 < field.getWidth() && isSwapValid(x, y, x + 1, y)) ||
                        (y + 1 < field.getHeight() && isSwapValid(x, y, x, y + 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSwapValid(int row1, int col1, int row2, int col2) {
        swapJewels(row1, col1, row2, col2);
        boolean hasMatch = field.checkMatches();
        swapJewels(row1, col1, row2, col2);
        return hasMatch;
    }

    public void swapJewels(int x1, int y1, int x2, int y2) {
        Jewel jewel1 = field.getJewel(y1, x1);
        Jewel jewel2 = field.getJewel(y2, x2);

        if (jewel1 != null && jewel2 != null) {
            field.setJewel(y1, x1, jewel2);
            field.setJewel(y2, x2, jewel1);
        }
    }

}
