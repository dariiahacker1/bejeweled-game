package sk.tuke.kpi.kp.bejeweled.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.kpi.kp.bejeweled.entity.Rating;
import sk.tuke.kpi.kp.bejeweled.service.RatingService;


@RestController
@RequestMapping("/api/rating")
public class RatingServiceRest {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/{game}/{player}")
    public int getRating(@PathVariable String game, @PathVariable String player) {return ratingService.getRating(game, player);}

    @GetMapping("/average/{game}")
    public int getAverageRating(@PathVariable String game) {return ratingService.getAverageRating(game);}

    @PostMapping
    public void setRating(@RequestBody Rating rating) {ratingService.setRating(rating);}
}
