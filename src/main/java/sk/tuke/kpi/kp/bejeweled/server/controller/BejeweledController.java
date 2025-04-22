package sk.tuke.kpi.kp.bejeweled.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sk.tuke.kpi.kp.bejeweled.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.core.Field;
import org.springframework.ui.Model;
import sk.tuke.kpi.kp.bejeweled.core.GameState;
import sk.tuke.kpi.kp.bejeweled.core.MoveHandler;
import sk.tuke.kpi.kp.bejeweled.core.Player;
import sk.tuke.kpi.kp.bejeweled.entity.Comment;
import sk.tuke.kpi.kp.bejeweled.entity.Rating;
import sk.tuke.kpi.kp.bejeweled.entity.Score;
import sk.tuke.kpi.kp.bejeweled.service.CommentService;
import sk.tuke.kpi.kp.bejeweled.service.RatingService;
import sk.tuke.kpi.kp.bejeweled.service.ScoreService;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@SessionAttributes("field")
public class BejeweledController {

    private Field field;
    private MoveHandler moveHandler;
    private ConsoleUI consoleUI;
    private int timeRemaining;
    private Player player ;

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private GameState gameState;

    private Timer gameTimer;

    @GetMapping("/bejeweled/welcome")
    public String showWelcomeForm(Model model) {

        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer = null;
        }

        this.field = new Field(8, 8);
        this.player = new Player("GUEST");
        this.moveHandler = new MoveHandler(this.field);
        this.consoleUI = new ConsoleUI(this.field, 400, 300);
        this.gameState = GameState.PLAYING;
        this.timeRemaining = consoleUI.getTimeRemaining();

        model.addAttribute("playerName", "");
        return "fragments/welcome";
    }

    @PostMapping("/bejeweled/start")
    public String startGame(@ModelAttribute("playerName") String playerName, Model model) {
        this.player = new Player(playerName);
        consoleUI.setPlayer(player);
        field.initializeBoard();
        gameState = GameState.PLAYING;

        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (field.getGameState() == GameState.PLAYING && timeRemaining > 0) {
                    timeRemaining--;
                    if (timeRemaining == 0) {
                        field.setGameState(GameState.FAILED);
                        gameTimer.cancel();
                    }
                } else {
                    gameTimer.cancel();
                }
            }
        }, 1000, 1000);

        model.addAttribute("playerScore", player.getScore());
        model.addAttribute("gameField", field);
        model.addAttribute("timeRemaining", timeRemaining);
        return "redirect:/bejeweled";
    }

    @GetMapping("/bejeweled/time")
    @ResponseBody
    public int getTimeRemaining() {
        return timeRemaining;
    }

    @GetMapping("/bejeweled")
    public String bejeweled(@RequestParam(value = "command", required = false) String command,
                            @RequestParam(value = "row", required = false) Integer row,
                            @RequestParam(value = "column", required = false) Integer column,
                            @RequestParam(value = "newRow", required = false) Integer newRow,
                            @RequestParam(value = "newColumn", required = false) Integer newColumn,
                            Model model, RedirectAttributes redirectAttributes) {

        if ("swap".equals(command) && row != null && column != null && newRow != null && newColumn != null) {

            if (moveHandler.isValidMove(row, column, newRow, newColumn) && moveHandler.isSwapValid(row, column, newRow, newColumn)) {
                moveHandler.swapJewels(row, column, newRow, newColumn);
                field.checkMatchesAndRemove(consoleUI.getPlayer());

            } else {
                System.out.println("Invalid move: swap does not create a sequence.");
            }
        } else if ("new".equals(command)) {
            this.field = new Field(8, 8);
        }else if ("stop".equals(command)) {
            gameState = GameState.STOPPED;
            return endGame(redirectAttributes);
        }

        if (player.getScore() >= 400) {
            gameState = GameState.SOLVED;
            return endGame(redirectAttributes);
        }

        if (timeRemaining == 0 || field.getGameState() == GameState.FAILED) {
            gameState = GameState.FAILED;
            return endGame(redirectAttributes);
        }

        model.addAttribute("gameField", this.field);
        model.addAttribute("timeRemaining", this.timeRemaining);
        model.addAttribute("playerScore", player.getScore());
        model.addAttribute("gameState", gameState);
        return "fragments/bejeweled";
    }

    private String endGame(RedirectAttributes redirectAttributes) {
        String lastWords = "";

        if (gameState == GameState.FAILED) {
            lastWords = "Game Over! You ran out of time.";
        } else if (gameState == GameState.SOLVED) {
            lastWords = "You won!";
        } else if (gameState == GameState.STOPPED) {
            lastWords = "Game stopped by the player.";
        }

        Score score = new Score("bejeweled", player.getUsername(), player.getScore(), new Date());
        scoreService.addScore(score);

        redirectAttributes.addFlashAttribute("lastWords", lastWords);
        return "redirect:/bejeweled/end";
    }

    @GetMapping("/bejeweled/end")
    public String showEndPage(Model model) {
        List<Score> topScores = scoreService.getTopScores("bejeweled");
        model.addAttribute("topScores", topScores);
        model.addAttribute("averageRating", ratingService.getAverageRating("bejeweled"));
        model.addAttribute("comments", commentService.getComments("bejeweled"));
        return "fragments/endgame";
    }

    @PostMapping("/bejeweled/feedback")
    public String handleFeedback(@RequestParam("comment") String comment,
                                 @RequestParam("rating") int rating,
                                 RedirectAttributes redirectAttributes) {
        commentService.addComment(new Comment("bejeweled", player.getUsername(), comment, new Date()));
        ratingService.setRating(new Rating("bejeweled", player.getUsername(), rating, new Date()));
        redirectAttributes.addFlashAttribute("message", "Thank you for your feedback!");
        return "redirect:/bejeweled/end";
    }


}
