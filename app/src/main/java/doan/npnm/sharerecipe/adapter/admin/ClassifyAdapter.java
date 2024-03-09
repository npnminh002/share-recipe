package doan.npnm.sharerecipe.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemCategoryClassifyBinding;
import doan.npnm.sharerecipe.model.Category;

public class ClassifyAdapter extends BaseAdapter<Category, ItemCategoryClassifyBinding> {

   private final ClassifyManager classifyManager;
   private final OnEventCategory eventCategory;

    public ClassifyAdapter(ClassifyManager classifyManager, OnEventCategory eventCategory) {
        this.classifyManager = classifyManager;
        this.eventCategory = eventCategory;
    }

    public enum ClassifyManager{
        ADD,
        REMOVE
    }
    @Override
    protected ItemCategoryClassifyBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemCategoryClassifyBinding.inflate(inflater,parent,false);
    }

    @Override
    protected void bind(ItemCategoryClassifyBinding binding, Category item, int position) {
        binding.imgPreivew.loadImage(item.Image);
        binding.txtName.setText(item.Name);
        if(classifyManager==ClassifyManager.REMOVE){
            binding.btnAdd.setOnClickListener(v -> eventCategory.onRemove(item));
            binding.btnAdd.setImageResource(R.drawable.ic_delete_outline);
        }
        else {

            binding.btnAdd.setOnClickListener(v -> eventCategory.onAdd(item));

        }

    }

    public interface OnEventCategory{
        void onAdd(Category ct);
        void onRemove(Category category);
    }
}
