package com.example.blogs.Contollers;

import com.example.blogs.payloads.ApiResponse;
import com.example.blogs.payloads.UserDto;
import com.example.blogs.services.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Creating new user with email: {}", userDto.getEmail());
        UserDto createUserDto = this.userService.createUser(userDto);
        logger.info("User created successfully with ID: {}", createUserDto.getId());
        return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {
        logger.info("Updating user with ID: {}", uid);
        UserDto updatedUser = this.userService.updateUser(userDto, uid);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        return ResponseEntity.ok(updatedUser);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
        logger.info("Deleting user with ID: {}", uid);
        this.userService.deleteUser(uid);
        logger.info("User deleted successfully with ID: {}", uid);
        return new ResponseEntity<>(new ApiResponse("User Deleted Successfully", true), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.info("Fetching all users");
        List<UserDto> users = this.userService.getAllUsers();
        logger.info("Fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getSingleUser(@PathVariable Integer userId) {
        logger.info("Fetching user with ID: {}", userId);
        UserDto userDto = this.userService.getUserById(userId);
        logger.info("User fetched successfully with ID: {}", userDto.getId());
        return ResponseEntity.ok(userDto);
    }
}
