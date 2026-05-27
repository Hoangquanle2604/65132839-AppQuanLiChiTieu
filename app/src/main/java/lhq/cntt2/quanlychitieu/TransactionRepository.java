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

    // 1. Hàm thêm giao dịch (Đã tối ưu để lưu cả ID vào document mới nếu cần)
    public void addTransaction(TransactionModel transaction, TransactionCallback callback) {
        FirebaseFirestore.getInstance().collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    // Cập nhật ngược lại ID của Document vừa tạo vào chính nó để dữ liệu trên Firebase luôn sạch
                    documentReference.update("transactionId", documentReference.getId())
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // 2. Hàm xóa giao dịch
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

    // 3. Hàm lấy danh sách giao dịch (Đã tối ưu hóa thứ tự gán ID)
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
                            // Chuyển dữ liệu từ Firestore thành đối tượng Java
                            TransactionModel tm = doc.toObject(TransactionModel.class);

                            // ĐÃ TỐI ƯU: Ép buộc lấy ID của Document trên Firestore gán vào Model
                            // Việc này giúp sửa lỗi nút xóa kể cả khi trường transactionId trên Firebase đang bị null
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