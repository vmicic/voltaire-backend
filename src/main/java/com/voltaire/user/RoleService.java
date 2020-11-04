package com.voltaire.user;

import com.google.cloud.Role;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final Firestore firestore;

    private CollectionReference getDocumentCollection() {
        return firestore.collection("roles");
    }

    @SneakyThrows
    public Role findByName(String name) {
        var query = getDocumentCollection().whereEqualTo("name", name);
        var querySnapshot = query.get();

        if (querySnapshot.get().getDocuments().isEmpty()) {
            throw new EntityNotFoundException("name", name);
        }

        return querySnapshot.get().getDocuments().get(0).toObject(Role.class);
    }

}
