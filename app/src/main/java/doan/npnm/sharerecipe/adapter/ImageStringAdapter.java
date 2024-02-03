package doan.npnm.sharerecipe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.annotation.Nullable;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemImageRecipeDetailBinding;

public class ImageStringAdapter extends BaseAdapter<String, ItemImageRecipeDetailBinding> {

    final ImageItemEvent event;


    public ImageStringAdapter(ImageItemEvent event) {
        this.event = event;
    }

    @Override
    protected ItemImageRecipeDetailBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemImageRecipeDetailBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void bind(ItemImageRecipeDetailBinding binding, @Nullable String item, int position) {

        binding.imgProduct.setVisibility(View.VISIBLE);
        loadImage(item, binding.imgProduct);
        binding.getRoot().setOnClickListener(v -> event.onView(item));
    }


    public interface ImageItemEvent {
        void onView(String url);
    }
}
