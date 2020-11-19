package com.voltaire.user.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.user.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleRepository {

    private static final String ROLE_COLLECTION_NAME = "roles";

    private final Firestore firestore;

    private CollectionReference getCollectionReference() {
        return firestore.collection(ROLE_COLLECTION_NAME);
    }

    @SneakyThrows
    public Role findByRole(String name) {
        var query = getCollectionReference().whereEqualTo("authority", name);
        var querySnapshot = query.get();
        var documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new EntityNotFoundException("authority", name);
        }

        return documents.get(0).toObject(Role.class);
    }

}
