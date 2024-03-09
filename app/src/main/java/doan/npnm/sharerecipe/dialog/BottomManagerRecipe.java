package doan.npnm.sharerecipe.dialog;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;

import doan.npnm.sharerecipe.base.BaseBottomSheet;
import doan.npnm.sharerecipe.databinding.BottomSheetRecipeManagerBinding;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class BottomManagerRecipe extends BaseBottomSheet<BottomSheetRecipeManagerBinding> {
    private Recipe recipe;
    private OnBottomSheetEvent event;

    public BottomManagerRecipe(Recipe recipe, OnBottomSheetEvent event, FragmentActivity activity) {
        super(activity);
        this.recipe = recipe;
        this.event = event;
    }

    @Override
    protected BottomSheetRecipeManagerBinding initView(LayoutInflater inflater, ViewGroup container) {
        return BottomSheetRecipeManagerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void onBind(BottomSheetRecipeManagerBinding binding) {
        binding.btnAppove.setOnClickListener(v -> {
            event.onApprove(recipe);
            this.dismiss();
        });
        binding.btnDelete.setOnClickListener(v -> {
            event.onDelete(recipe);
            this.dismiss();
        });
        binding.btnDetail.setOnClickListener(v -> {
            event.onDetail(recipe);
            this.dismiss();
        });
        binding.btnAuth.setOnClickListener(v -> {
            event.onAuth(recipe.RecipeAuth);
            this.dismiss();
        });
        binding.btnLoked.setOnClickListener(v -> {
            event.onLocked(recipe);
            this.dismiss();
        });
        binding.btnClassify.setOnClickListener(v -> {
            event.onClassify(recipe);
            this.dismiss();
        });
    }

    public interface OnBottomSheetEvent {
        void onDetail(Recipe recipe);

        void onAuth(String authID);

        void onApprove(Recipe recipe);

        void onDelete(Recipe rcp);

        void onClassify(Recipe recipe);

        void onLocked(Recipe recipe);
    }


}
