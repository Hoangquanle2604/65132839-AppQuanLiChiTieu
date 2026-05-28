package lhq.cntt2.quanlychitieu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleChartView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();


    private final int[] colors = {
            Color.parseColor("#FF9800"), // Cam
            Color.parseColor("#4CAF50"), // Xanh lá
            Color.parseColor("#9C27B0"), // Tím
            Color.parseColor("#00BCD4"), // Xanh dương
            Color.parseColor("#E91E63")  // Hồng
    };

    public CircleChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int size = Math.min(getWidth(), getHeight());
        int padding = 40;
        rectF.set(padding, padding, size - padding, size - padding);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(60f);

        float startAngle = -90f;


        float[] sweepAngles = {100f, 120f, 40f, 60f, 40f};

        for (int i = 0; i < sweepAngles.length; i++) {
            paint.setColor(colors[i % colors.length]);
            canvas.drawArc(rectF, startAngle, sweepAngles[i], false, paint);
            startAngle += sweepAngles[i];
        }
    }
}