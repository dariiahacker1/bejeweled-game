package sk.tuke.kpi.kp.bejeweled.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import sk.tuke.kpi.kp.bejeweled.core.Jewel;
import sk.tuke.kpi.kp.bejeweled.core.Player;

import java.util.*;

@Controller
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3001", allowCredentials = "true")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody Map<String, String> payload, HttpSession session) {
        String username = payload.get("username");
        String password = payload.get("password");

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid username or password");
        }

        if (!passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            return ResponseEntity.status(400).body("Invalid username or password");
        }

        session.setAttribute("user", optionalUser.get());
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<String> signup(@RequestBody Map<String, String> payload, HttpSession session) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(400).body("User already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        session.setAttribute("user", user);
        return ResponseEntity.ok("Signup successful");
    }
}
