package com.example.blogs.services.implementations;

import com.example.blogs.Repositories.UserRepository;
import com.example.blogs.entities.Role;
import com.example.blogs.entities.User;
import com.example.blogs.payloads.UserDto;
import com.example.blogs.services.UserService;
import com.example.blogs.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.blogs.entities.Role.USER;

@Service
public class UserImplementation implements UserService {

    private static final Logger logger = LogManager.getLogger(UserImplementation.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerNewUser(UserDto userDto) {
        logger.info("Registering new user with email: {}", userDto.getEmail());
        User user = this.modelMapper.map(userDto, User.class);

        // encoded the password
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        // roles
        user.setRole(USER);

        User newUser = this.userRepo.save(user);
        logger.info("User registered successfully with ID: {}", newUser.getId());

        return this.modelMapper.map(newUser, UserDto.class);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        logger.info("Creating new user with email: {}", userDto.getEmail());
        User user = this.dtoToUser(userDto);
        User savedUser = this.userRepo.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return this.userToDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        logger.info("Updating user with ID: {}", userId);
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setAbout(userDto.getAbout());
        user.setPassword(userDto.getPassword());

        User updatedUser = this.userRepo.save(user);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        return this.userToDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Integer userId) {
        logger.info("Fetching user with ID: {}", userId);
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
        logger.info("User fetched successfully with ID: {}", user.getId());
        return this.userToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = this.userRepo.findAll();
        List<UserDto> userDtos = users.stream()
                .map(user -> this.userToDto(user))
                .collect(Collectors.toList());
        logger.info("Fetched {} users", userDtos.size());
        return userDtos;
    }

    @Override
    public void deleteUser(Integer userId) {
        logger.info("Deleting user with ID: {}", userId);
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
        this.userRepo.delete(user);
        logger.info("User deleted successfully with ID: {}", userId);
    }

    @Override
    public User findByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        return userRepo.findByEmail(username);
    }

    private User dtoToUser(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }

    public UserDto userToDto(User user) {
        return this.modelMapper.map(user, UserDto.class);
    }

    public List<User> getAdminList() {
        logger.info("Fetching admin list");
        return userRepo.getAdmins(Role.ADMIN);
    }

    public void addUser(User user) {
        logger.info("Adding new user with email: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        logger.info("User added successfully with ID: {}", user.getId());
    }
}
