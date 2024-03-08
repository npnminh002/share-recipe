package doan.npnm.sharerecipe.fragment.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;

import doan.npnm.sharerecipe.adapter.admin.ClassifyAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentClassifyCateogryBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class ClassifyCategoryFragment extends BaseFragment<FragmentClassifyCateogryBinding> {

    private Recipe recipe;
    private AdminViewModel viewModel;

    private ClassifyAdapter classifyAdapter;
    private ClassifyAdapter categoryAdapter;

    private ArrayList<Category> categories = new ArrayList<>();
    private ArrayList<Category> classifies = new ArrayList<>();

    public ClassifyCategoryFragment(Recipe recipe, AdminViewModel viewModel) {
        this.recipe = recipe;
        this.viewModel = viewModel;
    }

    @Override
    protected FragmentClassifyCateogryBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentClassifyCateogryBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        viewModel.categoryMutableLiveData.observe(this, data -> {
            categories = data;
        });


        classifyAdapter = new ClassifyAdapter(ClassifyAdapter.ClassifyManager.ADD, new ClassifyAdapter.OnEventCategory() {
            @Override
            public void onAdd(Category ct) {

            }

            @Override
            public void onRemove(Category category) {
                categories.add(category);
                classifies.remove(category);
                refreshItem();

            }
        });

        categoryAdapter = new ClassifyAdapter(ClassifyAdapter.ClassifyManager.REMOVE, new ClassifyAdapter.OnEventCategory() {
            @Override
            public void onAdd(Category ct) {
                classifies.add(ct);
                categories.remove(ct);
                refreshItem();

            }

            @Override
            public void onRemove(Category category) {

            }
        });

        binding.rcvCategory.setAdapter(categoryAdapter);
        binding.rcvClassify.setAdapter(classifyAdapter);
        refreshItem();


    }

    private void refreshItem() {
        categoryAdapter.setItems(categories);
        classifyAdapter.setItems(classifies);
    }

    @Override
    public void OnClick() {
        binding.btnSave.setOnClickListener(v -> {
            new ConfirmDialog(requireContext(), "Do you want save change", () -> {
                ArrayList<String> categoryId= new ArrayList<>();
                for (Category ct:classifies){
                    categoryId.add(ct.Id);
                }
                HashMap<String,Object> data= new HashMap<>();
                data.put("Category",classifies);
                data.put("RecipeStatus","Preview");
                firestore.collection(Constant.RECIPE)
                        .document(recipe.Id)
                        .update(data)
                        .addOnSuccessListener(unused -> {
                            showToast("Success");
                            closeFragment(ClassifyCategoryFragment.this);
                        })
                        .addOnFailureListener(e -> {
                            showToast(e.getMessage());
                        });
            }).show();
        });
    }
}
