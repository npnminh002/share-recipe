package doan.npnm.sharerecipe.adapter.users;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemUserViewBinding;
import doan.npnm.sharerecipe.model.Users;

public class UsersAdapter extends BaseAdapter<Users, ItemUserViewBinding> {
    @Override
    protected ItemUserViewBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return  ItemUserViewBinding.inflate(inflater,parent,false);
    }

    @Override
    protected void bind(ItemUserViewBinding binding, Users item, int position) {
            binding.name.setText(item.UserName.toString());
    }
}
