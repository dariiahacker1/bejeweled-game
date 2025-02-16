package src.sk.tuke.kpi.kp.bejeweled.core;

public class Jewel {
    private String type;
    private int x, y;

    public Jewel(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public String getType() { return type; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
