package lhq.cntt2.quanlychitieu;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    public interface TransactionCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface TransactionListCallback {
        void onSuccess(List<TransactionModel> transactions);
        void onFailure(Exception e);
    }

    public void addTransaction(TransactionModel transaction, TransactionCallback callback) {
        FirebaseFirestore.getInstance().collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("transactionId", documentReference.getId())
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void deleteTransaction(String transactionId, TransactionCallback callback) {
        if (transactionId == null || transactionId.isEmpty()) {
            callback.onFailure(new Exception("Transaction ID không hợp lệ (null hoặc rỗng)"));
            return;
        }

        FirebaseFirestore.getInstance().collection("transactions")
                .document(transactionId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void getTransactions(String userId, TransactionListCallback callback) {
        FirebaseFirestore.getInstance().collection("transactions")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onFailure(error);
                        return;
                    }

                    List<TransactionModel> list = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            TransactionModel tm = doc.toObject(TransactionModel.class);
                            if (tm != null) {
                                tm.setTransactionId(doc.getId());
                                list.add(tm);
                            }
                        }
                    }
                    callback.onSuccess(list);
                });
    }
}