package lhq.cntt2.quanlychitieu;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    private TransactionViewModel transactionViewModel;
    private TransactionAdapter adapter;
    private CircleChartView circleChartView;

    private TextView tvTimeDisplay, tvTotalExpense, tvTotalIncome, tvNetBalance;
    private Button btnMonthFilter, btnYearFilter;
    private ImageButton btnPrevTime, btnNextTime;

    private Calendar currentCalendar = Calendar.getInstance();
    private boolean isMonthMode = true;
    private List<TransactionModel> allTransactions = new ArrayList<>();
    private final DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = findViewById(R.id.toolbarReport);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Báo cáo chi tiêu");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTimeDisplay = findViewById(R.id.tvTimeDisplay);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvNetBalance = findViewById(R.id.tvNetBalance);

        btnMonthFilter = findViewById(R.id.btnMonthFilter);
        btnYearFilter = findViewById(R.id.btnYearFilter);
        btnPrevTime = findViewById(R.id.btnPrevTime);
        btnNextTime = findViewById(R.id.btnNextTime);

        circleChartView = findViewById(R.id.circleChartView);
        RecyclerView rvReportCategories = findViewById(R.id.rvReportCategories);

        adapter = new TransactionAdapter();
        rvReportCategories.setLayoutManager(new LinearLayoutManager(this));
        rvReportCategories.setAdapter(adapter);

        updateTimeDisplay();

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionViewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions != null) {
                allTransactions = transactions;
                filterAndProcessData();
            }
        });

        btnMonthFilter.setOnClickListener(v -> {
            isMonthMode = true;
            btnMonthFilter.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF9800")));
            btnYearFilter.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
            updateTimeDisplay();
            filterAndProcessData();
        });

        btnYearFilter.setOnClickListener(v -> {
            isMonthMode = false;
            btnYearFilter.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF9800")));
            btnMonthFilter.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
            updateTimeDisplay();
            filterAndProcessData();
        });

        btnPrevTime.setOnClickListener(v -> {
            if (isMonthMode) {
                currentCalendar.add(Calendar.MONTH, -1);
            } else {
                currentCalendar.add(Calendar.YEAR, -1);
            }
            updateTimeDisplay();
            filterAndProcessData();
        });

        btnNextTime.setOnClickListener(v -> {
            if (isMonthMode) {
                currentCalendar.add(Calendar.MONTH, 1);
            } else {
                currentCalendar.add(Calendar.YEAR, 1);
            }
            updateTimeDisplay();
            filterAndProcessData();
        });

        transactionViewModel.fetchTransactions("USER_TEST_01");
    }

    private void updateTimeDisplay() {
        int year = currentCalendar.get(Calendar.YEAR);
        if (isMonthMode) {
            int month = currentCalendar.get(Calendar.MONTH) + 1;
            String monthStr = month < 10 ? "0" + month : String.valueOf(month);
            tvTimeDisplay.setText(monthStr + "/" + year);
        } else {
            tvTimeDisplay.setText(String.valueOf(year));
        }
    }

    private void filterAndProcessData() {
        List<TransactionModel> filteredList = new ArrayList<>();
        double totalExpense = 0;
        double totalIncome = 0;

        int targetYear = currentCalendar.get(Calendar.YEAR);
        int targetMonth = currentCalendar.get(Calendar.MONTH);

        for (TransactionModel t : allTransactions) {
            if (t.getTimestamp() == null) continue;

            Calendar tCal = Calendar.getInstance();
            tCal.setTime(t.getTimestamp().toDate());

            boolean matches = false;
            if (isMonthMode) {
                if (tCal.get(Calendar.YEAR) == targetYear && tCal.get(Calendar.MONTH) == targetMonth) {
                    matches = true;
                }
            } else {
                if (tCal.get(Calendar.YEAR) == targetYear) {
                    matches = true;
                }
            }

            if (matches) {
                if ("EXPENSE".equals(t.getType())) {
                    totalExpense += t.getAmount();
                    filteredList.add(t);
                } else if ("INCOME".equals(t.getType())) {
                    totalIncome += t.getAmount();
                }
            }
        }

        tvTotalExpense.setText("Chi tiêu: -" + formatter.format(totalExpense) + "đ");
        tvTotalIncome.setText("Thu nhập: +" + formatter.format(totalIncome) + "đ");

        double netBalance = totalIncome - totalExpense;
        if (netBalance >= 0) {
            tvNetBalance.setText("Thu chi: +" + formatter.format(netBalance) + "đ");
        } else {
            tvNetBalance.setText("Thu chi: " + formatter.format(netBalance) + "đ");
        }

        adapter.setTransactions(filteredList);
        circleChartView.setTransactionData(filteredList);
    }
}