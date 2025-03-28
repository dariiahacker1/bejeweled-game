package sk.tuke.kpi.kp.bejeweled.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.NamedQuery;

import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@NamedQuery( name = "Score.getTopScores",
        query = "SELECT s FROM Score s WHERE s.game=:game ORDER BY s.points DESC")
@NamedQuery( name = "Score.resetScores",
        query = "DELETE FROM Score")
//@NamedQuery(
//        name = "Score.addScore",
//        query = "INSERT INTO Score (game, player, points, playedOn) VALUES (:game, :player, :points, :playedOn)"
//)
public class Score implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ident;

    private String game;
    private String player;
    private int points;
    private Date playedOn;

    public Score(String game, String player, int points, Date playedOn) {
        this.game = game;
        this.player = player;
        this.points = points;
        this.playedOn = playedOn;
    }
}
