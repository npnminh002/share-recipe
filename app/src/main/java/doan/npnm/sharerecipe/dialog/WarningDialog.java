package doan.npnm.sharerecipe.dialog;

import android.content.Context;
import android.view.View;

import doan.npnm.sharerecipe.base.BaseDialog;
import doan.npnm.sharerecipe.databinding.DialogWarningBinding;
import doan.npnm.sharerecipe.lib.widget.TextValue;

public class WarningDialog extends BaseDialog<DialogWarningBinding> {
    final String message;
    final OnSenReportSelect select;
    public WarningDialog(Context context, String message, OnSenReportSelect select) {
        super(context);
        this.message=message;
        this.select=select;
    }
    TextValue content;

    @Override
    protected DialogWarningBinding getBinding() {
        return DialogWarningBinding.inflate(getLayoutInflater());
    }
    boolean isReport= false;
    @Override
    protected void initView() {
        setCancelable(false);
        content= new TextValue(binding.txtConten);
        binding.txtContent.setText(message);
        binding.bntCancel.setOnClickListener(v-> dismiss());
        binding.btnSave.setOnClickListener(v->{
           if(isReport){
              if(checkData()){
                  select.onSelect(content.value());
                  dismiss();
              }
               dismiss();
           }
           else {
               isReport=true;
               binding.llContentReport.setVisibility(View.VISIBLE);
           }
        });
    }

    private boolean checkData() {
        if(!content.isEmty()){
            content.onError();
            return false;
        }
        else {
            return true;
        }
    }

    public interface OnSenReportSelect{
        void onSelect(String content);
    }
}