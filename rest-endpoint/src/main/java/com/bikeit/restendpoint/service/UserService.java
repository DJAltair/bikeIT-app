package com.bikeit.restendpoint.service;

import com.bikeit.restendpoint.model.Dto.RegistrationDto;
import com.bikeit.restendpoint.model.Dto.UpdateUserProfileDto;
import com.bikeit.restendpoint.model.Dto.UserProfileDto;
import com.bikeit.restendpoint.model.Role;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        } else {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            });

            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
    }

    public User create(RegistrationDto request) {
        if(userRepository.existsByUsername(request.username())) { return null; }
        List<Role> roles = new ArrayList<>();
        try {
            roles.add(roleService.getByName(Role.ROLE_USER));
        } catch (Exception e) { }
        return create(request, roles);
    }

    public User create(RegistrationDto request, List<Role> roles) {
        if(userRepository.existsByUsername(request.username())) { return null; }
        User user = new User(request.name(), request.username(), passwordEncoder.encode(request.password()), roles);
        save(user);
        return user;
    }

    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public Optional<UserProfileDto> getUserProfileById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserProfileDto userProfileDto = new UserProfileDto();
                    userProfileDto.setUsername(user.getUsername());
                    userProfileDto.setName(user.getName());
                    userProfileDto.setDescription(user.getDescription());
                    return userProfileDto;
                });
    }

    @Transactional
    public Optional<UserProfileDto> updateUserProfile(Long id, UpdateUserProfileDto updateUserProfileDto) {
        String currentUsername = getCurrentUsername();
        User currentUser = userRepository.findByUsername(currentUsername);

        return userRepository.findById(id).map(user -> {
            if (!user.getUsername().equals(currentUsername)) {
                throw new SecurityException("You are not authorized to update this profile.");
            }
            user.setName(updateUserProfileDto.getName());
            user.setDescription(updateUserProfileDto.getDescription());
            userRepository.save(user);

            UserProfileDto updatedProfile = new UserProfileDto();
            updatedProfile.setUsername(user.getUsername());
            updatedProfile.setName(user.getName());
            updatedProfile.setDescription(user.getDescription());

            return updatedProfile;
        });
    }
}