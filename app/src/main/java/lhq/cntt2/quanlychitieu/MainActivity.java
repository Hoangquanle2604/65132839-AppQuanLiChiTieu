package lhq.cntt2.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TransactionViewModel transactionViewModel;
    private String currentUserId = "USER_TEST_01";


    private List<TransactionModel> allTransactions = new ArrayList<>();
    private TransactionAdapter adapter;
    private String selectedDateStr = "";


    private TextView tvIncomeSummary, tvExpenseSummary, tvTotalBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarView calendarView = findViewById(R.id.calendarView);
        tvIncomeSummary = findViewById(R.id.tvIncomeSummary);
        tvExpenseSummary = findViewById(R.id.tvExpenseSummary);
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        RecyclerView rvTransactions = findViewById(R.id.rvTransactions);
        FloatingActionButton fabAddTransaction = findViewById(R.id.fabAddTransaction);


        adapter = new TransactionAdapter();
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDateStr = sdf.format(calendar.getTime());


        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);


        adapter.setOnDeleteClickListener(transaction -> {
            if (transaction.getTransactionId() != null) {
                transactionViewModel.deleteTransaction(transaction.getTransactionId());
            }
        });

        transactionViewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions != null) {
                allTransactions = transactions;
                filterTransactionsByDate(selectedDateStr);
            }
        });

        transactionViewModel.fetchTransactions(currentUserId);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateStr = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (month + 1), year);


            filterTransactionsByDate(selectedDateStr);
        });

        fabAddTransaction.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddTransactionActivity.class))
        );
    }

    private void filterTransactionsByDate(String date) {
        List<TransactionModel> filteredList = new ArrayList<>();

        double budget = 2000000;
        double totalBalance = budget;
        double dailyIncome = 0;
        double dailyExpense = 0;


        for (TransactionModel t : allTransactions) {


            if ("INCOME".equals(t.getType())) {
                totalBalance += t.getAmount();
            } else {
                totalBalance -= t.getAmount();
            }
            if (date.equals(t.getDate())) {
                filteredList.add(t);

                if ("INCOME".equals(t.getType())) {
                    dailyIncome += t.getAmount();
                } else {
                    dailyExpense += t.getAmount();
                }
            }
        }


        adapter.setTransactions(filteredList);

        DecimalFormat df = new DecimalFormat("#,### đ");


        tvIncomeSummary.setText("+" + df.format(dailyIncome));
        tvExpenseSummary.setText("-" + df.format(dailyExpense));
        tvTotalBalance.setText(df.format(totalBalance));
    }
}