package sk.tuke.kpi.kp.bejeweled.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Score {
    private String game;
    private String player;
    private int points;
    private Date playedOn;
}
