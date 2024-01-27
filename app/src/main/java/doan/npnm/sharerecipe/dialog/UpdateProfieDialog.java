package doan.npnm.sharerecipe.dialog;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseDialog;
import doan.npnm.sharerecipe.databinding.DialogUpdateProfileBinding;

public class UpdateProfieDialog extends BaseDialog {
    public UpdateProfieDialog(Context context,String message,OnUpdateSelect select) {
        super(context);

        DialogUpdateProfileBinding binding = DialogUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCancelable(false);

        binding.txtContent.setText(message);
        binding.bntCancel.setOnClickListener(v-> dismiss());
        binding.btnSave.setOnClickListener(v->{
            select.onSelect();
            dismiss();
        });
    }
     public interface OnUpdateSelect{
        void onSelect();
    }
}