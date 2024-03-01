package doan.npnm.sharerecipe.dialog;

import android.content.Context;

import doan.npnm.sharerecipe.base.BaseDialog;
import doan.npnm.sharerecipe.databinding.DialogConfirmBinding;

public class ConfirmDialog extends BaseDialog<DialogConfirmBinding> {
    final String message;
    final OnUpdateSelect select;
    public ConfirmDialog(Context context, String message, OnUpdateSelect select) {
        super(context);
        this.message=message;
        this.select=select;
    }

    @Override
    protected DialogConfirmBinding getBinding() {
        return DialogConfirmBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
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