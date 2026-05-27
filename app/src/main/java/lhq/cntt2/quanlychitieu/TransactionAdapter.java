package lhq.cntt2.quanlychitieu;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<TransactionModel> list = new ArrayList<>();
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(TransactionModel transaction);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setTransactions(List<TransactionModel> transactions) {
        if (transactions != null) {
            this.list = transactions;
        } else {
            this.list = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionModel tm = list.get(position);

        holder.tvCategory.setText(tm.getCategory() != null ? tm.getCategory() : "Không rõ");
        holder.tvNote.setText(tm.getNote() != null ? tm.getNote() : "");

        DecimalFormat df = new DecimalFormat("#,### đ");
        if ("EXPENSE".equals(tm.getType())) {
            holder.tvAmount.setText("- " + df.format(tm.getAmount()));
            holder.tvAmount.setTextColor(Color.RED);
        } else {
            holder.tvAmount.setText("+ " + df.format(tm.getAmount()));
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        }

        if (holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(tm);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvNote, tvAmount;
        ImageView btnDelete;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}