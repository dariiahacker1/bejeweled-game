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

@NamedQuery(
        name = "Rating.setRating",
        query = "UPDATE Rating r SET r.rating = :newRating WHERE r.id = :id"
)

@NamedQuery(
        name = "Rating.getAverageRating",
        query = "SELECT AVG(r.rating) FROM Rating r WHERE r.game = :game"
)
@NamedQuery(
        name = "Rating.getRatingByPlayer",
        query = "SELECT r.rating FROM Rating r WHERE r.game = :game AND r.player = :player"
)
@NamedQuery(
        name = "Rating.resetRatings",
        query = "DELETE FROM Rating"
)
public class Rating implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ident;

    private String game;
    private String player;
    private int rating;
    private Date ratedOn;

    public Rating(String game, String player, int rating, Date ratedOn) {
        this.game = game;
        this.player = player;
        this.rating = rating;
        this.ratedOn = ratedOn;
    }
}
