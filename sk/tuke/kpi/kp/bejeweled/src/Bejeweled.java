package sk.tuke.kpi.kp.bejeweled.src;

import sk.tuke.kpi.kp.bejeweled.src.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.src.core.Field;
import sk.tuke.kpi.kp.bejeweled.src.core.Player;

public class Bejeweled {
    public static void main(String[] args) {
        Field field = new Field(8, 8);
        Player player = new Player("dashenka");
        ConsoleUI ui = new ConsoleUI(field, player, 500, 300);
        ui.play(field);
    }
}
