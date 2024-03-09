package doan.npnm.sharerecipe.dialog;

import android.content.Context;

import doan.npnm.sharerecipe.base.BaseDialog;
import doan.npnm.sharerecipe.databinding.DialogDeleteBinding;

public class ReportDialog extends BaseDialog<DialogDeleteBinding> {
    final OnSenReportSelect select;
    public ReportDialog(Context context, OnSenReportSelect select) {
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