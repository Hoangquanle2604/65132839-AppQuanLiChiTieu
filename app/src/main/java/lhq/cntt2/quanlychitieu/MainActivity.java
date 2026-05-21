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

        // Khởi tạo các view
        RecyclerView rvTransactions = findViewById(R.id.rvTransactions);
        TextView tvTotalBalance = findViewById(R.id.tvTotalBalance);
        TextView tvMonthlyBudget = findViewById(R.id.tvMonthlyBudget);
        TextView tvPercent = findViewById(R.id.tvPercent);
        FloatingActionButton fabAddTransaction = findViewById(R.id.fabAddTransaction);

        // Thiết lập Adapter
        TransactionAdapter adapter = new TransactionAdapter();
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Xử lý xóa giao dịch từ Adapter
        adapter.setOnDeleteClickListener(transaction -> {
            if (transaction.getTransactionId() != null) {
                transactionViewModel.deleteTransaction(transaction.getTransactionId());
            }
        });

        // Lắng nghe dữ liệu thay đổi từ Firestore
        transactionViewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions != null) {
                adapter.setTransactions(transactions);
                updateDashboard(transactions, tvTotalBalance, tvMonthlyBudget, tvPercent);
            }
        });

        // Gọi dữ liệu
        transactionViewModel.fetchTransactions(currentUserId);

        // Nút thêm giao dịch
        fabAddTransaction.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddTransactionActivity.class))
        );
    }

    private void updateDashboard(List<TransactionModel> transactions, TextView tvBalance, TextView tvBudget, TextView tvPercent) {
        if (tvBalance == null || tvBudget == null || tvPercent == null) return;

        double totalBalance = 0;
        double totalExpense = 0;
        double budget = 2000000;

        for (TransactionModel t : transactions) {
            if ("INCOME".equals(t.getType())) {
                totalBalance += t.getAmount();
            } else {
                totalBalance -= t.getAmount();
                totalExpense += t.getAmount();
            }
        }

        DecimalFormat df = new DecimalFormat("#,### đ");
        tvBalance.setText(df.format(totalBalance));
        tvBudget.setText("Hạn mức: " + df.format(budget));

        int percent = (budget > 0) ? (int) ((totalExpense / budget) * 100) : 0;
        tvPercent.setText(percent + "%");
    }
}