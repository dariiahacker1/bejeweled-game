package sk.tuke.kpi.kp.bejeweled;

import sk.tuke.kpi.kp.bejeweled.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.core.Field;
import sk.tuke.kpi.kp.bejeweled.core.Player;

public class Bejeweled {
    public static void main(String[] args) {
        Field field = new Field(8, 8);
        Player player = new Player("dashenka");
        ConsoleUI ui = new ConsoleUI(field, player, 1000, 300);
        ui.play();
    }
}
