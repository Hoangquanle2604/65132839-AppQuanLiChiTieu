package lhq.cntt2.quanlychitieu;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    // Interface dùng chung cho các hàm thêm/xóa
    public interface TransactionCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface TransactionListCallback {
        void onSuccess(List<TransactionModel> transactions);
        void onFailure(Exception e);
    }

    // 1. Hàm thêm giao dịch
    public void addTransaction(TransactionModel transaction, TransactionCallback callback) {
        FirebaseFirestore.getInstance().collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // 2. Hàm xóa giao dịch
    public void deleteTransaction(String transactionId, TransactionCallback callback) {
        FirebaseFirestore.getInstance().collection("transactions")
                .document(transactionId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // 3. Hàm lấy danh sách giao dịch
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
                            tm.setTransactionId(doc.getId());
                            list.add(tm);
                        }
                    }
                    callback.onSuccess(list);
                });
    }
}