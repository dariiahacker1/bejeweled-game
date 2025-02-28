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

    public boolean isSwapValid(int x1, int y1, int x2, int y2) {
        swapJewels(x1, y1, x2, y2);
        boolean hasMatch = field.checkMatches();
        swapJewels(x1, y1, x2, y2);
        return hasMatch;
    }

    public void swapJewels(int x1, int y1, int x2, int y2) {
        Jewel jewel1 = field.getJewel(x1, y1);
        Jewel jewel2 = field.getJewel(x2, y2);

        if (jewel1 != null && jewel2 != null) {
            field.setJewel(x1, y1, jewel2);
            field.setJewel(x2, y2, jewel1);

            jewel1.setPosition(x2, y2);
            jewel2.setPosition(x1, y1);
        }
    }

}
