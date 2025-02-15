package bejeweled;

import bejeweled.consoleui.ConsoleUI;
import bejeweled.core.Field;
import bejeweled.core.Player;

public class Bejeweled {
    public static void main(String[] args) {
        Field field = new Field(8, 8);
        Player player = new Player("dashenka");
        ConsoleUI ui = new ConsoleUI(field, player, 1000, 300);
        ui.play();
    }
}
