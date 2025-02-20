package sk.tuke.kpi.kp.bejeweled.core;

import java.util.Random;

public class Field {
    private Jewel[][] grid;
    private int width, height;
    private GameState gameState;
    private MoveHandler moveHandler;
    private static final String[] JEWEL_TYPES = {"G", "W", "B", "O", "R", "Y", "P"};

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Jewel[width][height];
        this.gameState = GameState.PLAYING;
        this.moveHandler = new MoveHandler(this);
        initializeBoard();
    }

    public void initializeBoard() {
        Random rand = new Random();
        boolean validBoard = false;

        while (!validBoard) {
            validBoard = true;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    grid[i][j] = new Jewel(JEWEL_TYPES[rand.nextInt(JEWEL_TYPES.length)], i, j);
                }
            }

            for (int i = 0; i < width - 2; i++) {
                for (int j = 0; j < height; j++) {
                    if (grid[i][j].getType().equals(grid[i + 1][j].getType()) &&
                            grid[i][j].getType().equals(grid[i + 2][j].getType())) {
                        validBoard = false;
                        break;
                    }
                }
                if (!validBoard) break;
            }

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height - 2; j++) {
                    if (grid[i][j].getType().equals(grid[i][j + 1].getType()) &&
                            grid[i][j].getType().equals(grid[i][j + 2].getType())) {
                        validBoard = false;
                        break;
                    }
                }
                if (!validBoard) break;
            }

            if (validBoard && !moveHandler.hasPossibleMove()) {
                validBoard = false;
            }
        }
    }


    public boolean checkMatches() {
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

    public void checkMatchesAndRemove(Player player) {
        boolean[][] toRemove = new boolean[width][height];

        for (int j = 0; j < height; j++) {
            int start = 0;
            while (start < width) {
                int end = start;
                while (end < width - 1 && grid[end][j] != null && grid[end + 1][j] != null &&
                        grid[end][j].getType().equals(grid[end + 1][j].getType())) {
                    end++;
                }
                if (end - start + 1 >= 3) {
                    player.updateScore((end - start + 1) > 3 ? 30 : 15);
                    for (int i = start; i <= end; i++) {
                        grid[i][j].setState(JewelState.REMOVED);
                        toRemove[i][j] = true;
                    }
                }
                start = end + 1;
            }
        }

        for (int i = 0; i < width; i++) {
            int start = 0;
            while (start < height) {
                int end = start;
                while (end < height - 1 && grid[i][end] != null && grid[i][end + 1] != null &&
                        grid[i][end].getType().equals(grid[i][end + 1].getType())) {
                    end++;
                }
                if (end - start + 1 >= 3) {
                    player.updateScore((end - start + 1) > 3 ? 30 : 15);
                    for (int j = start; j <= end; j++) {
                        grid[i][j].setState(JewelState.REMOVED);
                        toRemove[i][j] = true;
                    }
                }
                start = end + 1;
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (toRemove[i][j]) {
                    grid[i][j].setState(JewelState.REMOVED);
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                if (grid[i][j].getState() == JewelState.REMOVED) {
                    int k = j - 1;
                    while (k >= 0 && grid[i][k].getState() == JewelState.REMOVED) {
                        k--;
                    }
                    if (k >= 0) {
                        grid[i][j] = grid[i][k];
                        grid[i][k] = new Jewel(grid[i][k].getType(), i, k);
                        grid[i][k].setState(JewelState.ADDED);
                    }
                }
            }
        }

        Random rand = new Random();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (grid[i][j].getState() == JewelState.REMOVED) {
                    grid[i][j] = new Jewel(JEWEL_TYPES[rand.nextInt(JEWEL_TYPES.length)], i, j);
                    grid[i][j].setState(JewelState.ADDED);
                }
            }
        }
    }


    public void printField() {

        System.out.print("   ");
        for (int col = 0; col < width; col++) {
            System.out.print(col + " ");
        }
        System.out.println();

        for (int row = 0; row < height; row++) {
            System.out.print(Character.toString(65 + row) + "  ");
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

    public Jewel getJewel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return grid[x][y];
    }

    public void setJewel(int x, int y, Jewel jewel) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[x][y] = jewel;
        }
    }


}
