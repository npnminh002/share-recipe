package doan.npnm.sharerecipe.adapter.users;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemCategoryFoodBinding;
import doan.npnm.sharerecipe.model.Category;

public class CategoryAdapter extends BaseAdapter<Category, ItemCategoryFoodBinding> {
    final OnCategoryEvent event;

    public CategoryAdapter(OnCategoryEvent event) {
        this.event = event;
    }

    @Override
    protected ItemCategoryFoodBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemCategoryFoodBinding.inflate(inflater, parent, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void bind(ItemCategoryFoodBinding binding, Category item, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Đặt màu nền cho item trong danh sách
            binding.llItem.setBackgroundColor(position == currentPosition ? Color.parseColor("#FFE0FFFF") : Color.parseColor("#FFe5e5e5"));
        }
        // Load hình ảnh cho item
        loadImage(item.Image, binding.imgItem);
        // Đặt tên cho item
        binding.txtName.setText(item.Name);
        // Thiết lập sự kiện khi item được chọn
        binding.getRoot().setOnClickListener(v -> {
            event.onSelect(item);
            setCurrentPos(position);
        });
    }

    // Interface để xử lý sự kiện khi một category được chọn
    public interface OnCategoryEvent {
        void onSelect(Category category);
    }
}
