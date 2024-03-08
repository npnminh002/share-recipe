package doan.npnm.sharerecipe.fragment.admin;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import doan.npnm.sharerecipe.adapter.admin.RecipeTableLayout;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.dialog.BottomManagerRecipe;
import doan.npnm.sharerecipe.databinding.FragmentAdminHomeBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.dialog.DeleteDialog;
import doan.npnm.sharerecipe.fragment.user.DetailAuthFragment;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

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
            new RecipeTableLayout(binding.tableLayout,requireContext() ,new RecipeTableLayout.OnEventSelect() {
                @Override
                public void onView(Recipe recipe) {
                    replaceFullViewFragment(new DetailAdminRecipeFragment(recipe,viewModel),android.R.id.content,true);

                }

                @Override
                public void onManager(Recipe recipe) {
                    new BottomManagerRecipe(recipe, new BottomManagerRecipe.OnBottomSheetEvent() {
                        @Override
                        public void onDetail(Recipe recipe) {
                            replaceFullViewFragment(new DetailAdminRecipeFragment(recipe,viewModel),android.R.id.content,true);
                        }

                        @Override
                        public void onAuth(String authID) {

                        }

                        @Override
                        public void onApprove(Recipe recipe) {
                            new ConfirmDialog(requireContext(), "Do you want apporve this recipe?", () -> {
                                firestore.collection(Constant.RECIPE)
                                        .document(recipe.Id)
                                        .update("RecipeStatus","PREVIEW")
                                        .addOnSuccessListener(unused -> {
                                            showToast("Success");
                                        });
                            }).show();
                        }

                        @Override
                        public void onDelete(Recipe rcp) {
                            new DeleteDialog(requireContext(), () -> {
                                firestore.collection(Constant.RECIPE)
                                        .document(recipe.Id)
                                        .delete()
                                        .addOnCompleteListener(task -> {
                                            showToast("Delete successfully");
                                        }).addOnFailureListener(e -> {
                                            showToast("Error: "+e.getMessage());
                                        });
                            }).show();
                        }

                        @Override
                        public void onClassify(Recipe recipe) {
                            replaceFullViewFragment(new ClassifyCategoryFragment(recipe,viewModel),android.R.id.content,true);
                        }

                        @Override
                        public void onLocked(Recipe recipe) {
                            new ConfirmDialog(requireContext(),"Do you want lock this recipe", () -> {
                                firestore.collection(Constant.RECIPE)
                                        .document(recipe.Id)
                                        .update("RecipeStatus","LOCKED")
                                        .addOnSuccessListener(unused -> {
                                            showToast("Success");
                                        });
                            }).show();
                        }
                    }, requireActivity()).show();
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

    }


    @Override
    public void OnClick() {

    }

}