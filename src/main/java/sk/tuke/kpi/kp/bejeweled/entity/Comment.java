package sk.tuke.kpi.kp.bejeweled.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(
        name = "Comment.getComments",
        query = "SELECT c FROM Comment c WHERE c.game = :game ORDER BY c.commentedOn DESC"
)
@NamedQuery(
        name = "Comment.resetComments",
        query = "DELETE FROM Comment"
)
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ident;

    private String game;
    private String player;
    private String comment;
    private Date commentedOn;

    public Comment(String game, String player, String comment, Date commentedOn) {
        this.game = game;
        this.player = player;
        this.comment = comment;
        this.commentedOn = commentedOn;
    }
}
