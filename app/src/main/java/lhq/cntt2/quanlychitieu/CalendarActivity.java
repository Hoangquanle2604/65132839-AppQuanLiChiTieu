package lhq.cntt2.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    private TransactionViewModel transactionViewModel;
    private List<TransactionModel> allTransactions = new ArrayList<>();
    private TransactionAdapter adapter;
    private String selectedDateStr = "";
    private TextView tvIncomeSummary, tvExpenseSummary;
    private String currentUserId; // Thêm biến lưu ID động

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(CalendarActivity.this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = user.getUid();

        Toolbar toolbar = findViewById(R.id.toolbarCalendar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Xem theo lịch");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        CalendarView calendarView = findViewById(R.id.calendarView);
        tvIncomeSummary = findViewById(R.id.tvIncomeSummary);
        tvExpenseSummary = findViewById(R.id.tvExpenseSummary);
        RecyclerView rvTransactions = findViewById(R.id.rvCalendarTransactions);

        adapter = new TransactionAdapter();
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        //======>
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDateStr = sdf.format(calendar.getTime());

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionViewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions != null) {
                allTransactions = transactions;
                //====>
                filterTransactionsByDate(selectedDateStr);
            }
        });

        // SỬA TẠI ĐÂY: Thay "USER_TEST_01" bằng currentUserId
        transactionViewModel.fetchTransactions(currentUserId);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateStr = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (month + 1), year);
            filterTransactionsByDate(selectedDateStr);
        });
    }

    private void filterTransactionsByDate(String date) {
        List<TransactionModel> filteredList = new ArrayList<>();
        double dailyIncome = 0;
        double dailyExpense = 0;

        for (TransactionModel t : allTransactions) {
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
    }
}