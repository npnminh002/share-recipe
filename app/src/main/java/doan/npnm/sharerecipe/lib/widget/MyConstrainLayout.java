package doan.npnm.sharerecipe.lib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import doan.npnm.sharerecipe.R;

public class MyConstrainLayout extends ConstraintLayout {
    private Bitmap bgBitmap;

    public MyConstrainLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public MyConstrainLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyConstrainLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyConstrainLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        try {
            bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_my_constrain);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public Bitmap scaleBitmap(Bitmap bitmap, int targetWidth, int targetHeight) {

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_my_constrain);

        int targetWidth = canvas.getWidth() / 2;
        int targetHeight = canvas.getHeight() / 3;

        if (targetWidth > 0 && targetHeight > 0) {
            Bitmap scale = scaleBitmap(bgBitmap, targetWidth, targetHeight);
            canvas.drawBitmap(scale, 0f, 0f, new Paint());
        }

        super.dispatchDraw(canvas);
    }

}


