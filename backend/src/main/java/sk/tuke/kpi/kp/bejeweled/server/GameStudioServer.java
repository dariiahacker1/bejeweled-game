package sk.tuke.kpi.kp.bejeweled.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.SessionScope;
import sk.tuke.kpi.kp.bejeweled.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.core.Field;
import sk.tuke.kpi.kp.bejeweled.core.MoveHandler;
import sk.tuke.kpi.kp.bejeweled.core.Player;
import sk.tuke.kpi.kp.bejeweled.service.*;

@SpringBootApplication
@ComponentScan(basePackages = {
        "sk.tuke.kpi.kp.bejeweled.auth",
        "sk.tuke.kpi.kp.bejeweled.server",
        "sk.tuke.kpi.kp.bejeweled.service"
})
@EntityScan(basePackages = {
        "sk.tuke.kpi.kp.bejeweled.entity",
        "sk.tuke.kpi.kp.bejeweled.auth"
})
@EnableJpaRepositories(basePackages = "sk.tuke.kpi.kp.bejeweled.auth")
@EnableScheduling
public class GameStudioServer {
    public static void main(String[] args) {
        SpringApplication.run(GameStudioServer.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Field field() {return new Field(8, 8);}

//    @Bean
//    @SessionScope
//    public Player player() {return new Player("GUEST");}

    @Bean
    public MoveHandler moveHandler(Field field) {return new MoveHandler(field);}

    @Bean
    public ConsoleUI consoleUI(Field field) {return new ConsoleUI(field, 400, 300);}

    @Bean
    public ScoreService scoreService() {return new ScoreServiceJPA();}

    @Bean
    public CommentService commentService() {return new CommentServiceJPA();}

    @Bean
    public RatingService ratingService() {return new RatingServiceJPA();}

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}