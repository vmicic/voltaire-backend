package com.voltaire.user.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class User {

    @DocumentId
    private String id;

    private String email;

    private List<Role> authorities = new ArrayList<>();

    public void addRole(Role role) {
        authorities.add(role);
    }

}
