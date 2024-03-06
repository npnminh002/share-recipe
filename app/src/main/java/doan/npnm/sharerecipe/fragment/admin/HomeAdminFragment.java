package doan.npnm.sharerecipe.fragment.admin;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.admin.MenuItemAdapter;
import doan.npnm.sharerecipe.adapter.admin.RecipeTableLayout;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.dialog.BottomManagerRecipe;
import doan.npnm.sharerecipe.databinding.FragmentAdminHomeBinding;
import doan.npnm.sharerecipe.model.recipe.Recipe;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HomeAdminFragment extends BaseFragment<FragmentAdminHomeBinding> {
    public AdminViewModel viewModel;
    public HomeAdminFragment(AdminViewModel userViewModel) {
        this.viewModel = userViewModel;
    }
    @Override
    protected FragmentAdminHomeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminHomeBinding.inflate(getLayoutInflater());
    }
    ArrayList<String> listMenuItem = new ArrayList<>();
    @Override
    protected void initView() {

        viewModel.recipeLiveData.observe(this, data -> {
            binding.txtCountRecipe.setText(""+data.size());
            new RecipeTableLayout(binding.tableLayout, new RecipeTableLayout.OnEventSelect() {
                @Override
                public void onView(Recipe recipe) {
                    replaceFullViewFragment(new DetailAdminRecipeFragment(recipe,viewModel),android.R.id.content,true);

                }

                @Override
                public void onManager(Recipe recipe) {
                    new BottomManagerRecipe(requireActivity()).show();
                }
            }).onFinih(() -> {
                binding.progressLoad.setVisibility(View.GONE);
            }).setData(data);
        });


        viewModel.recipeApproveLiveData.observe(this, data -> {
            binding.txtAppove.setText("" + data.size());
        });
        viewModel.usersLiveData.observe(this, data -> {
            binding.txtUser.setText("" + data.size());
        });
        viewModel.recipeReportLiveData.observe(this, data -> {
            binding.txtReport.setText("" + data.size());
        });

        viewModel.categoryMutableLiveData.observe(this,data->{
            binding.txtCategory.setText(""+data.size());
        });
        // binding.rcvListPreview.setAdapter(adapter);


    }


    @Override
    public void OnClick() {

    }

}