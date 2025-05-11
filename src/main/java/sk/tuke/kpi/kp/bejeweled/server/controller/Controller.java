package sk.tuke.kpi.kp.bejeweled.server.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.kpi.kp.bejeweled.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.core.*;
import sk.tuke.kpi.kp.bejeweled.entity.Comment;
import sk.tuke.kpi.kp.bejeweled.entity.Rating;
import sk.tuke.kpi.kp.bejeweled.entity.Score;
import sk.tuke.kpi.kp.bejeweled.service.CommentService;
import sk.tuke.kpi.kp.bejeweled.service.RatingService;
import sk.tuke.kpi.kp.bejeweled.service.ScoreService;

import java.util.*;

@org.springframework.stereotype.Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
@SessionAttributes({"player", "field", "moveHandler", "consoleUI"})
public class Controller {
    private Field field;
    private Player player;
    private MoveHandler moveHandler;
    private ConsoleUI consoleUI;

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private int timeRemaining;

    @PostMapping("/bejeweled/start")
    public ResponseEntity<String> startGame(@RequestBody Map<String, String> payload, Model model) {
        String playerName = payload.get("playerName");
        this.player = new Player(playerName);
        this.field = new Field(8, 8);
        this.moveHandler = new MoveHandler(field);
        this.consoleUI = new ConsoleUI(field, 400, 300);

        model.addAttribute("player", player);
        model.addAttribute("field", field);
        model.addAttribute("moveHandler", moveHandler);
        model.addAttribute("consoleUI", consoleUI);

        this.timeRemaining = consoleUI.getTimeRemaining();

        System.out.println("Game started for player: " + playerName);
        return ResponseEntity.ok("Game started");
    }

    @GetMapping("/bejeweled/field")
    @ResponseBody
    public Map<String, Object> getField(Model model) {
        this.player = (Player) model.getAttribute("player");
        if (this.player == null) {
            throw new IllegalStateException("Player not found in session!");
        }

        Map<String, Object> response = new HashMap<>();
        List<List<Map<String, String>>> grid = new ArrayList<>();

        for (int row = 0; row < field.getHeight(); row++) {
            List<Map<String, String>> rowList = new ArrayList<>();
            for (int col = 0; col < field.getWidth(); col++) {
                Jewel jewel = field.getJewel(col, row);
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

        return response;
    }

    @GetMapping("/bejeweled/score")
    public ResponseEntity<Map<String, Integer>> getScore(Model model) {
        this.player = (Player) model.getAttribute("player");
        if (this.player == null) {
            throw new IllegalStateException("Player not found in session!");
        }

        Map<String, Integer> response = Collections.singletonMap("score", this.player.getScore());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bejeweled/time")
    public ResponseEntity<Map<String, Integer>> getTimeRemaining(Model model) {
        this.player = (Player) model.getAttribute("player");
        if (this.player == null) {
            throw new IllegalStateException("Player not found in session!");
        }
        Map<String, Integer> response = Collections.singletonMap("time", this.timeRemaining);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bejeweled/stop")
    public void stopGame(Model model) {
        this.player = (Player) model.getAttribute("player");
        if (this.player == null) {
            throw new IllegalStateException("Player not found in session!");
        }
        field.setGameState(GameState.STOPPED);

        Score score = new Score("bejeweled", player.getUsername(), player.getScore(), new Date());
        scoreService.addScore(score);
    }

    @PostMapping("/bejeweled/swap")
    public ResponseEntity<?> swapJewels(@RequestBody Map<String, Integer> payload, HttpSession session) {
        Field currentField = (Field) session.getAttribute("field");
        MoveHandler currentHandler = (MoveHandler) session.getAttribute("moveHandler");
        Player currentPlayer = (Player) session.getAttribute("player");

        if (currentPlayer == null || currentField == null || currentHandler == null) {
            return ResponseEntity.status(400).body("Game not properly initialized!");
        }

        int row1 = payload.get("row1");
        int col1 = payload.get("col1");
        int row2 = payload.get("row2");
        int col2 = payload.get("col2");

        if (currentHandler.isValidMove(row1, col1, row2, col2)) {
            if (currentHandler.isSwapValid(row1, col1, row2, col2)) {
                currentHandler.swapJewels(row1, col1, row2, col2);
                currentField.checkMatchesAndRemove(currentPlayer);

                session.setAttribute("field", currentField);
                session.setAttribute("moveHandler", currentHandler);

                return ResponseEntity.ok("Swap successful");
            } else {
                return ResponseEntity.badRequest().body("Swap doesn't create a valid match");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid move - positions must be adjacent");
        }
    }

    @GetMapping("/bejeweled/end-data")
    @ResponseBody
    public Map<String, Object> getEndGameData() {
        Map<String, Object> data = new HashMap<>();
        data.put("topScores", scoreService.getTopScores("bejeweled"));
        data.put("averageRating", ratingService.getAverageRating("bejeweled"));
        data.put("comments", commentService.getComments("bejeweled"));
        return data;
    }

    @PostMapping("/bejeweled/feedback")
    @ResponseBody
    public ResponseEntity<String> handleFeedback(@RequestParam("comment") String comment,
                                                 @RequestParam("rating") int rating, HttpSession session) {
        Player currentPlayer = (Player) session.getAttribute("player");
        commentService.addComment(new Comment("bejeweled", currentPlayer.getUsername(), comment, new Date()));
        ratingService.setRating(new Rating("bejeweled", currentPlayer.getUsername(), rating, new Date()));
        return ResponseEntity.ok("Feedback submitted");
    }

}