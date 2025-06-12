import '../css/styles.css';

import HelpBar from "../components/HelpBar/HelpBar";
import {GameBoard} from "../components/GameBoard/GameBoard";
import {useGameLogic} from "../hooks/useGameLogic";
import {GameControls} from "../components/Controls/GameControls";

function GamePage() {

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

    const {
        gameField,
        score,
        timeRemaining,
        selected,
        affectedJewels,
        setSelected,
        swapJewels,
        stopGame,
        activateBomb
    } = useGameLogic();

    return (
        <div className="bejeweled">
            <GameControls score={score}
                          timeRemaining={timeRemaining}
                          onStopGame={stopGame}
            />
            <GameBoard gameField={gameField}
                       selected={selected}
                       affectedJewels={affectedJewels}
                       onCellClick={handleClick}
                       activateBomb={activateBomb}
            />
            <HelpBar/>
        </div>
    );
}

export default GamePage;


