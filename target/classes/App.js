import './App.css';
import {BrowserRouter, Routes, Route} from 'react-router-dom';
import GamePage from "./Pages/GamePage";
import GameOverPage from "./Pages/GameOverPage";
import SignupPage from "./Pages/SignupPage";
import LoginPage from "./Pages/LoginPage";

function App() {
    return (
        <BrowserRouter basename="/bejeweled">
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/game" element={<GamePage/>}/>
                <Route path="/end" element={<GameOverPage/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
