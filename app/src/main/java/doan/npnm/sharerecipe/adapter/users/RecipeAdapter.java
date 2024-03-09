package doan.npnm.sharerecipe.adapter.users;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.database.AppDatabase;
import doan.npnm.sharerecipe.databinding.ItemRecipeHomeBinding;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class RecipeAdapter extends BaseAdapter<Recipe, ItemRecipeHomeBinding> {

    final OnRecipeEvent event;
    final AppDatabase database;

    public RecipeAdapter(OnRecipeEvent event, AppDatabase database) {
        this.event = event;
        this.database = database;
    }

    @Override
    protected ItemRecipeHomeBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemRecipeHomeBinding.inflate(inflater, parent, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bind(ItemRecipeHomeBinding binding, Recipe item, int position) {

        boolean isLove = database.loveRecipeDao().checkExist(item.Id);
        if (isLove) {
            binding.llIcLove.setColorFilter(Color.parseColor("#FF0000"));
        } else {
            binding.llIcLove.setColorFilter(Color.parseColor("#ffffff"));
        }

        binding.txtView.setText("" + item.View);
        binding.txtTimeCook.setText(item.CookTime.Time + " " + (item.CookTime.TimeType.equals("s") ? "second" :
                Objects.equals(item.CookTime.TimeType, "m") ? "minute" : "hour"));
        binding.chefName.setText(item.Name);
        binding.llIcLove.setOnClickListener(v -> {
            event.onLove(item,position,isLove);
            Toast.makeText(AppContext.getContext(), ""+isLove, Toast.LENGTH_SHORT).show();
        });
        binding.getRoot().setOnClickListener(v -> event.onView(item));
        loadImage(item.ImgUrl, binding.imgChef);
    }

    public interface OnRecipeEvent {
        void onView(Recipe rcp);

        void onLove(Recipe recipe,int pos,boolean isLove);
    }
}
