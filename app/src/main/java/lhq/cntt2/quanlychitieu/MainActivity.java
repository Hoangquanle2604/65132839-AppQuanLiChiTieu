package lhq.cntt2.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TransactionViewModel transactionViewModel;
    private String currentUserId = "USER_TEST_01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các view từ layout
        RecyclerView rvTransactions = findViewById(R.id.rvTransactions);
        TextView tvTotalBalance = findViewById(R.id.tvTotalBalance);
        TextView tvMonthlyBudget = findViewById(R.id.tvMonthlyBudget);
        TextView tvPercent = findViewById(R.id.tvPercent);
        FloatingActionButton fabAddTransaction = findViewById(R.id.fabAddTransaction);

        // Thiết lập Adapter cho RecyclerView
        TransactionAdapter adapter = new TransactionAdapter();
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        // Khởi tạo ViewModel
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Xử lý sự kiện click nút xóa (icon thùng rác) truyền từ Adapter về
        adapter.setOnDeleteClickListener(transaction -> {
            if (transaction.getTransactionId() != null) {
                transactionViewModel.deleteTransaction(transaction.getTransactionId());
            }
        });

        // Lắng nghe dữ liệu thay đổi từ Firestore thông qua LiveData
        transactionViewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions != null) {
                adapter.setTransactions(transactions);
                updateDashboard(transactions, tvTotalBalance, tvMonthlyBudget, tvPercent);
            }
        });

        // Gọi hàm nạp dữ liệu từ server/database
        transactionViewModel.fetchTransactions(currentUserId);

        // Nút bấm mở màn hình Thêm giao dịch mới
        fabAddTransaction.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddTransactionActivity.class))
        );
    }

    /**
     * Hàm cập nhật các thông số trên Dashboard (Số dư, Hạn mức, Phần trăm)
     */
    private void updateDashboard(List<TransactionModel> transactions, TextView tvBalance, TextView tvBudget, TextView tvPercent) {
        if (tvBalance == null || tvBudget == null || tvPercent == null) return;

        double budget = 2000000;       // Hạn mức gốc ban đầu
        double totalBalance = budget;  // ĐÃ SỬA: Số dư ban đầu xuất phát từ hạn mức, không bắt đầu bằng 0
        double totalExpense = 0;       // Tổng chi tiêu

        // Duyệt qua danh sách để tính toán lại số dư thực tế
        for (TransactionModel t : transactions) {
            if ("INCOME".equals(t.getType())) {
                totalBalance += t.getAmount(); // Nếu là khoản thu -> Cộng vào số dư
            } else {
                totalBalance -= t.getAmount(); // Nếu là khoản chi -> Trừ vào số dư
                totalExpense += t.getAmount(); // Đồng thời cộng dồn vào tổng số tiền đã chi
            }
        }

        // Định dạng hiển thị tiền tệ (Ví dụ: 1,990,000 đ)
        DecimalFormat df = new DecimalFormat("#,### đ");
        tvBalance.setText(df.format(totalBalance));
        tvBudget.setText("Hạn mức: " + df.format(budget));

        // Tính toán phần trăm thanh tiến trình chi tiêu (đã dùng bao nhiêu % hạn mức)
        int percent = (budget > 0) ? (int) ((totalExpense / budget) * 100) : 0;
        tvPercent.setText(percent + "%");
    }
}