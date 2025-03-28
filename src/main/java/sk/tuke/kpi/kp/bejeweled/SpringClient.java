package sk.tuke.kpi.kp.bejeweled;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.tuke.kpi.kp.bejeweled.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.bejeweled.core.Field;

@SpringBootApplication
@Configuration
public class SpringClient {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringClient.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public CommandLineRunner runner(ConsoleUI ui) {
        return args -> ui.play();
    }

    @Bean
    public ConsoleUI consoleUI(Field field) {
        return new ConsoleUI(field, 400, 300);
    }

    @Bean
    public Field field() {
        return new Field(8, 8);
    }
}