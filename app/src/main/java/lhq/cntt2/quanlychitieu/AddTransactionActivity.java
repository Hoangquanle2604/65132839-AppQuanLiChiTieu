package lhq.cntt2.quanlychitieu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {
    private TransactionViewModel viewModel;
    private Spinner spinnerCategory;
    private SharedPreferences sharedPreferences;
    private List<String> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thêm giao dịch");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        sharedPreferences = getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);

        RadioButton radioExpense = findViewById(R.id.radioExpense);
        EditText etAmount = findViewById(R.id.etAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        EditText etNote = findViewById(R.id.etNote);
        Button btnSave = findViewById(R.id.btnSaveTransaction);

        loadCategoriesFromBudget();

        viewModel.getAddSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });

        btnSave.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            String note = etNote.getText().toString().trim();

            String type = "EXPENSE";
            if (!radioExpense.isChecked()) {
                type = "INCOME";
            }

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spinnerCategory.getSelectedItem() == null) {
                Toast.makeText(this, "Vui lòng thiết lập danh mục ngân sách trước", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedCategory = spinnerCategory.getSelectedItem().toString();

            try {
                double amount = Double.parseDouble(amountStr);
                viewModel.addTransaction("USER_TEST_01", amount, type, selectedCategory, note);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền nhập vào không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategoriesFromBudget() {
        categoryList.clear();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof Float) {
                String catName = entry.getKey();
                if (catName != null && !catName.isEmpty()) {
                    String formattedName = catName.substring(0, 1).toUpperCase() + catName.substring(1);
                    categoryList.add(formattedName);
                }
            }
        }

        if (categoryList.isEmpty()) {
            categoryList.add("Chưa có ngân sách");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }
}