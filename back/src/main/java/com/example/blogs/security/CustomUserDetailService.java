package com.example.blogs.security;

import com.example.blogs.Repositories.UserRepository;
import com.example.blogs.entities.User;
import com.example.blogs.exceptions.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(CustomUserDetailService.class);

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);
        UserDetails user = userRepo.findByEmail(username);
        if (user == null) {
            logger.error("User not found with email: {}", username);
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        logger.info("User loaded successfully with email: {}", username);
        return user;
    }
}
