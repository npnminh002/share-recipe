package doan.npnm.sharerecipe.adapter.users;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.database.models.LoveRecipe;
import doan.npnm.sharerecipe.databinding.ItemRecipeRecentBinding;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class RecipeLoveViewAdapter extends BaseAdapter<LoveRecipe, ItemRecipeRecentBinding> {

    final OnRecipeEvent event;

    public RecipeLoveViewAdapter(OnRecipeEvent event) {
        this.event = event;
    }

    @Override
    protected doan.npnm.sharerecipe.databinding.ItemRecipeRecentBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return doan.npnm.sharerecipe.databinding.ItemRecipeRecentBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void bind(doan.npnm.sharerecipe.databinding.ItemRecipeRecentBinding binding, LoveRecipe recent, int position) {
        Recipe item = new Recipe().fromJson(recent.Recipe);

        if (item != null) {
            binding.txtTimeCook.setText((item.CookTime.Time + item.CookTime.TimeType).equals("s") ? "second" : Objects.equals(item.CookTime.TimeType, "m") ? "minute" : "hour");
            binding.chefName.setText(item.Name);
            binding.llSaveRecipe.setOnClickListener(v -> {
                event.onRemove(item, position);
            });

            binding.llSaveRecipe.setImageResource(R.drawable.ic_delete_outline);
            binding.getRoot().setOnClickListener(v -> event.onView(item));
            loadImage(item.ImgUrl, binding.imgChef);
        }

    }

    public interface OnRecipeEvent {
        void onView(Recipe rcp);

        void onRemove(Recipe recipe, int pos);
    }
}
