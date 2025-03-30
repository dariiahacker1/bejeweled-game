package sk.tuke.kpi.kp.bejeweled.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.kpi.kp.bejeweled.entity.Rating;

@Service
public class RatingServiceRestClient implements RatingService {

    private final String url = "http://localhost:8080/api/rating";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void setRating(Rating rating) {
        restTemplate.postForEntity(url, rating, Rating.class);
    }

    @Override
    public int getRating(String game, String player) {
        return restTemplate.getForObject(url + "/" + game + "/" + player, Integer.class);
    }

    @Override
    public int getAverageRating(String game) {
        return restTemplate.getForObject(url + "/average/" + game, Integer.class);
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}
