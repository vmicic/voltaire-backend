package com.voltaire.user.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
public class Role implements GrantedAuthority {

    @DocumentId
    private String id;

    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
