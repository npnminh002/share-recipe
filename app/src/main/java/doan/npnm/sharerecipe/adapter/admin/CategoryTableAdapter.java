package doan.npnm.sharerecipe.adapter.admin;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.TableLayout;

import doan.npnm.sharerecipe.base.BaseTableAdapter;
import doan.npnm.sharerecipe.databinding.RowCategoryAdminBinding;
import doan.npnm.sharerecipe.model.Category;

public class CategoryTableAdapter extends BaseTableAdapter<Category, RowCategoryAdminBinding> {
    private OnCategoryEvent categoryConsumer;

    public interface OnCategoryEvent {
        void onSelect(Category category);
    }

    public CategoryTableAdapter(TableLayout tableLayout, Context context, OnCategoryEvent categoryConsumer) {
        super(tableLayout, context);
        this.categoryConsumer = categoryConsumer;
    }

    @Override
    protected RowCategoryAdminBinding initLayout(Context context) {
        return RowCategoryAdminBinding.inflate(LayoutInflater.from(context));
    }

    @Override
    protected void onBind(RowCategoryAdminBinding binding, Category item, int position) {
        binding.txtImgSrc.setText("" + item.Image);
        binding.txtId.setText("" + item.Id);
        binding.txtName.setText(item.Name);
        binding.getRoot().setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                categoryConsumer.onSelect(item);
            }
        });
    }

}
