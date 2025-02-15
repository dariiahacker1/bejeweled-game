package bejeweled.core;

import java.util.Random;

public class Field {
    private Jewel[][] grid;
    private int width, height;
    private GameState gameState;
    private static final String[] JEWEL_TYPES = {"G", "W", "B", "O", "R", "Y", "P"};

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Jewel[width][height];
        this.gameState = GameState.PLAYING;
        initializeBoard();
    }

    private void initializeBoard() {
        Random rand = new Random();
        do {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (grid[i][j] == null) {
                        grid[i][j] = new Jewel(JEWEL_TYPES[rand.nextInt(JEWEL_TYPES.length)], i, j);
                    }
                }
            }
        } while (!hasPossibleMove());
    }

    public void swapJewels(int x1, int y1, int x2, int y2) {
        Jewel temp = grid[x1][y1];
        grid[x1][y1] = grid[x2][y2];
        grid[x2][y2] = temp;

        if(checkMatches()){
            removeMatches();
            initializeBoard(); // fill spaces with random gems but field must contain at least one possible move
        }
    }

    private boolean hasPossibleMove() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if ((i < width - 1 && isSwapValid(i, j, i + 1, j)) ||
                        (j < height - 1 && isSwapValid(i, j, i, j + 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSwapValid(int x1, int y1, int x2, int y2) {
        if(Math.abs(x1 - x2) + Math.abs(y1 - y2) != 1) return false;
        swapJewels(x1, y1, x2, y2);
        boolean hasMatch = checkMatches();
        swapJewels(x1, y1, x2, y2);
        return hasMatch;
    }

    private boolean checkMatches() {
        for (int i = 0; i < width - 2; i++) {
            for (int j = 0; j < height; j++) {
                if (grid[i][j].getType().equals(grid[i + 1][j].getType()) &&
                        grid[i][j].getType().equals(grid[i + 2][j].getType())) {
                    return true;
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height - 2; j++) {
                if (grid[i][j].getType().equals(grid[i][j + 1].getType()) &&
                        grid[i][j].getType().equals(grid[i][j + 2].getType())) {
                    return true;
                }
            }
        }

        return false;
    }

//    private void removeMatches() {
//
//        for (int i = 0; i < width - 2; i++) {
//            for (int j = 0; j < height; j++) {
//                String type = grid[i][j].getType();
//                if (type.equals(grid[i + 1][j].getType()) && type.equals(grid[i + 2][j].getType())) {
//                    int k = i;
//                    while (k < width && grid[k][j].getType().equals(type)) {
//                        grid[k][j] = null;
//                        k++;
//                    }
//
//                    if(i > 0 && type.equals(grid[i - 1][j].getType())){
//                       k = i - 1;
//                       while (k > 0 && grid[k][j].getType().equals(type)) {
//                           grid[k][j] = null;
//                           k--;
//                       }
//                    }
//
//                }
//            }
//        }
//
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height - 2; j++) {
//                if (grid[i][j].getType().equals(grid[i][j + 1].getType()) &&
//                        grid[i][j].getType().equals(grid[i][j + 2].getType())) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }


    public void printField() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                System.out.print(grid[col][row].getType() + " ");
            }
            System.out.println();
        }
    }

    public GameState getState() {
        return gameState;
    }

    public void setState(GameState state) {
        this.gameState = state;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
