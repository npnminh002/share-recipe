package doan.npnm.sharerecipe.lib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

import doan.npnm.sharerecipe.R;

public class AutoAnimateView extends View {

    private Paint ellipsePaint;
    private Paint circlePaint;

    private float centerX; // X coordinate of ellipse center
    private float centerY; // Y coordinate of ellipse center
    private float radiusX; // X radius of ellipse
    private float radiusY; // Y radius of ellipse

    private float circleX; // X coordinate of moving circle
    private float circleY; // Y coordinate of moving circle
    private float circleRadius = 20; // Radius of moving circle

    private double angle = 0; // Angle for moving circle

    private Handler handler;
    private Runnable runnable;
    private Paint spiralPaint;

    private float a = 0; // Constant 'a' for the spiral equation
    private float b = 5; // Constant 'b' for the spiral equation



    public AutoAnimateView(Context context) {
        super(context);
        init();
    }

    public AutoAnimateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoAnnimte);
        init();
    }

    @SuppressLint("CustomViewStyleable")
    public AutoAnimateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoAnnimte);
        init();
    }


    private float amplitude = 100; // Amplitude of the sine wave
    private float frequency = 0.1f; // Frequency of the sine wave
    private float phaseShift = 0; // Phase shift of the sine wave
    private float verticalShift = 0; // Vertical shift of the sine wave
    private Paint sinePaint;



    int viewColor = Color.BLUE;

    public TypedArray typedArray;

    private void init() {
        viewColor = typedArray.getColor(R.styleable.AutoAnnimte_viewColor, viewColor);

        spiralPaint = new Paint();
        spiralPaint.setColor(Color.RED);
        spiralPaint.setStrokeWidth(3);
        spiralPaint.setStyle(Paint.Style.STROKE);

        sinePaint = new Paint();
        sinePaint.setColor(Color.RED);
        sinePaint.setStrokeWidth(3);
        sinePaint.setStyle(Paint.Style.STROKE);


        ellipsePaint = new Paint();
        ellipsePaint.setColor(viewColor);
        ellipsePaint.setStyle(Paint.Style.STROKE);
        ellipsePaint.setStrokeWidth(3);

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.FILL);

        // Initialize the position of the moving circle at the start of the ellipse
        circleX = centerX + radiusX;
        circleY = centerY;

        // Initialize handler and runnable
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Update the view
                updateView();
                // Schedule the next update after a delay
                handler.postDelayed(this, 10); // Update every 20 milliseconds
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Start the continuous updates when the view is attached to the window
        handler.post(runnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Stop the continuous updates when the view is detached from the window
        handler.removeCallbacks(runnable);
    }

    private void updateView() {
        // Calculate the position of the moving circle
        circleX = centerX + (float) (radiusX * Math.cos(angle));
        circleY = centerY + (float) (radiusY * Math.sin(angle));

        angle += 0.05;
        if (angle > Math.PI * 2) {
            angle = 0;
        }
        invalidate();
    }


    private void drawSineWave(Canvas canvas) {
        // Calculate the range of x-values to be drawn
        float startX = 0;
        float endX = getWidth();
        float stepSize = 5; // Step size for x-values

        // Move to the starting point
        float prevX = startX;
        float prevY = calculateSineValue(startX);
        for (float x = startX + stepSize; x <= endX; x += stepSize) {
            float y = calculateSineValue(x);
            canvas.drawLine(prevX, prevY, x, y, sinePaint);
            prevX = x;
            prevY = y;
        }
    }

    private float calculateSineValue(float x) {
        // Calculate the y-coordinate using the sinusoidal equation
        return amplitude * (float) Math.sin(frequency * x + phaseShift) + centerY + verticalShift;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate the center and radii of the ellipse based on view size
        centerX = w / 2f;
        centerY = h / 2f;
        radiusX = Math.min(w, h) / 3f;
        radiusY = Math.min(w, h) / 2f;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawOval(centerX - radiusX, centerY - radiusY, centerX + radiusX, centerY + radiusY, ellipsePaint);
//        canvas.drawCircle(circleX, circleY, circleRadius, circlePaint);
//        drawSineWave(canvas);

        drawSpiral(canvas);
    }

    private float radiusIncrement = 1; // Increment for increasing radius
    private float maxRadius = 300; // Maximum radius of the spiral

    private void drawSpiral(Canvas canvas) {
        float prevX = centerX;
        float prevY = centerY;

        float currentRadius = 0;

        while (currentRadius < maxRadius) {
            float x = centerX + currentRadius;
            float y = centerY;

            canvas.drawLine(prevX, prevY, x, y, spiralPaint);

            prevX = x;
            prevY = y;

            currentRadius += radiusIncrement;
        }
    }
}
