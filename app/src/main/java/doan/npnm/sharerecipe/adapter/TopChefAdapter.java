package doan.npnm.sharerecipe.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemTopChefViewHomeBinding;
import doan.npnm.sharerecipe.model.recipe.RecipeAuth;
import doan.npnm.sharerecipe.utility.Constant;

public class TopChefAdapter extends BaseAdapter<RecipeAuth, ItemTopChefViewHomeBinding> {
    @Override
    protected ItemTopChefViewHomeBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemTopChefViewHomeBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void bind(ItemTopChefViewHomeBinding binding, RecipeAuth item, int position) {
        loadImage(item.Image, binding.imgChef);
        new Thread(() -> {
            FirebaseFirestore.getInstance().collection(Constant.KEY_USER)
                    .document(item.AuthId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Check if the "Recipe" field exists
                                if (document.contains("Recipe")) {
                                    Object recipeValue = document.get("Recipe");
                                    if (recipeValue instanceof Long) {
                                        // If the value is a long (which is Firestore's representation of integer), cast and set it
                                        long recipe = (long) recipeValue;
                                        binding.recipeView.setText(String.valueOf(recipe));
                                    } else if (recipeValue instanceof String) {
                                        // If the value is a string, attempt to parse it as an integer
                                        try {
                                            int recipe = Integer.parseInt((String) recipeValue);
                                            binding.recipeView.setText(String.valueOf(recipe));
                                        } catch (NumberFormatException e) {
                                            // Handle the case where the string cannot be parsed as an integer
                                            binding.recipeView.setText("0");
                                        }
                                    } else {
                                        // Handle unexpected data type
                                        binding.recipeView.setText("0");
                                    }
                                } else {
                                    // Handle the case where the "Recipe" field is missing
                                    binding.recipeView.setText("0");
                                }
                            } else {
                                // Handle the case where the document does not exist
                                binding.recipeView.setText("0");
                            }
                        } else {
                            // Handle errors
                            binding.recipeView.setText("0");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AppContext.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }).start();
        binding.chefName.setText(item.AuthName);
    }
}
