package doan.npnm.sharerecipe.fragment.user;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.adapter.CategoryAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentHomeUserBinding;
import doan.npnm.sharerecipe.model.Category;

public class HomeUserFragment extends BaseFragment<FragmentHomeUserBinding> {

    public AppViewModel homeviewModel;

    public HomeUserFragment(AppViewModel appViewModel) {
        this.homeviewModel = appViewModel;
    }

    @Override
    public void OnClick() {

    }


    @Override
    protected FragmentHomeUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeUserBinding.inflate(getLayoutInflater());
    }

    private CategoryAdapter categoryAdapter;

    @Override
    protected void initView() {


        categoryAdapter = new CategoryAdapter(new CategoryAdapter.OnCategoryEvent() {
            @Override
            public void onSelect(Category category) {

            }
        });
        binding.rcvItemCategory.setAdapter(categoryAdapter);
        categoryAdapter.setItems(homeviewModel.getListCategory());
    }
}