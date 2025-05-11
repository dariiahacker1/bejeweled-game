package sk.tuke.kpi.kp.bejeweled.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import sk.tuke.kpi.kp.bejeweled.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.core.Field;
import sk.tuke.kpi.kp.bejeweled.core.GameState;
import sk.tuke.kpi.kp.bejeweled.core.MoveHandler;
import sk.tuke.kpi.kp.bejeweled.core.Player;
import sk.tuke.kpi.kp.bejeweled.entity.Comment;
import sk.tuke.kpi.kp.bejeweled.entity.Rating;
import sk.tuke.kpi.kp.bejeweled.entity.Score;
import sk.tuke.kpi.kp.bejeweled.service.CommentService;
import sk.tuke.kpi.kp.bejeweled.service.RatingService;
import sk.tuke.kpi.kp.bejeweled.service.ScoreService;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3002")
@RestController
@Scope(WebApplicationContext.SCOPE_SESSION)
@SessionAttributes({"field", "player"})
public class BejeweledController {

    /*
    @Autowired
    private Field field;
    private Player player = new Player("Guest");
    @Autowired
    private MoveHandler moveHandler;
    @Autowired
    private ConsoleUI consoleUI;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private GameState gameState;
    private int timeRemaining;
    private Timer gameTimer;

    @GetMapping("/bejeweled/welcome")
    public String showWelcomeForm(Model model) {

        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer = null;
        }

        this.gameState = GameState.PLAYING;
        this.timeRemaining = consoleUI.getTimeRemaining();

        model.addAttribute("playerName", "");
        return "fragments/welcome";
    }

    @GetMapping("/api/game")
    @ResponseBody
    public Map<String, Object> getGameData() {
        Map<String, Object> response = new HashMap<>();

        List<List<Map<String, String>>> grid = new ArrayList<>();

        for (int row = 0; row < field.getHeight(); row++) {
            List<Map<String, String>> rowList = new ArrayList<>();
            for (int col = 0; col < field.getWidth(); col++) {
                var jewel = field.getJewel(row, col);
                if (jewel != null) {
                    Map<String, String> jewelData = new HashMap<>();
                    jewelData.put("type", jewel.getType().toString());
                    rowList.add(jewelData);
                } else {
                    rowList.add(null);
                }
            }
            grid.add(rowList);
        }

        response.put("grid", grid);
        response.put("score", player.getScore());
        response.put("time", timeRemaining);

        return response;
    }


    @PostMapping("/bejeweled/start")
    public ResponseEntity<Void> startGame(@RequestBody Map<String, String> request, Model model) {
        String playerName = request.get("playerName");
        if (playerName == null || playerName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        this.player = new Player(playerName);
        consoleUI.setPlayer(player);
        field.initializeBoard();
        gameState = GameState.PLAYING;

        model.addAttribute("player", player);
        System.out.println("Player: " + this.player.getUsername());

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

        return ResponseEntity.ok().build();
    }

    @GetMapping("/bejeweled/time")
    @ResponseBody
    public int getTimeRemaining() {
        return timeRemaining;
    }

    @PostMapping("/api/game/swap")
    public ResponseEntity<Map<String, Object>> swapJewels(@RequestBody Map<String, Integer> swapData) {
        try {

            Integer row = swapData.get("row");
            Integer column = swapData.get("column");
            Integer newRow = swapData.get("newRow");
            Integer newColumn = swapData.get("newColumn");

            System.out.println("Received swap request: row=" + row + ", column=" + column + ", newRow=" + newRow + ", newColumn=" + newColumn);

            if (row == null || column == null || newRow == null || newColumn == null) {
                System.out.println("Missing parameters");
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid parameters"));
            }

            if (moveHandler.isValidMove(row, column, newRow, newColumn) && moveHandler.isSwapValid(row, column, newRow, newColumn)) {
                moveHandler.swapJewels(row, column, newRow, newColumn);
                field.checkMatchesAndRemove(consoleUI.getPlayer());

                Map<String, Object> response = new HashMap<>();
                List<List<Map<String, String>>> grid = new ArrayList<>();
                for (int r = 0; r < field.getHeight(); r++) {
                    List<Map<String, String>> rowList = new ArrayList<>();
                    for (int c = 0; c < field.getWidth(); c++) {
                        var jewel = field.getJewel(r, c);
                        if (jewel != null) {
                            Map<String, String> jewelData = new HashMap<>();
                            jewelData.put("type", jewel.getType().toString());
                            rowList.add(jewelData);
                        } else {
                            rowList.add(null);
                        }
                    }
                    grid.add(rowList);
                }

                response.put("grid", grid);
                response.put("score", player.getScore());
                response.put("time", timeRemaining);

                System.out.println(player.getUsername());
                System.out.println("Score sent: " + player.getScore());
                System.out.println("Time sent: " + timeRemaining);

                return ResponseEntity.ok(response);
            } else {
                System.out.println("Invalid move or swap");
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid swap move"));
            }
        } catch (Exception e) {
            System.out.println("Error processing the swap request");
            e.printStackTrace();  // Log the stack trace for more details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }


//    @GetMapping("/bejeweled")
//    public String bejeweled(@RequestParam(value = "command", required = false) String command,
//                            @RequestParam(value = "row", required = false) Integer row,
//                            @RequestParam(value = "column", required = false) Integer column,
//                            @RequestParam(value = "newRow", required = false) Integer newRow,
//                            @RequestParam(value = "newColumn", required = false) Integer newColumn,
//                            Model model, RedirectAttributes redirectAttributes) {
//
//        if ("swap".equals(command) && row != null && column != null && newRow != null && newColumn != null) {
//            if (moveHandler.isValidMove(row, column, newRow, newColumn) && moveHandler.isSwapValid(row, column, newRow, newColumn)) {
//                moveHandler.swapJewels(row, column, newRow, newColumn);
//                field.checkMatchesAndRemove(consoleUI.getPlayer());
//            } else {
//                System.out.println("Invalid move: swap does not create a sequence.");
//            }
//        } else if ("new".equals(command)) {
//            this.field = new Field(8, 8);
//        } else if ("stop".equals(command)) {
//            gameState = GameState.STOPPED;
//            return endGame(redirectAttributes);
//        }
//
//        if (player.getScore() >= 400) {
//            gameState = GameState.SOLVED;
//            return endGame(redirectAttributes);
//        }
//
//        if (timeRemaining == 0 || field.getGameState() == GameState.FAILED) {
//            gameState = GameState.FAILED;
//            return endGame(redirectAttributes);
//        }
//
//        model.addAttribute("gameField", this.field);
//        model.addAttribute("timeRemaining", this.timeRemaining);
//        model.addAttribute("playerScore", player.getScore());
//        model.addAttribute("gameState", gameState);
//        return "fragments/bejeweled";
//    }

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
                                 @RequestParam("rating") int rating) {
        commentService.addComment(new Comment("bejeweled", player.getUsername(), comment, new Date()));
        ratingService.setRating(new Rating("bejeweled", player.getUsername(), rating, new Date()));
        return "redirect:/bejeweled/end";
    }


     */
}
