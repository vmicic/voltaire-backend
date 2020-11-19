package com.voltaire.user.repository;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.voltaire.exception.customexceptions.EntityNotFoundException;
import com.voltaire.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRepository {

    private static final String USER_COLLECTION_NAME = "users";

    private final Firestore firestore;

    private CollectionReference getCollectionReference() {
        return firestore.collection(USER_COLLECTION_NAME);
    }

    private DocumentReference getUserDocumentReference(String userId) {
        return getCollectionReference().document(userId);
    }

    @SneakyThrows
    public User findByEmail(String email) {
        var query = getCollectionReference().whereEqualTo("email", email);
        var querySnapshot = query.get();
        var documents = querySnapshot.get().getDocuments();

        if (documents.isEmpty()) {
            throw new EntityNotFoundException("email", email);
        }

        return documents.get(0).toObject(User.class);
    }

    @SneakyThrows
    public User findById(String id) {
        var documentSnapshot = getUserDocumentReference(id).get().get();
        if (!documentSnapshot.exists()) {
            throw new EntityNotFoundException("id", id);
        }

        return documentSnapshot.toObject(User.class);

    }

    @SneakyThrows
    public void save(User user) {
        getUserDocumentReference(user.getId()).set(user);
    }

}
