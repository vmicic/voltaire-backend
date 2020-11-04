package com.voltaire.user;

import com.google.cloud.firestore.Firestore;
import com.voltaire.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody String email) {
        userService.createUser(email);
    }

    @GetMapping("v1/users/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.findById(id);
    }
}
