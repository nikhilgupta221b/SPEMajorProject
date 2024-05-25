package com.example.blogs.Contollers;

import com.example.blogs.Repositories.UserRepository;
import com.example.blogs.entities.User;
import com.example.blogs.payloads.JwtAuthRequest;
import com.example.blogs.payloads.JwtAuthResponse;
import com.example.blogs.payloads.UserDto;
import com.example.blogs.security.JwtTokenHelper;
import com.example.blogs.services.UserService;
import com.example.blogs.exceptions.ApiException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@CrossOrigin("http://localhost:9090")
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest request) throws Exception {
        logger.info("Attempting to authenticate user: {}", request.getUsername());
        this.authenticate(request.getUsername(), request.getPassword());

        User userDetails = userService.findByUsername(request.getUsername());

        if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            String token = jwtTokenHelper.generateToken(userDetails.getUsername());

            JwtAuthResponse response = new JwtAuthResponse();
            response.setToken(token);
            response.setUser(this.mapper.map(userDetails, UserDto.class));

            logger.info("User authenticated successfully: {}", request.getUsername());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        logger.warn("Authentication failed for user: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private void authenticate(String username, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        try {
            authenticationManager.authenticate(authenticationToken);
            logger.info("Authentication successful for user: {}", username);
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials provided for user: {}", username);
            throw new ApiException("Invalid username or password !!");
        }
    }

    // Register new user API
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Registering new user with email: {}", userDto.getEmail());
        UserDto registeredUser = this.userService.registerNewUser(userDto);
        logger.info("User registered successfully with email: {}", userDto.getEmail());
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // Get logged-in user data
    // Uncomment and implement as needed
    // @GetMapping("/current-user/")
    // public ResponseEntity<UserDto> getUser(Principal principal) {
    //     User user = this.userRepo.findByEmail(principal.getName()).get();
    //     return new ResponseEntity<UserDto>(this.mapper.map(user, UserDto.class), HttpStatus.OK);
    // }
}
