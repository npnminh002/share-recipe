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

    // Constructor nhận một viewModel của Admin
    public AccountAdminFragment(AdminViewModel userViewModel) {
        this.viewModel = userViewModel;
    }

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentAdminAccountBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminAccountBinding.inflate(inflater);
    }

    // Khởi tạo giao diện
    AccountAdapter accountAdapter;
    @Override
    protected void initView() {
        // Khởi tạo Adapter cho RecyclerView của tài khoản
        accountAdapter= new AccountAdapter(new AccountAdapter.AccountEvent() {
            @Override
            public void onDetail(Users us) {
                // Mở fragment chi tiết của tài khoản khi nhấn vào
                replaceFullViewFragment(new DetailAuthAdminFragment(viewModel,us),android.R.id.content,true);
            }

            @Override
            public void onSelect(Users us) {
                // Xử lý sự kiện chọn tài khoản
            }
        });
        // Quan sát LiveData để cập nhật dữ liệu
        viewModel.usersLiveData.observe(this,data->{
            accountAdapter.setItems(data);
        });
        // Thiết lập Adapter cho RecyclerView
        binding.rcvAccount.setAdapter(accountAdapter);
    }

    @Override
    public void OnClick() {
        // Xử lý sự kiện click
    }
}
