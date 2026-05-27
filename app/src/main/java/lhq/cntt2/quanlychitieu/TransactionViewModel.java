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

    private String savedUserId = "USER_TEST_01";

    public LiveData<Boolean> getAddSuccess() { return addSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<TransactionModel>> getTransactionsLiveData() { return transactionsLiveData; }

    public void addTransaction(String userId, double amount, String type, String category, String note) {
        this.savedUserId = userId;

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
                fetchTransactions(savedUserId);
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
                fetchTransactions(savedUserId);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.setValue("Lỗi xóa: " + e.getMessage());
            }
        });
    }

    public void fetchTransactions(String userId) {
        this.savedUserId = userId;

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