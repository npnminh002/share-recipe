package doan.npnm.sharerecipe.dialog;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;

import doan.npnm.sharerecipe.base.BaseBottomSheet;
import doan.npnm.sharerecipe.databinding.BottomSheetRecipeManagerBinding;

public class BottomManagerRecipe extends BaseBottomSheet<BottomSheetRecipeManagerBinding> {

    public BottomManagerRecipe(FragmentActivity activity) {
        super(activity);
    }

    @Override
    protected BottomSheetRecipeManagerBinding initView(LayoutInflater inflater, ViewGroup container) {
        return BottomSheetRecipeManagerBinding.inflate(inflater,container,false);
    }

    @Override
    protected void onBind(BottomSheetRecipeManagerBinding binding) {

    }


}
