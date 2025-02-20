package sk.tuke.kpi.kp.bejeweled;

import sk.tuke.kpi.kp.bejeweled.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.core.Field;


public class Bejeweled {
    public static void main(String[] args) {
        Field field = new Field(8, 8);
        ConsoleUI ui = new ConsoleUI(field, 500, 300);
        ui.play(field);
    }
}
