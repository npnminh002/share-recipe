package doan.npnm.sharerecipe.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Objects;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.databinding.ItemRecipeHomeBinding;
import doan.npnm.sharerecipe.databinding.ItemRecipeRecentBinding;
import doan.npnm.sharerecipe.interfaces.OnRecipeEvent;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class RecipeRecentViewAdapter extends BaseAdapter<RecentView, ItemRecipeRecentBinding> {

    final OnRecipeEvent event;

    public RecipeRecentViewAdapter(OnRecipeEvent event) {
        this.event = event;
    }

    @Override
    protected ItemRecipeRecentBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemRecipeRecentBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void bind(ItemRecipeRecentBinding binding, RecentView recent, int position) {
        Recipe item= new Recipe().fromJson(recent.Recipe);
        binding.txtTimeCook.setText((item.CookTime.Time + item.CookTime.TimeType).equals("s") ? "second" : Objects.equals(item.CookTime.TimeType, "m") ? "minute" : "hour");
        binding.chefName.setText(item.Name);
        binding.llSaveRecipe.setOnClickListener(v -> {
            event.onSave(item);
        });
        binding.getRoot().setOnClickListener(v -> event.onView(item));
        loadImage(item.ImgUrl,binding.imgChef);
        binding.txtTimeView.setText(recent.ViewTime);

    }
}

