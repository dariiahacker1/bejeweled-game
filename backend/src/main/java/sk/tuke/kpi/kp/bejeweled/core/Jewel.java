package sk.tuke.kpi.kp.bejeweled.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Jewel {
    private JewelType type;
    private int row, col;
    private JewelState state = JewelState.ADDED;
    private boolean isBomb;

    public Jewel(JewelType type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
