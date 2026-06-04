package lhq.cntt2.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private TransactionViewModel transactionViewModel;
    private TransactionAdapter adapter;
    private TextView tvTotalBalance;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = user.getUid(); // Lấy Uid duy nhất của tài khoản hiện tại

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        RecyclerView rvTransactions = findViewById(R.id.rvTransactions);
        LinearLayout layoutOpenCalendar = findViewById(R.id.layoutOpenCalendar);
        LinearLayout layoutOpenReport = findViewById(R.id.layoutOpenReport);
        LinearLayout layoutOpenBudget = findViewById(R.id.layoutOpenBudget);
        FloatingActionButton fabAddTransaction = findViewById(R.id.fabAddTransaction);

        adapter = new TransactionAdapter();
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        adapter.setOnDeleteClickListener(transaction -> {
            if (transaction.getTransactionId() != null) {
                transactionViewModel.deleteTransaction(transaction.getTransactionId());
            }
        });
        //===>
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

        transactionViewModel.fetchTransactions(currentUserId);

        layoutOpenCalendar.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CalendarActivity.class))
        );

        if (layoutOpenReport != null) {
            layoutOpenReport.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, ReportActivity.class))
            );
        }

        if (layoutOpenBudget != null) {
            layoutOpenBudget.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, BudgetActivity.class))
            );
        }

        if (fabAddTransaction != null) {
            fabAddTransaction.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, AddTransactionActivity.class))
            );
        }
    }
}