package com.dee.blog_rest.services.serviceImplementation;

import com.dee.blog_rest.entities.User;
import com.dee.blog_rest.entities.role.Role;
import com.dee.blog_rest.entities.role.RoleName;
import com.dee.blog_rest.exceptions.AccessDeniedException;
import com.dee.blog_rest.exceptions.AppException;
import com.dee.blog_rest.exceptions.BadRequestException;
import com.dee.blog_rest.exceptions.UnauthorizedException;
import com.dee.blog_rest.repositories.RoleRepository;
import com.dee.blog_rest.repositories.UserRepository;
import com.dee.blog_rest.requests_and_responses.AddUserRequest;
import com.dee.blog_rest.requests_and_responses.ApiResponse;
import com.dee.blog_rest.requests_and_responses.SignUpRequest;
import com.dee.blog_rest.requests_and_responses.UpdateUserRequest;
import com.dee.blog_rest.security.UserPrincipal;
import com.dee.blog_rest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Service
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User addUser(AddUserRequest user) {
        if (userRepository.findByEmail(user.getEmail())!=null) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Username is already taken");
            throw new BadRequestException(apiResponse);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "Email is already taken");
            throw new BadRequestException(apiResponse);
        }

        User user1 = new User();
        user1.setEmail(user.getEmail());
        user1.setLastName(user.getLastName());
        user1.setFirstName(user.getFirstName());
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        user1.setCreatedAt(Instant.now());
        user1.setUpdatedAt(Instant.now());

        List<Role> roles = new ArrayList<>();

        roles.add(
                roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user1.setRoles(roles);

        return userRepository.save(user1);
    }

    public  User findById(Long id){
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()){
            return byId.get();
        }
        throw new BadRequestException(
                "User not found", new Throwable("Invalid user id"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User updateUser(UpdateUserRequest newUser, Long id, UserPrincipal currentUser) {
        Optional<User> user = userRepository.findById(id);
        if (user.get().getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            User user1 = user.get();
            user1.setFirstName(newUser.getFirstName());
            user1.setLastName(newUser.getLastName());
            user1.setPassword(passwordEncoder.encode(newUser.getPassword()));
            user1.setUpdatedAt(Instant.now());
            user1.setEmail(newUser.getEmail());

            return userRepository.save(user1);

        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update profile of: " + user.get().getEmail());
        throw new UnauthorizedException(apiResponse);

    }


    public ApiResponse deleteUser(Long id, UserPrincipal currentUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("User with that id not found"));
        if (!user.getId().equals(currentUser.getId()) || !currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete profile of: " + user.getEmail());
            throw new AccessDeniedException(apiResponse);
        }

        userRepository.deleteById(user.getId());

        return new ApiResponse(Boolean.TRUE, "You successfully deleted profile of: " + user.getEmail());
    }


    public ApiResponse giveAdmin(Long id) {
        Optional<User> user = userRepository.findById(id);
        List<Role> roles = new ArrayList<>();

        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new AppException("User role not set")));
        roles.add(
                roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.get().setRoles(roles);
        userRepository.save(user.get());
        return new ApiResponse(Boolean.TRUE, "You gave ADMIN role to user: " + user.get().getEmail());
    }


    public ApiResponse removeAdmin(Long id) {
        Optional<User> user = userRepository.findById(id);
        List<Role> roles = new ArrayList<>();
        roles.add(
                roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.get().setRoles(roles);
        userRepository.save(user.get());
        return new ApiResponse(Boolean.TRUE, "You took ADMIN role from user: " + user.get().getEmail());
    }
}
