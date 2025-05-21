package sk.tuke.kpi.kp.bejeweled.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Jewel {
    private JewelType type;
    private int x, y;
    private JewelState state = JewelState.ADDED;

    public Jewel(JewelType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
