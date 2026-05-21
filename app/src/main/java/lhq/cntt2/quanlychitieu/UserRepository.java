package lhq.cntt2.quanlychitieu;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersCollection = db.collection("users");

    public interface UserActionCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface UserDataCallback {
        void onSuccess(UserModel userModel);
        void onFailure(Exception e);
    }

    public void createUser(UserModel userModel, UserActionCallback callback) {
        usersCollection.document(userModel.getUid()).set(userModel)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void getUserData(String uid, UserDataCallback callback) {
        usersCollection.document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot.toObject(UserModel.class));
                    } else {
                        callback.onFailure(new Exception("Không tìm thấy user!"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}