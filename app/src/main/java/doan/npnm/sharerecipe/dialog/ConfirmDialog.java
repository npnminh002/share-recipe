package doan.npnm.sharerecipe.dialog;

import android.content.Context;

import doan.npnm.sharerecipe.base.BaseDialog;
import doan.npnm.sharerecipe.databinding.DialogConfirmBinding;

public class ConfirmDialog extends BaseDialog {
    public ConfirmDialog(Context context, String message, OnUpdateSelect select) {
        super(context);

        DialogConfirmBinding binding = DialogConfirmBinding.inflate(getLayoutInflater());
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