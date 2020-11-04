package com.voltaire.user;

import com.voltaire.user.model.RoleConstants;
import com.voltaire.user.model.User;
import com.voltaire.user.repository.RoleRepository;
import com.voltaire.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @SneakyThrows
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void createUser(String email) {
        var user = new User();
        user.setEmail(email);
        user.setId(UUID.randomUUID().toString());
        user.addRole(roleRepository.findByRole(RoleConstants.ROLE_USER.toString()));

        userRepository.save(user);
    }

    @SneakyThrows
    public User findById(String id) {
        return userRepository.findById(id);
    }

}
