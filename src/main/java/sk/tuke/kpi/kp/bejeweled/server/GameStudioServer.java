package sk.tuke.kpi.kp.bejeweled.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import sk.tuke.kpi.kp.bejeweled.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.core.Field;
import sk.tuke.kpi.kp.bejeweled.core.MoveHandler;
import sk.tuke.kpi.kp.bejeweled.core.Player;
import sk.tuke.kpi.kp.bejeweled.service.*;

@SpringBootApplication
@EntityScan("sk.tuke.kpi.kp.bejeweled.entity")
@EnableScheduling
public class GameStudioServer {
    public static void main(String[] args) {
        SpringApplication.run(GameStudioServer.class, args);
    }

    @Bean
    public Field field() {
        return new Field(8, 8);
    }

    @Bean
    public Player player() {
        return new Player("GUEST");
    }

    @Bean
    public MoveHandler moveHandler(Field field) {
        return new MoveHandler(field);
    }

    @Bean
    public ConsoleUI consoleUI(Field field) {
        return new ConsoleUI(field, 400, 300);
    }

    @Bean
    public ScoreService scoreService() {return new ScoreServiceJPA();}

    @Bean
    public CommentService commentService() {return new CommentServiceJPA();}

    @Bean
    public RatingService ratingService() {return new RatingServiceJPA();}
}