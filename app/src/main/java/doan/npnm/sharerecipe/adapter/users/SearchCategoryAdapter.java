package doan.npnm.sharerecipe.adapter.users;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemCategorySearchBinding;
import doan.npnm.sharerecipe.model.Category;

public class SearchCategoryAdapter extends BaseAdapter<Category, ItemCategorySearchBinding> {
    final OnCategoryEvent event;

    public SearchCategoryAdapter(OnCategoryEvent event) {
        this.event = event;
    }

    @Override
    protected ItemCategorySearchBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemCategorySearchBinding.inflate(inflater, parent, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void bind(ItemCategorySearchBinding binding, Category item, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.llItem.setBackgroundColor(position == currentPosition ? Color.parseColor("#FFE0FFFF") :Color.parseColor("#ffffff"));
        }
        loadImage(item.Image, binding.imgItem);
        binding.txtName.setText(item.Name);
        binding.getRoot().setOnClickListener(v -> {
            event.onSelect(item);
           setCurrentPos(position);
        });
    }

    public interface OnCategoryEvent {
        void onSelect(Category category);
    }
}
