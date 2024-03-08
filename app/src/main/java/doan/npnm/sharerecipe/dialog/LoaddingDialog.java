package doan.npnm.sharerecipe.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;

import doan.npnm.sharerecipe.base.BaseDialog;
import doan.npnm.sharerecipe.databinding.DialogLoaddingBinding;


public class LoaddingDialog extends BaseDialog<DialogLoaddingBinding> {
    @Override
    protected DialogLoaddingBinding getBinding() {
        return DialogLoaddingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(binding.imgLoading, "rotation", -360f, 0f);
        rotation.setDuration(800);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.start();
        setCancelable(false);
    }

    public LoaddingDialog(Context context) {
        super(context);
    }
}
