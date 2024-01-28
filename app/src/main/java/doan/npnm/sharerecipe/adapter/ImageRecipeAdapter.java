package doan.npnm.sharerecipe.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.annotation.Nullable;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemImageRecipeBinding;

public class ImageRecipeAdapter extends BaseAdapter<Uri, ItemImageRecipeBinding> {

    final ImageItemEvent event;

    public ImageRecipeAdapter(ImageItemEvent event) {
        this.event = event;
    }

    @Override
    protected ItemImageRecipeBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemImageRecipeBinding.inflate(inflater,parent,false);
    }

    @Override
    protected void bind(ItemImageRecipeBinding binding, @Nullable Uri item, int position) {
        if(item==null){
            binding.llAddImage.setVisibility(View.VISIBLE);
            binding.imgProduct.setVisibility(View.GONE);
        }
        else {
            binding.llAddImage.setVisibility(View.GONE);
            binding.imgProduct.setVisibility(View.VISIBLE);
            loadImage(item,binding.imgProduct);
        }


        binding.llAddImage.setOnClickListener(v->event.onAdd());
        binding.imgProduct.setOnLongClickListener(v->{
            event.onRemove(position);
            return true;
        });
    }

    public interface ImageItemEvent{
        void onAdd();
        void onRemove(int pos);
    }
}
