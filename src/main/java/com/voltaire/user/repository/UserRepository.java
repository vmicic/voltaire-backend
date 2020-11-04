package com.voltaire.user.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRepository {

    private final Firestore firestore;

    private CollectionReference getDocumentCollection() {
        return firestore.collection("users");
    }

    @SneakyThrows
    public User findByEmail(String email) {
        var query = getDocumentCollection().whereEqualTo("email", email);
        var querySnapshot = query.get();
        var documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new EntityNotFoundException("email", email);
        }

        return documents.get(0).toObject(User.class);
    }

    @SneakyThrows
    public User findById(String id) {
        var documentSnapshot = getDocumentCollection().document(id).get().get();
        if (!documentSnapshot.exists()) {
            throw new EntityNotFoundException("id", id);
        }

        return documentSnapshot.toObject(User.class);

    }

    @SneakyThrows
    public void save(User user) {
        getDocumentCollection().document(user.getId()).set(user);
    }

}
