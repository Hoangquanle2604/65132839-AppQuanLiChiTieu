package lhq.cntt2.quanlychitieu;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    private TransactionViewModel transactionViewModel;
    private TransactionAdapter adapter;
    private TextView tvCenterStatus;

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

        tvCenterStatus = findViewById(R.id.tvCenterStatus);
        RecyclerView rvReportCategories = findViewById(R.id.rvReportCategories);

        adapter = new TransactionAdapter();
        rvReportCategories.setLayoutManager(new LinearLayoutManager(this));
        rvReportCategories.setAdapter(adapter);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionViewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions != null) {
                List<TransactionModel> expenseList = new ArrayList<>();
                for (TransactionModel t : transactions) {
                    if ("EXPENSE".equals(t.getType())) {
                        expenseList.add(t);
                    }
                }
                adapter.setTransactions(expenseList);
                if (!expenseList.isEmpty()) {
                    tvCenterStatus.setText("100.0%");
                }
            }
        });
        transactionViewModel.fetchTransactions("USER_TEST_01");
    }
}