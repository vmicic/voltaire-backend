package com.voltaire.user;

import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.user.model.User;
import com.voltaire.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("email", email));
    }

}
