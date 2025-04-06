package sk.tuke.kpi.kp.bejeweled.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.kpi.kp.bejeweled.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.core.Field;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import sk.tuke.kpi.kp.bejeweled.core.MoveHandler;
import sk.tuke.kpi.kp.bejeweled.core.Player;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@SessionAttributes("field")
public class BejeweledController {

    //@Autowired
    private Field field = new Field(8, 8);
    private MoveHandler moveHandler = new MoveHandler(field);
    private ConsoleUI consoleUI = new ConsoleUI(field, 400, 300);
    private int timeRemaining = consoleUI.getTimeRemaining();


    @GetMapping("/bejeweled")
    public String bejeweled(@RequestParam(value = "command", required = false) String command,
                            @RequestParam(value = "row", required = false) Integer row,
                            @RequestParam(value = "column", required = false) Integer column,
                            @RequestParam(value = "newRow", required = false) Integer newRow,
                            @RequestParam(value = "newColumn", required = false) Integer newColumn,
                            Model model) {

        consoleUI.setPlayer(new Player("GUEST"));

        if ("swap".equals(command) && row != null && column != null && newRow != null && newColumn != null) {

            if (moveHandler.isValidMove(row, column, newRow, newColumn) && moveHandler.isSwapValid(row, column, newRow, newColumn)) {
                moveHandler.swapJewels(row, column, newRow, newColumn);
                field.checkMatchesAndRemove(consoleUI.getPlayer());
            } else {
                System.out.println("Invalid move: swap does not create a sequence.");
            }
        } else if ("new".equals(command)) {
            this.field = new Field(8, 8);
        }

        model.addAttribute("gameField", this.field);
        model.addAttribute("timeRemaining", this.timeRemaining);
        return "fragments/bejeweled";
    }

}
