package doan.npnm.sharerecipe.adapter.admin;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.TableLayout;

import java.util.function.Consumer;

import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.base.BaseTableAdapter;
import doan.npnm.sharerecipe.databinding.RowDiscussionViewAdminBinding;
import doan.npnm.sharerecipe.model.disscus.Discussion;
import doan.npnm.sharerecipe.model.disscus.DisscusAuth;

public class DiscussionTableAdapter extends BaseTableAdapter<Discussion, RowDiscussionViewAdminBinding> {
    private final Consumer<DisscusAuth> event;
    public DiscussionTableAdapter(TableLayout tableLayout, Consumer<DisscusAuth> us) {
        super(tableLayout);
        this.event=us;
    }

    @Override
    protected RowDiscussionViewAdminBinding initLayout() {
        return RowDiscussionViewAdminBinding.inflate(LayoutInflater.from(AppContext.getContext()));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBind(RowDiscussionViewAdminBinding binding, Discussion item, int position) {
        binding.txtCount.setText(String.valueOf(position));
        binding.txtUserName.setText(item.DisscusAuth.AuthName);
        binding.txtUserID.setText(item.DisscusAuth.AuthId);
        binding.txtContent.setText(item.Content);
        binding.txtTime.setText(item.Time);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.getRoot().setOnClickListener(v->event.accept(item.DisscusAuth));
        }
    }
}
