import React, {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import '../css/styles.css';
import logo from '../images/logo.png';
import confetti from 'canvas-confetti';

function SignupPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [showGem, setShowGem] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        if (password && confirmPassword && password === confirmPassword) {
            setShowGem(true);
            const timer = setTimeout(() => setShowGem(false), 2000);
            return () => clearTimeout(timer);
        }
    }, [password, confirmPassword]);

    const handleSignup = async (e) => {
        e.preventDefault();
        setError('');

        if (password !== confirmPassword) {
            setError("Passwords don't match!");
            return;
        }

        setIsLoading(true);

        try {
            const signupResponse = await fetch('http://localhost:8080/auth/signup', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                credentials: 'include',
                body: JSON.stringify({ username, password })
            });

            if (!signupResponse.ok) throw new Error(await signupResponse.text());

            const loginResponse = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                credentials: 'include',
                body: JSON.stringify({ username, password })
            });

            if (!loginResponse.ok) throw new Error(await loginResponse.text());

            const gameResponse = await fetch('http://localhost:8080/bejeweled/start', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                credentials: 'include',
                body: JSON.stringify({ playerName: username })
            });

            if (!gameResponse.ok) {
                const errorData = await gameResponse.json().catch(() => ({}));
                throw new Error(errorData.message || 'Failed to start game');
            }

            confetti({
                particleCount: 100,
                spread: 70,
                origin: { y: 0.6 }
            });

            setTimeout(() => navigate('/game'), 1000);

        } catch (err) {
            setError(err.message);
            console.error('Signup error:', err);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth">
            <img src={logo} alt="BEJEWELED" className="logo"/>
            <div className="auth-page">
                <h2>Sign Up</h2>
                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleSignup}>
                    <input
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        minLength={3}
                    />

                    <div className="password-container">
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            minLength={4}
                        />
                        {showGem && (
                            <span className="gem-animation">💎</span>
                        )}
                    </div>

                    <input
                        type="password"
                        placeholder="Confirm Password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                        minLength={4}
                    />

                    <button
                        type="submit"
                        disabled={isLoading}
                        className={isLoading ? 'loading' : ''}
                    >
                        {isLoading ? (
                            <>
                                <span className="spinner"></span>
                                Registering...
                            </>
                        ) : (
                            'Register Now!'
                        )}
                    </button>
                </form>
                <p className="login-link">Already have an account? <a href="/bejeweled/login">Log in</a></p>
            </div>
        </div>
    );
}

export default SignupPage;