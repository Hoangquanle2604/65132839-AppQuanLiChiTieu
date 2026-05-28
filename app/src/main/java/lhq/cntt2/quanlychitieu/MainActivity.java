package lhq.cntt2.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private TransactionViewModel transactionViewModel;
    private TransactionAdapter adapter;
    private TextView tvTotalBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        RecyclerView rvTransactions = findViewById(R.id.rvTransactions);
        LinearLayout layoutOpenCalendar = findViewById(R.id.layoutOpenCalendar);
        LinearLayout layoutOpenReport = findViewById(R.id.layoutOpenReport);

        // ĐÃ CHUYỂN ĐỔI: Thay thế nút bấm dấu cộng cũ bằng nút Nhập vào ở menu đáy
        LinearLayout layoutOpenAddTransaction = findViewById(R.id.layoutOpenAddTransaction);

        adapter = new TransactionAdapter();
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        adapter.setOnDeleteClickListener(transaction -> {
            if (transaction.getTransactionId() != null) {
                transactionViewModel.deleteTransaction(transaction.getTransactionId());
            }
        });

        transactionViewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions != null) {
                adapter.setTransactions(transactions);

                double totalBalance = 0;

                for (TransactionModel t : transactions) {
                    if ("INCOME".equals(t.getType())) {
                        totalBalance += t.getAmount();
                    } else {
                        totalBalance -= t.getAmount();
                    }
                }
                DecimalFormat df = new DecimalFormat("#,### đ");
                tvTotalBalance.setText(df.format(totalBalance));
            }
        });

        transactionViewModel.fetchTransactions("USER_TEST_01");

        layoutOpenCalendar.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CalendarActivity.class))
        );

        if (layoutOpenReport != null) {
            layoutOpenReport.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, ReportActivity.class))
            );
        }

        // ĐÃ CHUYỂN ĐỔI: Gán sự kiện click vào nút Nhập vào để khởi chạy màn hình thêm giao dịch
        if (layoutOpenAddTransaction != null) {
            layoutOpenAddTransaction.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, AddTransactionActivity.class))
            );
        }
    }
}