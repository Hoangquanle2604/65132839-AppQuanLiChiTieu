package lhq.cntt2.quanlychitieu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {
    private TransactionViewModel viewModel;
    private Spinner spinnerCategory;
    private TextView tvCategoryTitle; // Thêm để ẩn/hiện tiêu đề danh mục nếu cần
    private SharedPreferences sharedPreferences;
    private List<String> categoryList = new ArrayList<>();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // 1. Kiểm tra trạng thái đăng nhập để lấy Uid động
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(AddTransactionActivity.this, LoginActivity.class));
            finish();
            return;
        }
        currentUserId = user.getUid(); // Đây là ID riêng biệt của từng tài khoản

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thêm giao dịch");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Đổi cấu trúc đọc SharedPreferences theo Uid riêng biệt giống BudgetActivity
        sharedPreferences = getSharedPreferences("BudgetPrefs_" + currentUserId, Context.MODE_PRIVATE);

        RadioGroup radioGroupType = findViewById(R.id.radioGroupType); // Giả định layout có RadioGroup bọc 2 nút Radio
        RadioButton radioExpense = findViewById(R.id.radioExpense);
        RadioButton radioIncome = findViewById(R.id.radioIncome);
        EditText etAmount = findViewById(R.id.etAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        // Cố gắng ánh xạ TextView tiêu đề danh mục (nếu trong xml có, ví dụ: tvCategory hoặc tương tự)
        // Nếu không có, ông có thể bỏ qua biến tvCategoryTitle này
        int resTvCatId = getResources().getIdentifier("tvCategoryTitle", "id", getPackageName());
        if (resTvCatId != 0) {
            tvCategoryTitle = findViewById(resTvCatId);
        }

        EditText etNote = findViewById(R.id.etNote);
        Button btnSave = findViewById(R.id.btnSaveTransaction);

        loadCategoriesFromBudget();

        // 2. Lắng nghe sự kiện thay đổi Thu nhập / Chi tiêu để ẩn/hiện Spinner danh mục
        if (radioExpense != null && radioIncome != null) {
            radioExpense.setOnClickListener(v -> {
                spinnerCategory.setVisibility(View.VISIBLE);
                if (tvCategoryTitle != null) tvCategoryTitle.setVisibility(View.VISIBLE);
            });

            radioIncome.setOnClickListener(v -> {
                spinnerCategory.setVisibility(View.GONE); // Ẩn Spinner chọn danh mục chi tiêu đi
                if (tvCategoryTitle != null) tvCategoryTitle.setVisibility(View.GONE);
            });
        }

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

            String selectedCategory = "Thu nhập"; // Mặc định nếu là INCOME thì gán danh mục là "Thu nhập"

            if ("EXPENSE".equals(type)) {
                if (spinnerCategory.getSelectedItem() == null || "Chưa có ngân sách".equals(spinnerCategory.getSelectedItem().toString())) {
                    Toast.makeText(this, "Vui lòng thiết lập danh mục ngân sách trước khi tạo khoản chi", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedCategory = spinnerCategory.getSelectedItem().toString();
            }

            try {
                double amount = Double.parseDouble(amountStr);
                // 3. Thay thế USER_TEST_01 bằng currentUserId động
                viewModel.addTransaction(currentUserId, amount, type, selectedCategory, note);
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