package doan.npnm.sharerecipe.dialog;

import android.content.Context;

import doan.npnm.sharerecipe.base.BaseDialog;
import doan.npnm.sharerecipe.databinding.DialogDeleteBinding;
import doan.npnm.sharerecipe.databinding.DialogNoUserBinding;

public class DeleteDialog extends BaseDialog<DialogDeleteBinding> {
    final OnSenReportSelect select;
    public DeleteDialog(Context context, OnSenReportSelect select) {
        super(context);
        this.select=select;
    }

    @Override
    protected DialogDeleteBinding getBinding() {
        return DialogDeleteBinding.inflate(getLayoutInflater());
    }
    boolean isReport= false;
    @Override
    protected void initView() {
        setCancelable(false);
        binding.bntCancel.setOnClickListener(v-> dismiss());
        binding.btnsignIn.setOnClickListener(v->{
           select.onSingIn();
           dismiss();
        });
    }



    public interface OnSenReportSelect{
        void onSingIn();
    }
}