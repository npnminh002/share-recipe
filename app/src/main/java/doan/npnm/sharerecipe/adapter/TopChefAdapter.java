package doan.npnm.sharerecipe.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemTopChefViewHomeBinding;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class TopChefAdapter extends BaseAdapter<Recipe, ItemTopChefViewHomeBinding> {
    @Override
    protected ItemTopChefViewHomeBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemTopChefViewHomeBinding.inflate(inflater,parent,false);
    }

    @Override
    protected void bind(ItemTopChefViewHomeBinding binding, Recipe item, int position) {
        loadImage(item.RecipeAuth.Image,binding.imgChef);
        new Thread(()->{
            FirebaseFirestore.getInstance().collection(Constant.KEY_USER)
                    .document(item.RecipeAuth.AuthId)
                    .get()
                    .addOnCompleteListener(task -> {
                        binding.recipeView.setText(Objects.requireNonNull(task.getResult().get("Recipe")).toString());
                    });
        }).start();
        binding.chefName.setText(item.RecipeAuth.AuthName);
    }
}
