import green from '../../images/tuke_logo_green.svg.png';
import white from '../../images/tuke_logo_white.svg.png';
import blue from '../../images/tuke_logo_blue.svg.png';
import orange from '../../images/tuke_logo_orange.svg.png';
import red from '../../images/tuke_logo_red.svg.png';
import yellow from '../../images/tuke_logo_yellow.svg.png';
import purple from '../../images/tuke_logo_purple.svg.png';
import empty from '../../images/tuke_logo_empty.svg.png';
import bombImg from '../../images/bomb.png';

import '../../css/styles.css';

const jewelImages = {
    GREEN: green,
    WHITE: white,
    BLUE: blue,
    ORANGE: orange,
    RED: red,
    YELLOW: yellow,
    PURPLE: purple,
    EMPTY: empty
};


export const GameBoard = ({gameField, selected, affectedJewels, onCellClick, activateBomb}) => {
    return (
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
                                {jewel && jewel.type && jewel.type !== "EMPTY" ? (
                                    <a
                                        href="#"
                                        onClick={(e) => {
                                            e.preventDefault();
                                            jewel.bomb
                                                ? activateBomb(rowIndex, colIndex)
                                                : onCellClick(e, rowIndex, colIndex);
                                        }}
                                    >
                                        <img
                                            src={jewel.bomb ? bombImg : jewelImages[jewel.type.toUpperCase()]}
                                            alt={jewel.type}
                                            className={`jewel-img ${
                                                affectedJewels.some(([r, c]) => r === rowIndex && c === colIndex)
                                                    ? 'affected'
                                                    : ''
                                            }`}
                                        />
                                    </a>
                                ) : (
                                    <img
                                        src={jewelImages["EMPTY"]}
                                        alt="empty"
                                        className="jewel-img"
                                        style={{ pointerEvents: 'none'}}
                                    />
                                )}

                            </td>
                        ))}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};