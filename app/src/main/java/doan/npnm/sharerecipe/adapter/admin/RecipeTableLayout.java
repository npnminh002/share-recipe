package doan.npnm.sharerecipe.adapter.admin;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.widget.TableLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.base.BaseTableAdapter;
import doan.npnm.sharerecipe.databinding.RowAdminRecipeViewBinding;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.model.recipe.RecipeStatus;

public class RecipeTableLayout extends BaseTableAdapter<Recipe, RowAdminRecipeViewBinding> {

private final OnEventSelect eventSelect;
    public RecipeTableLayout(TableLayout tableLayout, OnEventSelect eventSelect) {
        super(tableLayout);
        this.eventSelect = eventSelect;
    }

    @Override
    protected RowAdminRecipeViewBinding initLayout() {
        return RowAdminRecipeViewBinding.inflate(LayoutInflater.from(AppContext.getContext()));
    }

    private static String formatDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBind(RowAdminRecipeViewBinding binding, Recipe item, int pos) {
        binding.txtStt.setText("" + pos);
        binding.txtId.setText(item.Id);
        binding.txtName.setText(item.Name);
        binding.txtUserId.setText(item.RecipeAuth);
        binding.txtIngrident.setText(item.Ingredients.size() + " " + AppContext.getContext().getString(R.string.ingredients).toLowerCase());
        binding.txtDirection.setText(item.Directions.size() + " " + AppContext.getContext().getString(R.string.directions).toLowerCase());
        binding.txtPrepareTime.setText(item.PrepareTime.Time + "" + item.PrepareTime.TimeType);
        binding.txtCookTime.setText(item.PrepareTime.Time + "" + item.PrepareTime.TimeType);
        binding.txtLever.setText(item.Level);
        binding.txtView.setText("" + item.View);
        binding.txtLike.setText("" + item.Love);
        binding.txtShare.setText("" + item.Share);
        binding.txtTimeCreate.setText(formatDate(Long.parseLong(item.TimeInit)));
        binding.txtStatus.setText(getSatuts(item.RecipeStatus));
        binding.btnManager.setOnClickListener(v->eventSelect.onManager(item));
        binding.btnView.setOnClickListener(v->eventSelect.onView(item));
        binding.getRoot().setOnClickListener(v->eventSelect.onView(item));

    }

    public interface OnEventSelect{
        void onView(Recipe recipe);
        void onManager(Recipe recipe);
    }

    private String getSatuts(RecipeStatus stt) {
        switch (stt) {
            case WAS_REPORT:
                return AppContext.getContext().getString(R.string.report);
            case PREVIEW:
                return AppContext.getContext().getString(R.string.view);
            case LOCKED:
                return AppContext.getContext().getString(R.string.locked);
            case WAIT_CONFIRM:
                return AppContext.getContext().getString(R.string.wait_cf);
            default:
                return "";
        }

    }

}
