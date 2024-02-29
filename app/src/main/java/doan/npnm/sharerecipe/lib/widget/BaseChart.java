package doan.npnm.sharerecipe.lib.widget;

import static doan.npnm.sharerecipe.app.context.AppContext.getContext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.Toast;

import doan.npnm.sharerecipe.R;
import kotlin.jvm.JvmOverloads;

public abstract class BaseChart extends FrameLayout {

    public float width = 0f;
    public float height = 0f;
    public int leftDef = 0;
    public int topDef = 0;
    public int rightDef = 0;
    public int bottomDef = 0;
    public TypedArray typedArray;
    private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;


    public BaseChart(Context context) {
        this(context, null);

    }

    public BaseChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public int backgroundColor;
    public float radius;
    public int borderColor;
    public float borderWidth;

    public BaseChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseChart);
        backgroundColor = typedArray.getColor(R.styleable.BaseChart_chartBackground, Color.WHITE);
        radius = typedArray.getDimension(R.styleable.BaseChart_radius, 0f);
        borderColor = typedArray.getColor(R.styleable.BaseChart_borderColor, Color.BLACK);
        borderWidth = typedArray.getDimension(R.styleable.BaseChart_borderWidth, 0f);

        typedArray.recycle();
        setBackgroundColor(backgroundColor);
    }

    public void showToast(Object mess) {
        Toast.makeText(getContext(), mess.toString(), Toast.LENGTH_SHORT).show();
    }

    protected abstract void getTypedArr(Context context, AttributeSet attrs, int defStyleAttr);


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = (right - left);
        height = (bottom - top);
        this.leftDef = left;
        this.topDef = top;
        this.bottomDef = bottom;
        this.rightDef = right;
        textPaint.setTextSize(width * 0.032f);
        invalidate();
    }

    Paint textPaint = new Paint() {{
        setColor(Color.BLACK);
        setTextSize(40f);
        setTextAlign(Paint.Align.CENTER);
    }};

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getMeasuredWidth() > maxWidth || getMeasuredHeight() > maxHeight) {
            if (getMeasuredWidth() > maxWidth)
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY);
            if (getMeasuredHeight() > maxHeight)
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public double roundToFirstDecimal(double number) {
        return Math.round(number * 10) / 10.0;
    }

    public double getFirstValueDecimal(double number) {
        return (int) (number * 10.0) / 10.0;
    }

    public float toPx(int dp) {
        return android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getContext().getResources().getDisplayMetrics()
        );
    }

    public float toPx(float dp) {
        return android.util.TypedValue.applyDimension(
                android.util.TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getContext().getResources().getDisplayMetrics()
        );
    }

    public void drawTriangle(
            Canvas canvas,
            Paint borderPaint,
            PointF firstPoint,
            PointF secondPoint,
            PointF threePoint
    ) {
        Path shapePath = new Path();
        shapePath.moveTo(firstPoint.x, firstPoint.y);
        shapePath.lineTo(secondPoint.x, secondPoint.y);
        shapePath.lineTo(threePoint.x, threePoint.y);
        shapePath.close();
       canvas.drawPath(shapePath, borderPaint);
    }

    void drawRoundRectPath(
            Canvas canvas,
            RectF rectF,
            float radius,
            boolean topLeft,
            boolean topRight,
            boolean bottomLeft,
            boolean bottomRight,
            Paint paint
    ) {
        Path path = new Path();
        if (bottomRight) {
            path.moveTo(rectF.left, rectF.bottom - radius);
        } else {
            path.moveTo(rectF.left, rectF.bottom);
        }
        if (topLeft) {
            path.lineTo(rectF.left, rectF.top + radius);
            path.quadTo(rectF.left, rectF.top, rectF.left + radius, rectF.top);
        } else {
            path.lineTo(rectF.left, rectF.top);
        }
        if (topRight) {
            path.lineTo(rectF.right - radius, rectF.top);
            path.quadTo(rectF.right, rectF.top, rectF.right, rectF.top + radius);
        } else {
            path.lineTo(rectF.right, rectF.top);
        }
        if (bottomRight) {
            path.lineTo(rectF.right, rectF.bottom - radius);
            path.quadTo(rectF.right, rectF.bottom, rectF.right - radius, rectF.bottom);
        } else {
            path.lineTo(rectF.right, rectF.bottom);
        }
        if (bottomLeft) {
            path.lineTo(rectF.left + radius, rectF.bottom);
            path.quadTo(rectF.left, rectF.bottom, rectF.left, rectF.bottom - radius);
        } else {
            path.lineTo(rectF.left, rectF.bottom);
        }
        path.close();

       canvas.drawPath(path, paint);
    }

     void drawTriangle(
            Canvas canvas,
            Paint borderPaint,
            Point firstPoint,
            Point secondPoint,
            Point threePoint
    ) {
        Path shapePath = new Path();
        shapePath.moveTo(firstPoint.x, firstPoint.y);
        shapePath.lineTo(secondPoint.x, secondPoint.y);
        shapePath.lineTo(threePoint.x, threePoint.y);
        shapePath.close();
       canvas.drawPath(shapePath, borderPaint);
    }

}
