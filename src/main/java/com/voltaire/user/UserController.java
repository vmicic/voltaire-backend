package com.voltaire.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("v1/echo")
    public String echo() {
        return "echo";
    }
}
