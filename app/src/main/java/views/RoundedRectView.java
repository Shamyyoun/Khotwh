package views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Shamyyoun on 12/1/\2015.
 */
public class RoundedRectView extends View {
    private String color; // hex color
    private Paint paint;

    public RoundedRectView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public RoundedRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public RoundedRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF oval = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawRoundRect(oval, 30, 30, paint);
    }

    public void setColor(String color) {
        this.color = color;
        paint.setColor(Color.parseColor(color));
        invalidate();
    }
}
