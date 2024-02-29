package doan.npnm.sharerecipe.adapter.users;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.Objects;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemRecipeHomeBinding;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class RecipeAdapter extends BaseAdapter<Recipe, ItemRecipeHomeBinding> {

    final OnRecipeEvent event;

    public RecipeAdapter(OnRecipeEvent event) {
        this.event = event;
    }

    @Override
    protected ItemRecipeHomeBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemRecipeHomeBinding.inflate(inflater, parent, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bind(ItemRecipeHomeBinding binding, Recipe item, int position) {
        binding.txtTimeCook.setText(item.CookTime.Time+" " + (item.CookTime.TimeType.equals("s") ? "second" :
                Objects.equals(item.CookTime.TimeType, "m") ? "minute" : "hour"));
        binding.chefName.setText(item.Name);
        binding.llSaveRecipe.setOnClickListener(v -> {
            event.onSave(item);
        });
        binding.getRoot().setOnClickListener(v -> event.onView(item));
        loadImage(item.ImgUrl,binding.imgChef);
    }

    public interface OnRecipeEvent {
        void onView(Recipe rcp);

        void onSave(Recipe recipe);
    }
}
