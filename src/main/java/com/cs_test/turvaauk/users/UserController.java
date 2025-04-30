package com.cs_test.turvaauk.users;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cs_test.turvaauk.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @RequestBody UserDto userDto,
            HttpServletResponse response) {

        if (!userService.validateCredentials(
                userDto.getUsername(),
                userDto.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        String token = jwtService.generateToken(userDto.getUsername());
        ResponseCookie cookie = ResponseCookie.from("SESSIONID", token)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofHours(1))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged in");
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        try {
            userService.registerUser(
                    userDto.getUsername(),
                    userDto.getPassword()
            );
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User created");
        } catch (DuplicateKeyException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username '"
                            + userDto.getUsername()
                            + "' is already taken");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(
            @CookieValue(name = "SESSIONID", required = false) String token) {

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final String username;
        try {
            username = jwtService.validateTokenAndGetUsername(token);
        } catch (JWTVerificationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(username);
        }
}
