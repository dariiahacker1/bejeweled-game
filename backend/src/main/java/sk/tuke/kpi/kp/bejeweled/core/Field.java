package sk.tuke.kpi.kp.bejeweled.core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Field {
    private Jewel[][] grid;
    private int width, height;
    private GameState gameState;
    private MoveHandler moveHandler;
    private static final JewelType[] JEWEL_TYPES = JewelType.values();

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Jewel[height][width];
        this.gameState = GameState.PLAYING;
        this.moveHandler = new MoveHandler(this);
    }

    public List<int[]> activateBomb(int row, int col, Player player) {
        List<int[]> affected = new ArrayList<>();

        for (int r = Math.max(0, row - 1); r <= Math.min(height - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(width - 1, col + 1); c++) {
                if (grid[r][c] != null) {
                    grid[r][c].setState(JewelState.REMOVED);
                    affected.add(new int[]{r, c});
                }
            }
        }

        processRemovalsAndRefill(player);

        if (player != null) {
            player.updateScore(25);
        }

        return affected;
    }

    public void swapJewels(int row1, int col1, int row2, int col2) {
        Jewel jewel1 = getJewel(row1, col1);
        Jewel jewel2 = getJewel(row2, col2);

        if (jewel1 != null && jewel2 != null) {
            setJewel(row1, col1, jewel2);
            setJewel(row2, col2, jewel1);
        }
    }

    private Jewel[][] applyGravity() {
        Random random = new Random();
        Jewel[][] newGrid = new Jewel[height][width];

        for (int c = 0; c < width; c++) {
            int writeRow = height - 1;

            for (int r = height - 1; r >= 0; r--) {
                if (grid[r][c] != null && grid[r][c].getState() != JewelState.REMOVED) {
                    newGrid[writeRow--][c] = grid[r][c];
                    newGrid[writeRow + 1][c].setPosition(writeRow + 1, c);
                }
            }

            while (writeRow >= 0) {
                Jewel newJewel = new Jewel(
                        JEWEL_TYPES[random.nextInt(JEWEL_TYPES.length - 1)],
                        writeRow, c
                );
                if (random.nextDouble() < 0.05) {
                    newJewel.setBomb(true);
                }
                newGrid[writeRow--][c] = newJewel;
            }
        }

        return newGrid;
    }

    private void processRemovalsAndRefill(Player player) {
        this.grid = applyGravity();
        if (player != null) {
            checkMatchesAndRemove(player);
        }
    }

    public void initializeBoard() {
        Random rand = new Random();
        boolean validBoard = false;

        while (!validBoard) {
            validBoard = true;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    grid[i][j] = new Jewel(JEWEL_TYPES[rand.nextInt(JEWEL_TYPES.length - 1)], i, j);

                    if (rand.nextDouble() < 0.05) {
                        grid[i][j].setBomb(true);
                    }
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
        boolean isFirstMatch = false;
        boolean matchesFound;

        do {
            matchesFound = false;
            boolean[][] toRemove = new boolean[width][height];

            for (int row = 0; row < height; row++) {
                int matchStart = 0;
                while (matchStart < width) {
                    int matchEnd = matchStart;

                    while (matchEnd < width - 1 && grid[matchEnd][row] != null && grid[matchEnd + 1][row] != null &&
                            grid[matchEnd][row].getType().equals(grid[matchEnd + 1][row].getType())) {
                        matchEnd++;
                    }
                    if (matchEnd - matchStart + 1 >= 3) {
                        matchesFound = true;
                        if (!isFirstMatch) {
                            player.updateScore((matchEnd - matchStart + 1) * 5);
                            isFirstMatch = true;
                        }
                        for (int col = matchStart; col <= matchEnd; col++) {
                            toRemove[col][row] = true;
                        }
                    }
                    matchStart = matchEnd + 1;
                }
            }

            for (int col = 0; col < width; col++) {
                int matchStart = 0;
                while (matchStart < height) {
                    int matchEnd = matchStart;

                    while (matchEnd < height - 1 && grid[col][matchEnd] != null && grid[col][matchEnd + 1] != null &&
                            grid[col][matchEnd].getType().equals(grid[col][matchEnd + 1].getType())) {
                        matchEnd++;
                    }
                    if (matchEnd - matchStart + 1 >= 3) {
                        matchesFound = true;
                        if (!isFirstMatch) {
                            player.updateScore((matchEnd - matchStart + 1) * 5);
                            isFirstMatch = true;
                        }
                        for (int row = matchStart; row <= matchEnd; row++) {
                            toRemove[col][row] = true;
                        }
                    }
                    matchStart = matchEnd + 1;
                }
            }

            for (int col = 0; col < width; col++) {
                for (int row = 0; row < height; row++) {
                    if (toRemove[col][row]) {
                        grid[col][row].setState(JewelState.REMOVED);
                    }
                }
            }

            this.grid = applyGravity();

        } while (matchesFound);
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
                System.out.print(grid[col][row].getType().name().substring(0, 1).toUpperCase() + " ");
            }
            System.out.println();
        }
    }

    public Jewel getJewel(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) return null;
        return grid[row][col];
    }

    public void setJewel(int row, int col, Jewel jewel) {
        if (row >= 0 && row < height && col >= 0 && col < width) {
            grid[row][col] = jewel;
        }
    }

}