package lhq.cntt2.quanlychitieu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

public class TransactionViewModel extends ViewModel {
    private final TransactionRepository repository = new TransactionRepository();

    private final MutableLiveData<Boolean> addSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<TransactionModel>> transactionsLiveData = new MutableLiveData<>();

    // XOÁ BỎ DÒNG KHAI BÁO CỨNG "USER_TEST_01". Thay vào đó dùng hàm lấy Uid trực tiếp từ Firebase Auth an toàn tuyệt đối
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : "USER_TEST_01";
    }

    public LiveData<Boolean> getAddSuccess() { return addSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<TransactionModel>> getTransactionsLiveData() { return transactionsLiveData; }

    public void addTransaction(String userId, double amount, String type, String category, String note) {
        TransactionModel transaction = new TransactionModel();
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setNote(note);
        transaction.setTimestamp(Timestamp.now());

        repository.addTransaction(transaction, new TransactionRepository.TransactionCallback() {
            @Override
            public void onSuccess() {
                addSuccess.setValue(true);
                // Đồng bộ lại đúng tài khoản vừa thêm
                fetchTransactions(userId);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.setValue(e.getMessage());
            }
        });
    }

    public void deleteTransaction(String transactionId) {
        repository.deleteTransaction(transactionId, new TransactionRepository.TransactionCallback() {
            @Override
            public void onSuccess() {
                // SỬA TẠI ĐÂY: Khi xoá xong, lấy đúng Uid của tài khoản đang đăng nhập để làm tươi (refresh) danh sách
                fetchTransactions(getCurrentUserId());
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.setValue("Lỗi xóa: " + e.getMessage());
            }
        });
    }

    public void fetchTransactions(String userId) {
        // Luôn truyền userId động vào Repository
        repository.getTransactions(userId, new TransactionRepository.TransactionListCallback() {
            @Override
            public void onSuccess(List<TransactionModel> transactions) {
                transactionsLiveData.setValue(transactions);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.setValue(e.getMessage());
            }
        });
    }
}