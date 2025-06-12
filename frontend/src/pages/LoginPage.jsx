import React, {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import '../css/styles.css';
import logo from '../images/logo.png';
import confetti from 'canvas-confetti';

function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleTestLogin = async (e) => {
        e.preventDefault();
        setUsername('dariia');
        setPassword('admin');
    };

    useEffect(() => {
        if (username === 'dariia' && password === 'admin' && !isLoading) {
            const syntheticEvent = { preventDefault: () => {} };
            handleLogin(syntheticEvent);
        }
    }, [username, password]);

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            const loginResponse = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                credentials: 'include',
                body: JSON.stringify({username, password})
            });

            if (!loginResponse.ok) {
                throw new Error(await loginResponse.text());
            }

            const gameResponse = await fetch('http://localhost:8080/bejeweled/start', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                credentials: 'include',
                body: JSON.stringify({playerName: username})
            });

            if (!gameResponse.ok) {
                const errorData = await gameResponse.json().catch(() => ({}));
                throw new Error(errorData.message || 'Failed to start game');
            }

            confetti({
                particleCount: 150,
                spread: 70,
                origin: {y: 0.6},
                colors: ['#ff0000', '#00ff00', '#0000ff', '#ffff00', '#ff00ff']
            });

            setTimeout(() => navigate('/game'), 0);

        } catch (err) {
            setError(err.message);
            console.error('Login error:', err);
        } finally {
            setIsLoading(false);
        }
    };

    return (<div className="auth">
            <img src={logo} alt="BEJEWELED" className="logo"/>
            <div className="auth-page">
                <h2>Welcome Back!</h2>
                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleLogin}>
                    <div className="input-container">
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>

                    <div className="input-container">
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className={isLoading ? 'loading' : ''}
                    >
                        {isLoading ? (<>
                                <span className="spinner"></span>
                                Logging in...
                            </>) : ('Play Now!')}
                    </button>

                    <button
                        onClick={handleTestLogin}
                        className="test-login-button"
                        disabled={isLoading}
                    >
                        {isLoading ? 'Logging in...' : 'Test Login (dariia/admin)'}
                    </button>

                </form>

                <p className="login-link">Don't have an account? <a href="/bejeweled/signup">Sign up</a></p>
            </div>
        </div>);
}

export default LoginPage;