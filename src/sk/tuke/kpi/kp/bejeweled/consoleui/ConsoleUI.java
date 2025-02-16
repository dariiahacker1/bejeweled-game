package sk.tuke.kpi.kp.bejeweled.consoleui;

import sk.tuke.kpi.kp.bejeweled.core.Field;
import sk.tuke.kpi.kp.bejeweled.core.GameState;
import sk.tuke.kpi.kp.bejeweled.core.MoveHandler;
import sk.tuke.kpi.kp.bejeweled.core.Player;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class ConsoleUI {
    private Field field;
    private Player player;
    private MoveHandler moveHandler;
    private Scanner scanner;
    private int winScore;
    private int timeRemaining;
    private Timer timer;

    public ConsoleUI(Field field, Player player, int winScore, int timeLimitInSeconds) {
        this.field = field;
        this.player = player;
        this.moveHandler = new MoveHandler(field);
        this.scanner = new Scanner(System.in);
        this.winScore = winScore;
        this.timeRemaining = timeLimitInSeconds;
        this.timer = new Timer();
    }

    public void play() {
        startTimer();

        while (field.getState() == GameState.PLAYING && timeRemaining > 0) {
            if (player.getScore() >= winScore) {
                field.setState(GameState.SOLVED);
                break;
            }

            field.printField();
            System.out.println("Current score: " + player.getScore() + " | Win score: " + winScore);
            System.out.println("Time remaining: " + timeRemaining + " seconds");

            handleInput();
        }

        timer.cancel();

        if (field.getState() == GameState.SOLVED) {
            System.out.println("You won!");
        } else {
            field.setState(GameState.FAILED);
            System.out.println("Time's up! You lost.");
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

    private void handleInput() {
        System.out.print("Enter move (x1 y1 x2 y2): ");
        int x1 = scanner.nextInt(), y1 = scanner.nextInt();
        int x2 = scanner.nextInt(), y2 = scanner.nextInt();

        if(moveHandler.isValidMove(x1, y1, x2, y2)) {
            moveHandler.swapJewels(x1, y1, x2, y2);
            field.checkMatchesAndRemove(player);
        }else{
            System.out.println("Invalid move");
        }

    }
}
