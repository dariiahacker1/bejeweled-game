package bejeweled.core;

public class Jewel {
    private String type;
    private int x, y;

    public Jewel(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public String getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
