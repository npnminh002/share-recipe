package doan.npnm.sharerecipe.fragment.admin;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.admin.MenuItemAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminHomeBinding;
import doan.npnm.sharerecipe.fragment.admin.home.FragmentAdminRecipeHome;

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
    private MenuItemAdapter menuItemAdapter;
    @Override
    protected void initView() {
        listMenuItem = new ArrayList<String>() {{
            add(getString(R.string.recipe));
            add(getString(R.string.approved));
            add(getString(R.string.account));
            add(getString(R.string.report));
            add(getString(R.string.category));
        }};
        menuItemAdapter = new MenuItemAdapter(item -> {
            if(item.equals(getString(R.string.recipe))){
                addFragment(new FragmentAdminRecipeHome(viewModel),R.id.llFrameView,true);
            }
            else if(item.equals(getString(R.string.approved))){

            }
        });

        binding.rcMenuItem.setAdapter(menuItemAdapter);
        menuItemAdapter.setItems(listMenuItem);
        menuItemAdapter.currentPosition = 0;
        viewModel.recipeApproveLiveData.observe(this, data -> {
            binding.txtAppove.setText("" + data.size());
        });
        viewModel.usersLiveData.observe(this, data -> {
            binding.txtUser.setText("" + data.size());
        });
        viewModel.recipeReportLiveData.observe(this, data -> {
            binding.txtReport.setText("" + data.size());
        });

        // binding.rcvListPreview.setAdapter(adapter);


    }


    @Override
    public void OnClick() {

    }

}