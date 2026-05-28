package lhq.cntt2.quanlychitieu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleChartView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();
    private final List<Float> sweepAngles = new ArrayList<>();

    private final int[] colors = {
            Color.parseColor("#FF9800"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#00BCD4"),
            Color.parseColor("#E91E63")
    };

    public CircleChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTransactionData(List<TransactionModel> expenseList) {
        sweepAngles.clear();
        if (expenseList == null || expenseList.isEmpty()) {
            invalidate();
            return;
        }

        Map<String, Double> categoryMap = new HashMap<>();
        double totalExpense = 0;

        for (TransactionModel t : expenseList) {
            double amount = t.getAmount();
            totalExpense += amount;
            categoryMap.put(t.getCategory(), categoryMap.getOrDefault(t.getCategory(), 0.0) + amount);
        }

        if (totalExpense > 0) {
            for (double categoryAmount : categoryMap.values()) {
                float angle = (float) ((categoryAmount / totalExpense) * 360.0);
                sweepAngles.add(angle);
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int size = Math.min(getWidth(), getHeight());
        int padding = 40;
        rectF.set(padding, padding, size - padding, size - padding);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(60f);

        if (sweepAngles.isEmpty()) {
            paint.setColor(Color.parseColor("#E0E0E0"));
            canvas.drawArc(rectF, 0, 360, false, paint);
            return;
        }

        float startAngle = -90f;

        for (int i = 0; i < sweepAngles.size(); i++) {
            paint.setColor(colors[i % colors.length]);
            canvas.drawArc(rectF, startAngle, sweepAngles.get(i), false, paint);
            startAngle += sweepAngles.get(i);
        }
    }
}