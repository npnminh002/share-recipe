package doan.npnm.sharerecipe.lib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import doan.npnm.sharerecipe.R;

public class CheckBoxImageView extends ImageView {

    private boolean checked = false;
    private int defImageRes = 0;
    private int checkedImageRes = 0;
    private OnCheckedChangeListener onCheckedChangeListener;

    public CheckBoxImageView(Context context) {
        this(context, null);
    }

    public CheckBoxImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBoxImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attributeSet, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(
                attributeSet,
                R.styleable.CheckBoxImageView,
                defStyle,
                0
        );
        defImageRes = a.getResourceId(R.styleable.CheckBoxImageView_default_img, 0);
        checkedImageRes = a.getResourceId(R.styleable.CheckBoxImageView_checked_img, 0);
        checked = a.getBoolean(R.styleable.CheckBoxImageView_checked, false);
        setImageResource(isChecked() ? checkedImageRes : defImageRes);
        a.recycle();

        setOnClickListener(v -> CheckBoxImageView.this.onClick());
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        setImageResource(isChecked() ? checkedImageRes : defImageRes);
    }

    @SuppressLint("ResourceType")
    private void onClick() {
        checked = !checked;
        setChecked(checked);
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View buttonView, boolean isChecked);
    }
}
