package lhq.cntt2.quanlychitieu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {
    private TransactionViewModel transactionViewModel;
    private BudgetAdapter adapter;
    private EditText etBudgetCategory, etBudgetLimit;
    private SharedPreferences sharedPreferences;
    private List<TransactionModel> currentTransactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        Toolbar toolbar = findViewById(R.id.toolbarBudget);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý Ngân sách");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etBudgetCategory = findViewById(R.id.etBudgetCategory);
        etBudgetLimit = findViewById(R.id.etBudgetLimit);
        Button btnSaveBudget = findViewById(R.id.btnSaveBudget);
        RecyclerView rvBudgets = findViewById(R.id.rvBudgets);

        sharedPreferences = getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);
        adapter = new BudgetAdapter();
        rvBudgets.setLayoutManager(new LinearLayoutManager(this));
        rvBudgets.setAdapter(adapter);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionViewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions != null) {
                currentTransactions = transactions;
                loadBudgetList();
            }
        });

        btnSaveBudget.setOnClickListener(v -> {
            String category = etBudgetCategory.getText().toString().trim();
            String limitStr = etBudgetLimit.getText().toString().trim();

            if (category.isEmpty() || limitStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            float limit = Float.parseFloat(limitStr);
            sharedPreferences.edit().putFloat(category.toLowerCase(), limit).apply();

            etBudgetCategory.setText("");
            etBudgetLimit.setText("");
            loadBudgetList();
            Toast.makeText(this, "Thiết lập thành công", Toast.LENGTH_SHORT).show();
        });

        adapter.setOnBudgetDeleteListener(category -> {
            sharedPreferences.edit().remove(category.toLowerCase()).apply();
            loadBudgetList();
        });

        transactionViewModel.fetchTransactions("USER_TEST_01");
    }

    private void loadBudgetList() {
        List<BudgetModel> budgets = new ArrayList<>();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof Float) {
                String originalCategory = entry.getKey();
                if (!currentTransactions.isEmpty()) {
                    for (TransactionModel t : currentTransactions) {
                        if (t.getCategory().equalsIgnoreCase(originalCategory)) {
                            originalCategory = t.getCategory();
                            break;
                        }
                    }
                }
                budgets.add(new BudgetModel(originalCategory, (Float) entry.getValue()));
            }
        }
        adapter.setData(budgets, currentTransactions);
    }
}