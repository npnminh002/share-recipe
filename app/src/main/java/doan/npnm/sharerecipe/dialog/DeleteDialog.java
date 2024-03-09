package doan.npnm.sharerecipe.dialog;

import android.content.Context;
import android.view.View;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseDialog;
import doan.npnm.sharerecipe.databinding.DialogDeleteBinding;
import doan.npnm.sharerecipe.databinding.DialogWarningBinding;
import doan.npnm.sharerecipe.lib.widget.TextValue;

public class DeleteDialog extends BaseDialog<DialogDeleteBinding> {
    final String message;
    final OnDialogSelect select;
    public DeleteDialog(Context context, String message, OnDialogSelect select) {
        super(context);
        this.message=message;
        this.select=select;
    }
    TextValue content;

    @Override
    protected DialogDeleteBinding getBinding() {
        return DialogDeleteBinding.inflate(getLayoutInflater());
    }
    @Override
    protected void initView() {
        setCancelable(false);
        content= new TextValue(binding.txtConten);
        binding.txtContent.setText(message);
        binding.btnsignIn.setText(getContext().getString(R.string.delete));
        binding.bntCancel.setOnClickListener(v-> dismiss());
        binding.btnsignIn.setOnClickListener(v->{
            select.onDelete();
        });
    }

    public interface OnDialogSelect{
        void onDelete();
    }
}