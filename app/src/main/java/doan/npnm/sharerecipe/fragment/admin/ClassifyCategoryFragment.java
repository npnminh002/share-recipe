package doan.npnm.sharerecipe.fragment.admin;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import doan.npnm.sharerecipe.adapter.admin.ClassifyAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentClassifyCateogryBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.interfaces.OnGetEvent;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.model.recipe.RecipeStatus;
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

    public void ongetCategory(OnGetEvent<Category> categoryOnGetEvent) {
        ArrayList<Category> categories = new ArrayList<>();
        firestore.collection(Constant.CATEGORY).get().addOnCompleteListener(task -> {
            for (DocumentSnapshot doc : task.getResult()) {
                categories.add(doc.toObject(Category.class));
            }
            categoryOnGetEvent.onSuccess(categories);
        }).addOnFailureListener(e -> {
            showToast(e.getMessage());
        });
    }


    @Override
    protected void initView() {

        classifyAdapter = new ClassifyAdapter(ClassifyAdapter.ClassifyManager.REMOVE, new ClassifyAdapter.OnEventCategory() {
            @Override
            public void onAdd(Category ct) {

            }

            @Override
            public void onRemove(Category ct) {
                categories.add(ct);
                classifies.remove(ct);
                classifyAdapter.removeItem(ct);
                categoryAdapter.addItem(ct, 0);
            }
        });

        categoryAdapter = new ClassifyAdapter(ClassifyAdapter.ClassifyManager.ADD, new ClassifyAdapter.OnEventCategory() {
            @Override
            public void onAdd(Category ct) {
                classifies.add(ct);
                categories.remove(ct);
                categoryAdapter.removeItem(ct);
                classifyAdapter.addItem(ct, 0);
            }

            @Override
            public void onRemove(Category ct) {

            }
        });

        binding.rcvCategory.setAdapter(categoryAdapter);
        binding.rcvClassify.setAdapter(classifyAdapter);

        ongetCategory(data -> {
            categories = data;
            refreshItem();

        });
    }

    private void refreshItem() {
        categoryAdapter.setItems(categories);
        classifyAdapter.setItems(classifies);
    }

    @Override
    public void OnClick() {
        binding.btnSave.setOnClickListener(v -> {
            new ConfirmDialog(requireContext(), "Do you want save change", () -> {
                ArrayList<String> categoryId = new ArrayList<>();
                for (Category ct : classifies) {
                    categoryId.add(ct.Id);
                }
                HashMap<String, Object> data = new HashMap<>();
                data.put("Category", categoryId);
                data.put("RecipeStatus", RecipeStatus.PREVIEW);
                loaddingDialog.show();
                firestore.collection(Constant.RECIPE).document(recipe.Id).update(data).addOnSuccessListener(unused -> {
                    showToast("Success");
                    viewModel.initRecipeData();
                    new Handler(Looper.myLooper()).postDelayed(() -> {
                        closeFragment(ClassifyCategoryFragment.this);
                        loaddingDialog.dismiss();
                    }, 2000);

                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                    loaddingDialog.dismiss();
                });
            }).show();
        });
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(ClassifyCategoryFragment.this);
        });
    }
}
