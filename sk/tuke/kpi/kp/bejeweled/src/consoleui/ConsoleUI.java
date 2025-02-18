package sk.tuke.kpi.kp.bejeweled.src.consoleui;

import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.CommentServiceJDBC;
import sk.tuke.gamestudio.service.RatingServiceJDBC;
import sk.tuke.gamestudio.service.ScoreServiceJDBC;
import sk.tuke.kpi.kp.bejeweled.src.core.Field;
import sk.tuke.kpi.kp.bejeweled.src.core.GameState;
import sk.tuke.kpi.kp.bejeweled.src.core.MoveHandler;
import sk.tuke.kpi.kp.bejeweled.src.core.Player;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUI {
    private Field field;
    private Player player;
    private MoveHandler moveHandler;
    private Scanner scanner;
    private int winScore;
    private int timeRemaining;
    private Timer timer;

    private CommentServiceJDBC commentService;
    private RatingServiceJDBC ratingService;
    private ScoreServiceJDBC scoreService;

    public ConsoleUI(Field field, int winScore, int timeLimitInSeconds) {
        this.field = field;
        this.moveHandler = new MoveHandler(field);
        this.scanner = new Scanner(System.in);
        this.winScore = winScore;
        this.timeRemaining = timeLimitInSeconds;
        this.timer = new Timer();

        this.commentService = new CommentServiceJDBC();
        this.ratingService = new RatingServiceJDBC();
        this.scoreService = new ScoreServiceJDBC();
    }

    public void play(Field field) {
        System.out.print("Welcome to Bejeweled!\nEnter your name: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        player = new Player(name);

        startTimer();

        while (field.getState() == GameState.PLAYING && timeRemaining > 0) {
            if (player.getScore() >= winScore) {
                field.setState(GameState.SOLVED);
                break;
            }

            show();
            handleInput();
        }

        timer.cancel();

        if (field.getState() == GameState.SOLVED) {
            System.out.println("You won!");
            scoreService.addScore(new Score("Bejeweled", player.getUsername(), player.getScore(), new Date()));
            handleRestart();
        } else if (field.getState() == GameState.STOPPED) {
            System.out.println("Game over!");
            scoreService.addScore(new Score("Bejeweled", player.getUsername(), player.getScore(), new Date()));
            handleRestart();
        } else {
            field.setState(GameState.FAILED);
            System.out.println("Time's up! You lost.");
            scoreService.addScore(new Score("Bejeweled", player.getUsername(), player.getScore(), new Date()));
            handleRestart();
        }
    }

    private void show() {
        field.printField();
        System.out.println("Current score: " + player.getScore() + " | Win score: " + winScore);
        System.out.println("Time remaining: " + timeRemaining + " seconds");
    }

    private void handleInput() {
        Pattern pattern = Pattern.compile("([A-H])([0-8])\\s([A-H])([0-8])");
        System.out.print("Enter move (eg. A1 A2) or X for game stop: ");
        String input = scanner.nextLine().toUpperCase().trim();

        if (input.equals("X")) {
            field.setState(GameState.STOPPED);
            System.out.println("You lost!");
            return;
        }

        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            int y1 = matcher.group(1).charAt(0) - 'A';
            int x1 = Integer.parseInt(matcher.group(2));
            int y2 = matcher.group(3).charAt(0) - 'A';
            int x2 = Integer.parseInt(matcher.group(4));

            if (moveHandler.isValidMove(x1, y1, x2, y2)) {
                moveHandler.swapJewels(x1, y1, x2, y2);
                field.checkMatchesAndRemove(player);
            } else {
                System.out.println("Invalid move. Try again.");
            }
        } else {
            System.out.println("Invalid input format. Please use the format like A1 A2.");
        }
    }

    private void handleRestart() {
        System.out.println("Prajete si začatie novej hry (A/N)?");
        String input = scanner.nextLine().trim().toUpperCase();

        if (input.equals("A")) {
            System.out.println("Začíname novú hru...");

            field.initializeBoard();
            field.setState(GameState.PLAYING);
            player.resetScore();
            timeRemaining = 300;

            timer.cancel();
            timer.purge();
            timer = new Timer();
            startTimer();

            play(field);
        } else {
            System.out.println("Chceš vidieť top 10 hráčov? (A/N)");
            String input2 = scanner.nextLine().trim().toUpperCase();
            if (input2.equals("A")) {
                showTopPlayers();
            }else{
                System.out.println("Ďakujeme za hranie!");
                System.exit(0);
            }

        }
    }

    public void showTopPlayers() {
        List<Score> topScores = scoreService.getTopScores("Bejeweled");
        System.out.println("TOP 10 hráčov:");

        for (int i = 0; i < topScores.size(); i++) {
            Score score = topScores.get(i);
            System.out.println((i + 1) + ". " + score.getPlayer() + " - " + score.getPoints());
        }
    }


    private void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    timeRemaining--;
                } else {
                    field.setState(GameState.FAILED);
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

}
