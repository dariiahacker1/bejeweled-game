import './App.css';
import {BrowserRouter, Routes, Route} from 'react-router-dom';
import GamePage from "./pages/GamePage";
import GameOverPage from "./pages/GameOverPage";
import SignupPage from "./pages/SignupPage";
import LoginPage from "./pages/LoginPage";

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
