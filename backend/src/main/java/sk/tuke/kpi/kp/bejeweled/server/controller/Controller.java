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

@Scope(WebApplicationContext.SCOPE_SESSION)
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
@SessionAttributes({"player", "field", "moveHandler", "consoleUI"})
@RestController
@RequestMapping("/bejeweled")
public class Controller {
    private Field field;
    private Player player;
    private MoveHandler moveHandler ;
    private ConsoleUI consoleUI;

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private int timeRemaining;

    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestBody Map<String, String> payload, Model model) {
        String playerName = payload.get("playerName");

        if (playerName == null || playerName.isEmpty()) {
            return ResponseEntity.badRequest().body("Player name is required");
        }

        this.player = new Player(playerName);
        this.field = new Field(8, 8);
        this.field.initializeBoard();
        this.moveHandler = new MoveHandler(this.field);
        this.consoleUI = new ConsoleUI(this.field, 400, 300);

        model.addAttribute("player", player);
        model.addAttribute("field", field);
        model.addAttribute("moveHandler", moveHandler);
        model.addAttribute("consoleUI", consoleUI);

        this.timeRemaining = consoleUI.getTimeRemaining();

        System.out.println("Game started for player: " + playerName);
        return ResponseEntity.ok("Game started");
    }

    @GetMapping("/field")
    public Map<String, Object> getField(Model model) {
        this.player = (Player) model.getAttribute("player");
        if (this.player == null) {
            throw new IllegalStateException("Player not found in session!");
        }

        Map<String, Object> response = new HashMap<>();
        response.putAll(getFieldData(field));

        return response;
    }

    @GetMapping("/score")
    public ResponseEntity<Map<String, Integer>> getScore(Model model) {
        this.player = (Player) model.getAttribute("player");
        if (this.player == null) {
            throw new IllegalStateException("Player not found in session!");
        }

        Map<String, Integer> response = Collections.singletonMap("score", this.player.getScore());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/time")
    public ResponseEntity<Map<String, Integer>> getTimeRemaining(Model model) {
        this.player = (Player) model.getAttribute("player");
        if (this.player == null) {
            throw new IllegalStateException("Player not found in session!");
        }
        Map<String, Integer> response = Collections.singletonMap("time", this.timeRemaining);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stop")
    public void stopGame(@RequestBody Map<String, Integer> payload, HttpSession session) {
        Player player = (Player) session.getAttribute("player");
        if (player == null) {
            throw new IllegalStateException("Player not found in session!");
        }
        field.setGameState(GameState.STOPPED);

        Score score = new Score("bejeweled", player.getUsername(), player.getScore(), new Date(), 300 - payload.get("timeRemaining"));
        scoreService.addScore(score);
    }

    @PostMapping("/swap")
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
                field.swapJewels(row1, col1, row2, col2);
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

    @GetMapping("/end-data")
    public Map<String, Object> getEndGameData() {
        Map<String, Object> data = new HashMap<>();
        data.put("topScores", scoreService.getTopScores("bejeweled"));
        data.put("averageRating", ratingService.getAverageRating("bejeweled"));
        data.put("comments", commentService.getComments("bejeweled"));
        return data;
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> handleFeedback(@RequestParam("comment") String comment,
                                                 @RequestParam("rating") int rating, HttpSession session) {
        Player currentPlayer = (Player) session.getAttribute("player");
        commentService.addComment(new Comment("bejeweled", currentPlayer.getUsername(), comment, new Date()));
        ratingService.setRating(new Rating("bejeweled", currentPlayer.getUsername(), rating, new Date()));
        return ResponseEntity.ok("Feedback submitted");
    }

    private Map<String, Object> getFieldData(Field field) {
        Map<String, Object> fieldData = new HashMap<>();
        List<List<Map<String, Object>>> grid = new ArrayList<>();

        for (int row = 0; row < field.getHeight(); row++) {
            List<Map<String, Object>> rowList = new ArrayList<>();
            for (int col = 0; col < field.getWidth(); col++) {
                Jewel jewel = field.getJewel(row, col);
                if (jewel != null) {
                    Map<String, Object> jewelData = new HashMap<>();
                    jewelData.put("type", jewel.getType().toString());
                    jewelData.put("bomb", jewel.isBomb());
                    rowList.add(jewelData);
                } else {
                    rowList.add(null);
                }
            }
            grid.add(rowList);
        }

        fieldData.put("grid", grid);
        return fieldData;
    }

    @PostMapping("/bomb")
    public ResponseEntity<Map<String, Object>> activateBomb(@RequestBody Map<String, Integer> pos, HttpSession session) {
        Field field = (Field) session.getAttribute("field");
        Player player = (Player) session.getAttribute("player");

        if (field == null || player == null) {
            return ResponseEntity.status(400).body(Collections.singletonMap("error", "Game not initialized"));
        }

        List<int[]> affectedPositions = field.activateBomb(pos.get("row"), pos.get("col"), player);

        Map<String, Object> response = new HashMap<>();
        response.put("affected", affectedPositions);
        response.put("score", player.getScore());
        response.put("field", getFieldData(field));

        return ResponseEntity.ok(response);
    }

}
