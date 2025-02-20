package sk.tuke.kpi.kp.bejeweled.service;


import sk.tuke.kpi.kp.bejeweled.entity.Rating;

import java.sql.*;


public class RatingServiceJDBC implements RatingService {

    public static final String URL = "jdbc:postgresql://localhost:5432/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";

    public static final String INSERT = "INSERT INTO rating (game, player, rating, ratedOn) VALUES (?, ?, ?, ?)";
    public static final String SELECT_AVG_RATING = "SELECT AVG(rating) FROM rating WHERE game = ?";
    public static final String SELECT_RATING = "SELECT rating FROM rating WHERE game = ? AND player = ?";
    public static final String DELETE = "DELETE FROM rating";

    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, rating.getGame());
            statement.setString(2, rating.getPlayer());
            statement.setInt(3, rating.getRating());
            statement.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RatingException("Problem inserting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_AVG_RATING)) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new RatingException("No ratings found for game: " + game);
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem retrieving average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_RATING)) {
            statement.setString(1, game);
            statement.setString(2, player);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new RatingException("No rating found for player: " + player + " and game: " + game);
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem retrieving rating", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting rating", e);
        }
    }

}
