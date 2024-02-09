package doan.npnm.sharerecipe.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemTopChefViewHomeBinding;
import doan.npnm.sharerecipe.model.Users;

public class TopChefAdapter extends BaseAdapter<Users, ItemTopChefViewHomeBinding> {
    final OnAuthSelect event;

    public TopChefAdapter(OnAuthSelect event) {
        this.event = event;
    }

    @Override
    protected ItemTopChefViewHomeBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemTopChefViewHomeBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void bind(ItemTopChefViewHomeBinding binding, Users item, int position) {
        loadImage(item.UrlImg == "" ? R.drawable.img_demo_user : item.UrlImg, binding.imgChef);

        binding.recipeView.setText(""+item.Recipe);
        binding.chefName.setText(item.UserName);
         binding.getRoot().setOnClickListener(v -> event.onSelect(item));
    }
    public interface OnAuthSelect{
        void onSelect(Users us);
    }


}
