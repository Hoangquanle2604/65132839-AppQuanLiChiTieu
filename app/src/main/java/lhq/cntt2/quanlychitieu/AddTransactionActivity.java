package lhq.cntt2.quanlychitieu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

public class AddTransactionActivity extends AppCompatActivity {
    private TransactionViewModel viewModel;

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

        RadioButton radioExpense = findViewById(R.id.radioExpense);
        EditText etAmount = findViewById(R.id.etAmount);
        EditText etCategory = findViewById(R.id.etCategory);
        EditText etNote = findViewById(R.id.etNote);
        Button btnSave = findViewById(R.id.btnSaveTransaction);

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
            String category = etCategory.getText().toString().trim();
            String note = etNote.getText().toString().trim();

            String type = "EXPENSE";
            if (!radioExpense.isChecked()) {
                type = "INCOME";
            }

            if (amountStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ số tiền và danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                viewModel.addTransaction("USER_TEST_01", amount, type, category, note);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền nhập vào không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}