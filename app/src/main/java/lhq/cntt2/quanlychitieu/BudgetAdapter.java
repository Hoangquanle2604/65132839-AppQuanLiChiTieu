package lhq.cntt2.quanlychitieu;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<BudgetModel> budgetList = new ArrayList<>();
    private List<TransactionModel> transactionList = new ArrayList<>();
    private OnBudgetDeleteListener deleteListener;
    private final DecimalFormat df = new DecimalFormat("#,### đ");

    public interface OnBudgetDeleteListener {
        void onDelete(String category);
    }

    public void setOnBudgetDeleteListener(OnBudgetDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setData(List<BudgetModel> budgets, List<TransactionModel> transactions) {
        this.budgetList = budgets;
        this.transactionList = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int viewType) {
        BudgetModel budget = budgetList.get(viewType);
        holder.tvBudgetItemCategory.setText(budget.getCategory());
        holder.tvBudgetLimit.setText("Hạn mức: " + df.format(budget.getLimitAmount()));

        double spent = 0;
        for (TransactionModel t : transactionList) {
            if ("EXPENSE".equals(t.getType()) && budget.getCategory().equalsIgnoreCase(t.getCategory())) {
                spent += t.getAmount();
            }
        }

        holder.tvBudgetSpent.setText("Đã chi: " + df.format(spent));

        int progress = (int) ((spent / budget.getLimitAmount()) * 100);
        holder.pbBudgetProgress.setProgress(Math.min(progress, 100));

        if (spent > budget.getLimitAmount()) {
            holder.tvBudgetSpent.setTextColor(Color.RED);
            holder.pbBudgetProgress.setProgressDrawable(holder.itemView.getContext().getResources().getDrawable(android.R.drawable.progress_horizontal));
        } else {
            holder.tvBudgetSpent.setTextColor(Color.parseColor("#E53935"));
        }

        holder.imgDeleteBudget.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(budget.getCategory());
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvBudgetItemCategory, tvBudgetSpent, tvBudgetLimit;
        ProgressBar pbBudgetProgress;
        ImageView imgDeleteBudget;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBudgetItemCategory = itemView.findViewById(R.id.tvBudgetItemCategory);
            tvBudgetSpent = itemView.findViewById(R.id.tvBudgetSpent);
            tvBudgetLimit = itemView.findViewById(R.id.tvBudgetLimit);
            pbBudgetProgress = itemView.findViewById(R.id.pbBudgetProgress);
            imgDeleteBudget = itemView.findViewById(R.id.imgDeleteBudget);
        }
    }
}