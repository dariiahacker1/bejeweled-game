import React, {useEffect, useState} from 'react';
import {useLocation} from 'react-router-dom';
import '../css/styles.css';
import gameover from '../images/gameover.png'


function GameOverPage() {

    const location = useLocation();
    const lastWords = location.state?.lastWords || "Thanks for playing!";

    const [averageRating, setAverageRating] = useState(null);
    const [topScores, setTopScores] = useState([]);
    const [comments, setComments] = useState([]);
    const [loading, setLoading] = useState(true);

    const [feedbackComment, setFeedbackComment] = useState("");
    const [feedbackRating, setFeedbackRating] = useState(1);

    useEffect(() => {
        fetch("http://localhost:8080/bejeweled/end-data")
            .then(response => response.json())
            .then(data => {
                setAverageRating(data.averageRating);
                setTopScores(data.topScores);
                setComments(data.comments);
                setLoading(false);
            })
            .catch(error => {
                console.error("Error fetching endgame data:", error);
                setLoading(false);
            });
    }, []);

    const handleFeedbackSubmit = (e) => {
        e.preventDefault();

        fetch("http://localhost:8080/bejeweled/feedback", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            credentials: 'include',
            body: new URLSearchParams({
                comment: feedbackComment,
                rating: feedbackRating,
            }),
        })
            .then((res) => {
                if (res.ok) {
                    alert("Feedback submitted!");
                    setFeedbackComment("");
                    setFeedbackRating(1);
                    // Refresh data
                    return fetch("http://localhost:8080/bejeweled/end-data")
                        .then(response => response.json())
                        .then(data => {
                            setAverageRating(data.averageRating);
                            setTopScores(data.topScores);
                            setComments(data.comments);
                        });
                } else {
                    alert("Failed to submit feedback");
                }
            })
            .catch((err) => console.error("Error submitting feedback:", err));
    };

    if (loading) {
        return <p>Loading endgame data...</p>;
    }

    return (
        <div className="final bejeweled">
            <img src={gameover} alt="game over" className="gameover"/>

            <div className="end-containers">
                <div className="average-rating">
                    <h2>Average Rating</h2>
                    <p>{averageRating}</p>
                </div>

                <div className="game-end-container">
                    <p className="result">Game Over! {lastWords}</p>

                    <h2>Top 10 Players</h2>
                    <table>
                        <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Player</th>
                            <th>Score</th>
                            {/*<th>Playing Time</th>*/}
                        </tr>
                        </thead>
                        <tbody>
                        {topScores.map((score, index) => (
                            <tr key={index}>
                                <td>{index + 1}</td>
                                <td>{score.player}</td>
                                <td>{score.points}</td>
                                {/*<td>{score.playingTime}</td>*/}
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    <form onSubmit={handleFeedbackSubmit} className="feedback-form">
                        <div>
                            <label htmlFor="comment">Your Comment:</label>
                            <br/>
                            <textarea
                                id="comment"
                                name="comment"
                                rows="4"
                                cols="50"
                                placeholder="Share your thoughts..."
                                required
                                value={feedbackComment}
                                onChange={(e) => setFeedbackComment(e.target.value)}
                            ></textarea>
                        </div>
                        <div>
                            <label htmlFor="rating">Your Rating (1-5):</label>
                            <br/>
                            <input
                                type="number"
                                id="rating"
                                name="rating"
                                min="1"
                                max="5"
                                required
                                value={feedbackRating}
                                onChange={(e) => setFeedbackRating(e.target.value)}
                            />
                        </div>
                        <button type="submit" className="btn btn-submit">Submit Feedback</button>
                    </form>

                    <a href="/bejeweled/login" className="btn btn-restart">Restart Game</a>
                </div>

                <div className="comments">
                    <h2>Comments</h2>
                    {comments.map((comment, i) => (
                        <div key={i} className="comment">
                            <strong>{comment.player}</strong>
                            <p>{comment.comment}</p>
                            <small>{new Date(comment.commentedOn).toLocaleString()}</small>
                            <hr/>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default GameOverPage;