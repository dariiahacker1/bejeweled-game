import React from 'react';
import logo from "../../images/logo.png";
import scoreimg from "../../images/score.png";
import timer from "../../images/timer.png";
import '../../css/styles.css';

export const GameControls = ({score, timeRemaining, onStopGame}) => {
    return (
        <div className="left-part">
            <img src={logo} alt="BEJEWELED" className="logo"/>
            <div className="score-board">
                <img src={scoreimg} alt="score board" className="board"/>
                <div className="score-counter">
                    <p id="score-display">{score}</p>
                </div>
            </div>
            <div className="timer-board">
                <img src={timer} alt="timer board" className="board"/>
                <div className="timer-counter">
                    <p id="timer-display">{timeRemaining}</p>
                </div>
            </div>

            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                flexDirection: 'row',
                gap: '50px',
            }}>
            </div>

            <div className="controls">
                <button onClick={onStopGame} className="stop-game-button">
                    Stop Game
                </button>
            </div>
        </div>
    )
}