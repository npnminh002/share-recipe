package doan.npnm.sharerecipe.fragment.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.adapter.admin.AccountAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminAccountBinding;
import doan.npnm.sharerecipe.model.Users;

public class AccountAdminFragment extends BaseFragment<FragmentAdminAccountBinding> {
    private AdminViewModel viewModel;

    public AccountAdminFragment(AdminViewModel userViewModel) {
        this.viewModel= userViewModel;
    }

    @Override
    protected FragmentAdminAccountBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminAccountBinding.inflate(inflater);
    }

    AccountAdapter accountAdapter;

    @Override
    protected void initView() {
        accountAdapter= new AccountAdapter(new AccountAdapter.AccountEvent() {
            @Override
            public void onDetail(Users us) {
                replaceFullViewFragment(new DetailAuthAdminFragment(viewModel,us),android.R.id.content,true);
            }

            @Override
            public void onSelect(Users us) {

            }
        });
        viewModel.usersLiveData.observe(this,data->{
            accountAdapter.setItems(data);
        });
        binding.rcvAccount.setAdapter(accountAdapter);
    }

    @Override
    public void OnClick() {

    }
}
