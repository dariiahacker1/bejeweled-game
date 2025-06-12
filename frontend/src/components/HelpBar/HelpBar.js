import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Typography, Button, Box, Slider, Select, MenuItem, FormControl, InputLabel
} from '@mui/material';

import React, {useEffect, useRef, useState} from "react";
import track1 from "./music/chill_vibes.mp3";
import track2 from "./music/epic_mode.mp3";
import track3 from "./music/classic_arcade.mp3";

import '../../css/styles.css';

const HelpBar = () => {

    const [assistantVisible, setAssistantVisible] = useState(false);

    const toggleAssistant = () => {
        setAssistantVisible(!assistantVisible);
    };

    const [helpOpen, setHelpOpen] = useState(false);
    const [settingsOpen, setSettingsOpen] = useState(false);

    const [musicVolume, setMusicVolume] = useState(0);
    const [noiseVolume, setNoiseVolume] = useState(50);

    const [selectedMusic, setSelectedMusic] = useState('track1');
    const musicOptions = [
        {label: 'Chill Vibes', value: 'track1'},
        {label: 'Epic Mode', value: 'track2'},
        {label: 'Classic Arcade', value: 'track3'}
    ];

    const audioFiles = {
        track1: track1,
        track2: track2,
        track3: track3
    };

    const audioRef = useRef(null);

    const json = JSON.stringify({
        name: "Bejeweled Assistant",
        icon: "https://img.icons8.com/color/96/diamond.png",
        description: "Your Bejeweled strategy helper!",
        initialMessage: "How can I help you with Bejeweled today?",
        initialResponse: "Welcome! Ready to get some tips on gems and combos?",
        systemMessage: "You are a Bejeweled expert AI. You always give specific tips about gem matching, spotting combos, and using bombs. If asked, explain moves step-by-step. Be concise but helpful.",
        chatLocation: "bejeweledAssistant"
    });

    const speakGptPayload = encodeURIComponent(
        btoa(unescape(encodeURIComponent(json)))
    );


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

    return(
        <div className="help-bar">

            <audio ref={audioRef} loop/>

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
                    <Typography variant="body1" sx={{mb: 2}}>
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
                    <FormControl fullWidth sx={{mt: 1}}>
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
    )
}

export default HelpBar;