package doan.npnm.sharerecipe.adapter.admin;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import java.util.function.Consumer;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemListMenuAdminBinding;

public class MenuItemAdapter extends BaseAdapter<String, ItemListMenuAdminBinding> {

    private final Consumer<String> event;

    public MenuItemAdapter(Consumer<String> event) {
        this.event = event;
    }

    @Override
    protected ItemListMenuAdminBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemListMenuAdminBinding.inflate(inflater, parent, false);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void bind(ItemListMenuAdminBinding binding, String item, int position) {

        binding.getRoot().setBackgroundResource(currentPosition == position ? R.drawable.bg_menu_item_select : R.drawable.bg_menu_item_unselect);
        binding.llNameItem.setText("" + item);
        binding.llNameItem.setTextColor(currentPosition==position? Color.parseColor("#ffffff") :R.color.appcolor);
        binding.getRoot().setOnClickListener(v -> {
            setCurrentPos(position);
            event.accept(item);
        });
    }
}
