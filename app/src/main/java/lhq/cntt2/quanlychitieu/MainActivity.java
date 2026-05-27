package lhq.cntt2.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
        FloatingActionButton fabAddTransaction = findViewById(R.id.fabAddTransaction);
        LinearLayout layoutOpenCalendar = findViewById(R.id.layoutOpenCalendar);

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

                // ĐÃ SỬA: Đặt lại số dư gốc mặc định là 0đ thay vì 2.000.000đ
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

        fabAddTransaction.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddTransactionActivity.class))
        );
    }
}