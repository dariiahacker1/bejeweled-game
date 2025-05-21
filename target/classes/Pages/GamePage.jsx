import React, {useEffect, useState, useRef} from 'react';
import {useNavigate} from 'react-router-dom';
import './CSS/styles.css';
import logo from './images/logo.png';
import scoreimg from './images/score.png';
import timer from './images/timer.png';

import green from './images/tuke_logo_green.svg.png';
import white from './images/tuke_logo_white.svg.png';
import blue from './images/tuke_logo_blue.svg.png';
import orange from './images/tuke_logo_orange.svg.png';
import red from './images/tuke_logo_red.svg.png';
import yellow from './images/tuke_logo_yellow.svg.png';
import purple from './images/tuke_logo_purple.svg.png';

import track1 from './music/chill_vibes.mp3';
import track2 from './music/epic_mode.mp3';
import track3 from './music/classic_arcade.mp3';

import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Typography, Button, Box, Slider, Select, MenuItem, FormControl, InputLabel
} from '@mui/material';


function GamePage() {
    const [gameField, setGameField] = useState([]);
    const [score, setScore] = useState(0);
    const [timeRemaining, setTimeRemaining] = useState(300);
    const [selected, setSelected] = useState(null);
    const navigate = useNavigate();

    const [helpOpen, setHelpOpen] = useState(false);
    const [settingsOpen, setSettingsOpen] = useState(false);

    const [musicVolume, setMusicVolume] = useState(50);
    const [noiseVolume, setNoiseVolume] = useState(50);

    const [assistantVisible, setAssistantVisible] = useState(false);

    const [selectedMusic, setSelectedMusic] = useState('track1'); // default music
    const musicOptions = [
        { label: 'Chill Vibes', value: 'track1' },
        { label: 'Epic Mode', value: 'track2' },
        { label: 'Classic Arcade', value: 'track3' }
    ];

    const audioFiles = {
        track1: track1,
        track2: track2,
        track3: track3
    };

    const audioRef = useRef(null);

    useEffect(() => {
        if (audioRef.current) {
            audioRef.current.src = audioFiles[selectedMusic];

            audioRef.current.volume = musicVolume / 100;

            if (audioRef.current.paused) {
                audioRef.current.play().catch((error) => {
                    console.error("Audio play error:", error);
                });
            }
        }
    }, [selectedMusic, musicVolume]);


    const jewelImages = {
        GREEN: green,
        WHITE: white,
        BLUE: blue,
        ORANGE: orange,
        RED: red,
        YELLOW: yellow,
        PURPLE: purple,
    };

    const json = JSON.stringify({
        name: "Bejeweled Assistant",
        icon: "https://img.icons8.com/color/96/diamond.png",
        description: "Your Bejeweled strategy helper!",
        initialMessage: "How can I help you with Bejeweled today?",
        initialResponse: "Welcome! Ready to get some tips on gems and combos?",
        systemMessage: "You're talking to an AI trained to assist with Bejeweled strategy. Ask about moves, patterns, or anything game-related.",
        chatLocation: "bejeweledAssistant"
    });

    const speakGptPayload = encodeURIComponent(
        btoa(unescape(encodeURIComponent(json)))
    );

    useEffect(() => {
        const initializeGame = async () => {
            try {
                await fetchGameField();
                await fetchScore();

                const response = await fetch('http://localhost:8080/bejeweled/time', {
                    credentials: 'include'
                });
                if (!response.ok) throw new Error('Failed to fetch time');
                const data = await response.json();
                setTimeRemaining(data.time || 0);
            } catch (error) {
                console.error("Initialization error:", error);
                navigate('/game');
            }
        };

        initializeGame();
    }, [navigate]);

    useEffect(() => {
        if (timeRemaining <= 0) return;

        const interval = setInterval(() => {
            setTimeRemaining(prevTime => {
                if (prevTime <= 1) {
                    clearInterval(interval);
                    return 0;
                }
                return prevTime - 1;
            });
        }, 1000);

        return () => clearInterval(interval);
    }, [timeRemaining]);

    const fetchGameField = async () => {
        try {
            const response = await fetch('http://localhost:8080/bejeweled/field', {
                credentials: 'include'
            });
            if (!response.ok) throw new Error('Failed to fetch field');
            const data = await response.json();
            setGameField(data.grid || []);
            console.log(gameField);
        } catch (error) {
            console.error("Field fetch error:", error);
            throw error;
        }
    };

    const fetchScore = async () => {
        try {
            const response = await fetch('http://localhost:8080/bejeweled/score', {
                credentials: 'include'
            });
            if (!response.ok) throw new Error('Failed to fetch score');
            const data = await response.json();
            setScore(data.score || 0);
        } catch (error) {
            console.error("Score fetch error:", error);
            throw error;
        }
    };

    const handleClick = (e, row, col) => {
        e.preventDefault();
        if (selected) {
            const [prevRow, prevCol] = selected;
            if (prevRow === row && prevCol === col) {
                setSelected(null);
                return;
            }
            swapJewels(prevRow, prevCol, row, col);
            setSelected(null);
        } else {
            setSelected([row, col]);
        }
    };

    const swapJewels = async (row1, col1, row2, col2) => {
        try {
            const response = await fetch('http://localhost:8080/bejeweled/swap', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                credentials: 'include',
                body: JSON.stringify({row1, col1, row2, col2}),
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error);
            }

            const fieldResponse = await fetch('http://localhost:8080/bejeweled/field', {credentials: 'include'});
            const fieldData = await fieldResponse.json();
            setGameField(fieldData.grid || []);

            const scoreResponse = await fetch('http://localhost:8080/bejeweled/score', {credentials: 'include'});
            const scoreData = await scoreResponse.json();
            setScore(scoreData.score || 0);
        } catch (error) {
            console.error("Swap error:", error);
            alert(`Swap failed: ${error.message}`);
        }
    };

    useEffect(() => {
        const checkGameEnd = async () => {
            const WIN_SCORE = 1000;

            if (timeRemaining <= 0) {
                try {
                    await fetch('http://localhost:8080/bejeweled/stop', {
                        method: 'POST',
                        credentials: 'include'
                    });
                    navigate('/end', {
                        state: {
                            lastWords: "Game Over! You ran out of time.",
                        }
                    });
                } catch (error) {
                    console.error("Stop error:", error);
                }
            } else if (score >= WIN_SCORE) {
                try {
                    await fetch('http://localhost:8080/bejeweled/stop', {
                        method: 'POST',
                        credentials: 'include'
                    });
                    navigate('/end', {
                        state: {
                            lastWords: "You won!",
                        }
                    });
                } catch (error) {
                    console.error("Stop error:", error);
                }
            }
        };

        checkGameEnd();
    }, [timeRemaining, score, navigate]);

    const stopGame = async () => {
        try {
            await fetch('http://localhost:8080/bejeweled/stop', {
                method: 'POST',
                credentials: 'include'
            });
            navigate('/end', {
                state: {
                    lastWords: "Game stopped by the player.",
                }
            });
        } catch (error) {
            console.error("Stop game error:", error);
            alert("Failed to stop game properly");
        }
    };

    const toggleAssistant = () => {
        setAssistantVisible(!assistantVisible);
    };

    return (
        <div className="bejeweled">
            <audio ref={audioRef} loop />
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
                <div className="controls">
                    <button onClick={stopGame} className="stop-game-button">
                        Stop Game
                    </button>
                </div>
            </div>

            <div className="right-part">
                <table className="game-board">
                    <tbody>
                    {gameField.map((row, rowIndex) => (
                        <tr key={`row-${rowIndex}`}>
                            {row.map((jewel, colIndex) => (
                                <td
                                    key={`cell-${rowIndex}-${colIndex}`}
                                    className={`jewel-cell ${selected && selected[0] === rowIndex && selected[1] === colIndex ? 'selected' : ''}`}
                                >
                                    {jewel && jewel.type ? (
                                        <a
                                            href="#"
                                            onClick={(e) => handleClick(e, rowIndex, colIndex)}
                                        >
                                            <img
                                                src={jewelImages[jewel.type.toUpperCase()]}
                                                alt={jewel.type}
                                                className="jewel-img"
                                            />
                                        </a>
                                    ) : (
                                        <div className="empty-cell"></div>
                                    )}
                                </td>
                            ))}
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <div className="help-bar">
                <span className="material-symbols-outlined" onClick={() => setHelpOpen(true)}>help</span>
                <span className="material-symbols-outlined" onClick={() => setSettingsOpen(true)}>settings</span>
                <span className="material-symbols-outlined" onClick={toggleAssistant}>lightbulb_2</span>

                {/* Embedded Assistant */}
                {assistantVisible && (
                    <div className="assistant-embedded" id="speakgpt">
                        <iframe
                            src={`https://assistant.teslasoft.org/embedded?payload=${speakGptPayload}`}
                            className="assistant-iframe"
                            title="Bejeweled Assistant"
                        />
                    </div>
                )}

                {/* Help Dialog */}
                <Dialog open={helpOpen} onClose={() => setHelpOpen(false)}>
                    <DialogTitle>Instructions</DialogTitle>
                    <DialogContent dividers>
                        <Typography variant="body1" sx={{ mb: 2 }}>
                            Form a horizontal or vertical line of 3 or more identical gems so that they can be removed.
                        </Typography>
                        <Typography variant="body1">
                            Click two horizontally or vertically adjacent gems to swap their positions.
                        </Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setHelpOpen(false)}>Close</Button>
                    </DialogActions>
                </Dialog>

                {/* Settings Dialog */}
                <Dialog open={settingsOpen} onClose={() => setSettingsOpen(false)}>
                    <DialogTitle>Settings</DialogTitle>
                    <DialogContent dividers>

                        {/* Music Selection */}
                        <FormControl fullWidth sx={{ mt: 1 }}>
                            <InputLabel id="music-select-label">Select Music</InputLabel>
                            <Select
                                labelId="music-select-label"
                                value={selectedMusic}
                                label="Select Music"
                                onChange={(e) => setSelectedMusic(e.target.value)}
                            >
                                {musicOptions.map((track) => (
                                    <MenuItem key={track.value} value={track.value}>
                                        {track.label}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        {/* Music Volume */}
                        <Box mt={3}>
                            <Typography gutterBottom>Music Volume</Typography>
                            <Slider
                                value={musicVolume}
                                onChange={(e, newValue) => setMusicVolume(newValue)}
                                aria-labelledby="music-volume-slider"
                                min={0}
                                max={100}
                            />
                        </Box>

                        {/* Sound Effects Volume */}
                        <Box mt={3}>
                            <Typography gutterBottom>Sound Effects Volume</Typography>
                            <Slider
                                value={noiseVolume}
                                onChange={(e, newValue) => setNoiseVolume(newValue)}
                                aria-labelledby="noise-volume-slider"
                                min={0}
                                max={100}
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setSettingsOpen(false)}>Close</Button>
                    </DialogActions>
                </Dialog>

            </div>
        </div>
    );
}

export default GamePage;