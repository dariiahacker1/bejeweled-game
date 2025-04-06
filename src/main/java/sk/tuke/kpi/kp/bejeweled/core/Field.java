package sk.tuke.kpi.kp.bejeweled.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

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

            for (int col = 0; col < width; col++) {
                for (int row = height - 1; row >= 0; row--) {
                    if (grid[col][row].getState() == JewelState.REMOVED) {
                        int searchIndex = row - 1;
                        while (searchIndex >= 0 && grid[col][searchIndex].getState() == JewelState.REMOVED) {
                            searchIndex--;
                        }
                        if (searchIndex >= 0) {
                            grid[col][row] = grid[col][searchIndex];
                            grid[col][searchIndex].setState(JewelState.REMOVED);
                        }
                    }
                }
            }

            Random random = new Random();
            for (int col = 0; col < width; col++) {
                for (int row = 0; row < height; row++) {
                    if (grid[col][row].getState() == JewelState.REMOVED) {
                        grid[col][row] = new Jewel(JEWEL_TYPES[random.nextInt(JEWEL_TYPES.length)], col, row);
                    }
                }
            }

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
                System.out.print(grid[col][row].getType().name().substring(0,1).toUpperCase() + " ");
            }
            System.out.println();
        }
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