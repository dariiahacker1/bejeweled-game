package sk.tuke.kpi.kp.bejeweled.consoleui;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sk.tuke.kpi.kp.bejeweled.core.Field;
import sk.tuke.kpi.kp.bejeweled.core.GameState;
import sk.tuke.kpi.kp.bejeweled.core.MoveHandler;
import sk.tuke.kpi.kp.bejeweled.core.Player;
import sk.tuke.kpi.kp.bejeweled.service.*;
import sk.tuke.kpi.kp.bejeweled.entity.Comment;
import sk.tuke.kpi.kp.bejeweled.entity.Rating;
import sk.tuke.kpi.kp.bejeweled.entity.Score;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Getter
@Setter
public class ConsoleUI {
    private final Field field;
    private Player player;
    private final MoveHandler moveHandler;
    private final Scanner scanner = new Scanner(System.in);
    private final int winScore;
    private int timeRemaining;
    private Timer timer = new Timer();

    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private ScoreService scoreService;

    @Autowired
    public ConsoleUI(Field field, @Value("${game.winScore}") int winScore, @Value("${game.timeLimit}") int timeLimitInSeconds) {
        this.field = field;
        this.moveHandler = new MoveHandler(field);
        this.winScore = winScore;
        this.timeRemaining = timeLimitInSeconds;
    }

    public void play() {
        initializePlayer();
        startTimer();

        while (field.getGameState() == GameState.PLAYING && timeRemaining > 0) {
            if (player.getScore() >= winScore) {
                field.setGameState(GameState.SOLVED);
                break;
            }
            showCurrentState();
            handleInput();
        }

        timer.cancel();
     //   endGame();
    }

    private void initializePlayer() {
        System.out.print("Welcome to Bejeweled!\nEnter your name: ");
        String name = scanner.nextLine().trim();
        player = new Player(name);
    }


    private void showCurrentState() {
        field.printField();
        System.out.println("Current score: " + player.getScore() + " | Win score: " + winScore);
        System.out.println("Time remaining: " + timeRemaining + " seconds");
    }

    private void handleInput() {
        System.out.print("Enter move (eg. A1 A2) or X for game stop: ");
        String input = scanner.nextLine().toUpperCase().trim();

        if (input.equals("X")) {
            field.setGameState(GameState.STOPPED);
         //   endGame();
            return;
        }

        if (processMove(input)) return;

        System.out.println("Invalid input. Try again.");
    }

    private boolean processMove(String input) {
        Pattern pattern = Pattern.compile("([A-H])([0-8])\\s([A-H])([0-8])");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            int y1 = matcher.group(1).charAt(0) - 'A';
            int x1 = Integer.parseInt(matcher.group(2));
            int y2 = matcher.group(3).charAt(0) - 'A';
            int x2 = Integer.parseInt(matcher.group(4));

            if (moveHandler.isValidMove(x1, y1, x2, y2) && moveHandler.isSwapValid(x1, y1, x2, y2)) {
                field.swapJewels(x1, y1, x2, y2);
                field.checkMatchesAndRemove(player);
                return true;
            }
            System.out.println("Invalid move: swap does not create a sequence.");
        }
        return false;
    }

//    private void endGame() {
//        scoreService.addScore(new Score("bejeweled", player.getUsername(), player.getScore(), new Date()));
//
//        if (field.getGameState() == GameState.PLAYING && timeRemaining == 0) {
//            field.setGameState(GameState.FAILED);
//        }
//
//        switch (field.getGameState()) {
//            case SOLVED -> System.out.println("You won!");
//            case STOPPED -> System.out.println("Game over!");
//            case FAILED -> System.out.println("Time's up! You lost.");
//        }
//
//        handleRestart();
//    }

    private void restartGame() {
        System.out.println("Starting a new game...");
        field.initializeBoard();
        field.setGameState(GameState.PLAYING);
        player.resetScore();
        timeRemaining = 300;
        timer.cancel();
        timer.purge();
        timer = new Timer();
        startTimer();
        play();
    }

    private void handleRestart() {
        if (askYesNo("Do you want to start a new game? (A/N)")) {
            restartGame();
        } else {
            if (askYesNo("Do you want to see the top 10 players? (A/N)")) showTopPlayers();
            if (askYesNo("Do you want to rate the game? (A/N)")) gameRating();
            if (askYesNo("Do you want to write a comment? (A/N)")) commentWriting();

            System.out.println("Thank you for playing!");
            System.exit(0);
        }
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().toUpperCase().trim();
    }

    private boolean askYesNo(String prompt) {
        String input = getInput(prompt);
        return input.equals("A");
    }

    private void showTopPlayers() {
        List<Score> topScores = scoreService.getTopScores("bejeweled");
        System.out.println("TOP 10 players:");

        for (int i = 0; i < topScores.size(); i++) {
            Score score = topScores.get(i);
            System.out.println((i + 1) + ". " + score.getPlayer() + " - " + score.getPoints());
        }
    }

    private void gameRating() {
        int attempts = 3;
        boolean validInput = false;

        while (attempts > 0 && !validInput) {
            System.out.println("How would you rate the game Bejeweled? (1-5)");
            String input = scanner.nextLine().trim();

            try {
                int ratingValue = Integer.parseInt(input);

                if (ratingValue < 1 || ratingValue > 5) {
                    System.out.println("Invalid rating. Please enter a number from 1 to 5.");
                } else {
                    ratingService.setRating(new Rating("Bejeweled", player.getUsername(), ratingValue, new Date()));
                    System.out.println("Thank you for your rating!");
                    validInput = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number from 1 to 5.");
            }

            attempts--;
        }

        if (!validInput) {
            System.out.println("Unfortunately, you have unsuccessfully attempted the rating 3 times. Please try again later.");
        }
    }

    private void commentWriting() {
        System.out.println("What would you like to say about the game?");
        String input = scanner.nextLine().trim();

        commentService.addComment(new Comment("bejeweled", player.getUsername(), input, new Date()));
        System.out.println("Thank you for your comment!");
    }

    private void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    timeRemaining--;
                } else {
                    field.setGameState(GameState.FAILED);
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

}
