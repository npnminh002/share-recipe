package doan.npnm.sharerecipe.fragment.user;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import doan.npnm.sharerecipe.adapter.users.CategoryAdapter;
import doan.npnm.sharerecipe.adapter.users.SearchAdapter;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.Search;
import doan.npnm.sharerecipe.databinding.FragmentSearchBinding;

public class SeachFragment extends BaseFragment<FragmentSearchBinding> {
    private UserViewModel viewModel;

    public SeachFragment(UserViewModel userViewModel) {
        this.viewModel = userViewModel;
    }

    @Override
    protected FragmentSearchBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSearchBinding.inflate(inflater);
    }

    CategoryAdapter categoryAdapter;
    SearchAdapter searchAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {
        searchAdapter = new SearchAdapter(s -> {

        });
        searchAdapter.setItems((ArrayList<Search>) viewModel.database.searchDao().getListCurrent());

        categoryAdapter = new CategoryAdapter(category -> {

        });

        viewModel.categoriesArr.observe(this, data -> {
            categoryAdapter.setItems(data);
        });

        String key = getData("Key").toString();
        if (key != null) {
            binding.edtSearchData.setText(key);
        }
        binding.rcvManufact.setAdapter(categoryAdapter);
        binding.rcvCurrent.setAdapter(searchAdapter);

    }

    @Override
    public void OnClick() {

    }
}
