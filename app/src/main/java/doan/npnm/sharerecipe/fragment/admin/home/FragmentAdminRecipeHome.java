package doan.npnm.sharerecipe.fragment.admin.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.adapter.admin.RecipeTableLayout;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminHomeRecipeBinding;
import doan.npnm.sharerecipe.dialog.BottomManagerRecipe;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class FragmentAdminRecipeHome extends BaseFragment<FragmentAdminHomeRecipeBinding> {
    private AdminViewModel viewModel;

    public FragmentAdminRecipeHome(AdminViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected FragmentAdminHomeRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminHomeRecipeBinding.inflate(getLayoutInflater());
    }


    @Override
    protected void initView() {
        viewModel.recipeLiveData.observe(this, data -> {
            new RecipeTableLayout(binding.tableLayout, new RecipeTableLayout.OnEventSelect() {
                @Override
                public void onView(Recipe recipe) {
                    new BottomManagerRecipe(requireActivity()).show();
                }

                @Override
                public void onManager(Recipe recipe) {

                }
            }).setData(data);

        });
    }

    @Override
    public void OnClick() {

    }
}
