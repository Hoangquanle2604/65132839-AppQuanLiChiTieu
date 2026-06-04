package lhq.cntt2.quanlychitieu;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {
    private TransactionViewModel transactionViewModel;
    private BudgetAdapter adapter;
    private EditText etBudgetCategory, etBudgetLimit;
    private SharedPreferences sharedPreferences;
    private List<TransactionModel> currentTransactions = new ArrayList<>();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // 1. Lấy Uid động của tài khoản hiện tại để tách biệt dữ liệu ngân sách
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(BudgetActivity.this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = user.getUid();

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


        sharedPreferences = getSharedPreferences("BudgetPrefs_" + currentUserId, Context.MODE_PRIVATE);

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

            try {
                //==>>>>>
                float limit = Float.parseFloat(limitStr);
                sharedPreferences.edit().putFloat(category.toLowerCase(), limit).apply();

                etBudgetCategory.setText("");
                etBudgetLimit.setText("");
                loadBudgetList();
                Toast.makeText(this, "Thiết lập thành công", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnBudgetDeleteListener(category -> {
            sharedPreferences.edit().remove(category.toLowerCase()).apply();
            loadBudgetList();
        });


        transactionViewModel.fetchTransactions(currentUserId);
    }

    private void loadBudgetList() {
        List<BudgetModel> budgets = new ArrayList<>();
        if (sharedPreferences != null) {
            Map<String, ?> allEntries = sharedPreferences.getAll();
            if (allEntries != null) {
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
            }
        }
        adapter.setData(budgets, currentTransactions);
    }
}