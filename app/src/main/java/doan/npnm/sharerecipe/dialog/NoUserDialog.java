package doan.npnm.sharerecipe.dialog;

import android.content.Context;
import android.view.View;

import doan.npnm.sharerecipe.base.BaseDialog;
import doan.npnm.sharerecipe.databinding.DialogNoUserBinding;
import doan.npnm.sharerecipe.databinding.DialogWarningBinding;
import doan.npnm.sharerecipe.lib.widget.TextValue;

public class NoUserDialog extends BaseDialog<DialogNoUserBinding> {
    final OnSenReportSelect select;
    public NoUserDialog(Context context, OnSenReportSelect select) {
        super(context);
        this.select=select;
    }

    @Override
    protected DialogNoUserBinding getBinding() {
        return DialogNoUserBinding.inflate(getLayoutInflater());
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