package doan.npnm.sharerecipe.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemAccountViewBinding;
import doan.npnm.sharerecipe.model.Users;

public class AccountAdapter extends BaseAdapter<Users, ItemAccountViewBinding> {
    final AccountEvent event;

    public AccountAdapter(AccountEvent event) {
        this.event = event;
    }

    @Override
    protected ItemAccountViewBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemAccountViewBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void bind(ItemAccountViewBinding binding, Users item, int position) {
        binding.imgUsers.loadImage(item.UrlImg == "" ? R.drawable.img_1 : item.UrlImg);
        binding.txtEmail.setText(item.Email);
        binding.txtUserName.setText(item.UserName);
        binding.txtDetail.setOnClickListener(v -> {
            event.onDetail(item);
        });
        binding.getRoot().setOnClickListener(v -> {
            event.onSelect(item);
        });
    }

    public interface AccountEvent {
        void onDetail(Users us);

        void onSelect(Users us);
    }
}
