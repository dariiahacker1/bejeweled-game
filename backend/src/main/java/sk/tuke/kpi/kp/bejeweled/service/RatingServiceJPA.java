package sk.tuke.kpi.kp.bejeweled.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import sk.tuke.kpi.kp.bejeweled.entity.Rating;

import java.util.Date;

@Service
@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        try {
            int updatedRows = entityManager.createNamedQuery("Rating.setRating")
                    .setParameter("newRating", rating.getRating())
                    .setParameter("newDate", new Date())
                    .setParameter("game", rating.getGame())
                    .setParameter("player", rating.getPlayer())
                    .executeUpdate();

            if(updatedRows == 0) {
                rating.setRatedOn(new Date());
                entityManager.persist(rating);
            }
        } catch (Exception e) {
            throw new RatingException("Problem setting rating", e);
        }
    }


    @Override
    public int getAverageRating(String game) throws RatingException {
        try {
            return ((Number) entityManager.createNamedQuery("Rating.getAverageRating")
                    .setParameter("game", game)
                    .getSingleResult()).intValue();
        } catch (Exception e) {
            throw new RatingException("Problem retrieving average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            return ((Number) entityManager.createNamedQuery("Rating.getRatingByPlayer")
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult()).intValue();
        } catch (Exception e) {
            throw new RatingException("Problem retrieving rating", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        entityManager.createNamedQuery("Rating.resetRatings").executeUpdate();
    }
}