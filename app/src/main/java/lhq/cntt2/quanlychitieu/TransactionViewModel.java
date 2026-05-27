package lhq.cntt2.quanlychitieu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.Timestamp;
import java.util.List;

public class TransactionViewModel extends ViewModel {
    private final TransactionRepository repository = new TransactionRepository();

    private final MutableLiveData<Boolean> addSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<TransactionModel>> transactionsLiveData = new MutableLiveData<>();

    // Biến lưu lại userId hiện tại để hỗ trợ tự động làm mới dữ liệu
    private String savedUserId = "USER_TEST_01";

    public LiveData<Boolean> getAddSuccess() { return addSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<TransactionModel>> getTransactionsLiveData() { return transactionsLiveData; }

    // Thêm giao dịch mới
    public void addTransaction(String userId, double amount, String type, String category, String note) {
        this.savedUserId = userId; // Lưu lại userId

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
                // ĐÃ SỬA: Gọi làm mới lại danh sách ngay lập tức sau khi thêm thành công
                fetchTransactions(savedUserId);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.setValue(e.getMessage());
            }
        });
    }

    // Xóa giao dịch
    public void deleteTransaction(String transactionId) {
        repository.deleteTransaction(transactionId, new TransactionRepository.TransactionCallback() {
            @Override
            public void onSuccess() {
                // ĐÃ SỬA: Vì phương thức lấy dữ liệu hiện tại chưa dùng SnapshotListener tự động cập nhật,
                // chúng ta chủ động gọi fetchTransactions để lấy lại danh sách mới nhất sau khi xóa.
                fetchTransactions(savedUserId);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.setValue("Lỗi xóa: " + e.getMessage());
            }
        });
    }

    // Tải danh sách giao dịch
    public void fetchTransactions(String userId) {
        this.savedUserId = userId; // Cập nhật lại userId khi gọi hàm

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