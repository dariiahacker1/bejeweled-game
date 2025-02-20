package sk.tuke.kpi.kp.bejeweled.service;

import java.util.List;
import sk.tuke.kpi.kp.bejeweled.entity.Score;

public interface ScoreService {
    void addScore(Score score) throws ScoreException;
    List<Score> getTopScores(String game) throws ScoreException;
    void reset() throws ScoreException;
}
