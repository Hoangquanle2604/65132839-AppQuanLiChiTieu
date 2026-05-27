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

        // --- CẤU HÌNH TOOLBAR ĐỂ CÓ NÚT QUAY LẠI ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiện nút Back
            getSupportActionBar().setTitle("Thêm giao dịch");
        }
        // Xử lý nút Back mặc định của Toolbar
        toolbar.setNavigationOnClickListener(v -> finish());
        // --------------------------------------------

        // Khởi tạo ViewModel cho màn hình AddTransaction
        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Ánh xạ các View từ XML
        RadioButton radioExpense = findViewById(R.id.radioExpense);
        EditText etAmount = findViewById(R.id.etAmount);
        EditText etCategory = findViewById(R.id.etCategory);
        EditText etNote = findViewById(R.id.etNote);
        Button btnSave = findViewById(R.id.btnSaveTransaction);

        // Lắng nghe trạng thái thêm giao dịch thành công để đóng màn hình
        viewModel.getAddSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();

                // ĐÃ TỐI ƯU: Reset lại trạng thái để không bị lỗi tự động đóng vào những lần mở sau
                // Do chúng ta không thể gọi trực tiếp setValue trên LiveData thường từ View,
                // lệnh finish() dưới đây sẽ giải phóng Activity này một cách an toàn.
                finish();
            }
        });

        // Lắng nghe thông báo lỗi nếu có từ ViewModel
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });

        // Xử lý sự kiện khi click nút Lưu giao dịch
        btnSave.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String note = etNote.getText().toString().trim();
            String type = radioExpense.isChecked() ? "EXPENSE" : "INCOME";

            // Kiểm tra dữ liệu đầu vào không được để trống
            if (amountStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ số tiền và danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);

                // Gọi hàm thêm giao dịch từ ViewModel
                viewModel.addTransaction("USER_TEST_01", amount, type, category, note);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền nhập vào không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}