import {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';

export const useGameLogic = () => {
    const navigate = useNavigate();
    const [gameField, setGameField] = useState([]);
    const [score, setScore] = useState(0);
    const [timeRemaining, setTimeRemaining] = useState(300);
    const [selected, setSelected] = useState(null);
    const [affectedJewels, setAffectedJewels] = useState([]);

    const fetchData = async (endpoint) => {
        try {
            const response = await fetch(`http://localhost:8080/bejeweled/${endpoint}`, {
                credentials: 'include'
            });
            if (!response.ok) throw new Error(`Failed to fetch ${endpoint}`);
            return await response.json();
        } catch (error) {
            console.error(`${endpoint} fetch error:`, error);
            throw error;
        }
    };

    const postData = async (endpoint, body = {}, expectJson = true) => {
        try {
            const response = await fetch(`http://localhost:8080/bejeweled/${endpoint}`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                credentials: 'include',
                body: JSON.stringify(body)
            });
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`${endpoint} failed: ${errorText}`);
            }
            return expectJson ? await response.json() : null;
        } catch (error) {
            console.error(`${endpoint} error:`, error);
            throw error;
        }
    };

    const fetchGameField = async () => {
        const data = await fetchData('field');
        setGameField(data.grid || []);
    };

    const swapJewels = async (row1, col1, row2, col2) => {
        try {
            await postData('swap', {row1, col1, row2, col2}, false);
            const fieldData = await fetchData('field');
            const scoreData = await fetchData('score');
            setGameField(fieldData.grid || []);
            setScore(scoreData.score || 0);
        } catch (error) {
            console.error("Swap error:", error);
            alert(`Swap failed: ${error.message}`);
        }
    };

    const activateBomb = async (row, col) => {
        const data = await postData('bomb', {row, col});
        setAffectedJewels(data.affected);
        setScore(data.score);
        setTimeout(async () => {
            setAffectedJewels([]);
            await fetchGameField();
        }, 500);
    };

    const stopGame = async () => {
        await postData('stop', {timeRemaining}, false);
        navigate('/end', {state: {lastWords: "Game stopped by the player."}});
    };

    // Timer effect
    useEffect(() => {
        if (timeRemaining <= 0) return;

        const interval = setInterval(() => {
            setTimeRemaining(prev => prev <= 1 ? 0 : prev - 1);
        }, 1000);

        return () => clearInterval(interval);
    }, [timeRemaining]);

    // Game end check
    useEffect(() => {
        const checkGameEnd = async () => {
            if (timeRemaining <= 0 || score >= 400) {
                await postData('stop', {timeRemaining}, false);
                navigate('/end', {
                    state: {
                        lastWords: timeRemaining <= 0
                            ? "You ran out of time."
                            : "You won!"
                    }
                });
            }
        };
        checkGameEnd();
    }, [timeRemaining, score, navigate]);

    // Initialization
    useEffect(() => {
        const initializeGame = async () => {
            try {
                const [fieldData, scoreData, timeData] = await Promise.all([
                    fetchData('field'),
                    fetchData('score'),
                    fetchData('time')
                ]);
                setGameField(fieldData.grid || []);
                setScore(scoreData.score || 0);
                setTimeRemaining(timeData.time || 0);
            } catch (error) {
                navigate('/game');
            }
        };
        initializeGame();
    }, [navigate]);

    return {
        gameField,
        score,
        timeRemaining,
        selected,
        affectedJewels,
        setSelected,
        fetchGameField,
        swapJewels,
        stopGame,
        activateBomb
    };
};


