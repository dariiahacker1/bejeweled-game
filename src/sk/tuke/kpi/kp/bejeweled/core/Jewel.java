package src.sk.tuke.kpi.kp.bejeweled.core;

public class Jewel {
    private String type;
    private int x, y;
    private JewelState state;

    public Jewel(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.state = JewelState.ADDED;
    }

    public String getType() {
        return type;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public JewelState getState() {
        return state;
    }

    public void setState(JewelState state) {
        this.state = state;
    }
}
