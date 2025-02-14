import java.util.Random;

public class Field {

    private Jewel[][] grid;
    private int width, height;
    private GameState gameState;

    private static final String[] JEWEL_TYPES = { "G", "W", "B", "O", "R", "Y", "P"};

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Jewel[width][height];
        this.gameState = GameState.PLAYING;
        initializeBoard();
    }

    // add check if there is at least one possible success move
    private void initializeBoard() {
        Random rand = new Random();
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                grid[i][j] = new Jewel(JEWEL_TYPES[rand.nextInt(7)], i, j);
            }
        }
    }

    public Jewel getJewel(int x, int y) {
        if (x >= 0 && x < height && y >= 0 && y < width) {
            return grid[x][y];
        }
        return null;
    }

    public void swapJewels(int x1, int y1, int x2, int y2) {
        Jewel temp = grid[x1][y1];
        grid[x1][y1] = grid[x2][y2];
        grid[x2][y2] = temp;
    }

    public void printField() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                System.out.print(grid[row][col].getType() + " ");
            }
            System.out.println();
        }
    }

    public GameState getState() { return gameState; }
    public void setState(GameState state) { this.gameState = state; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

}
