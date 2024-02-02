package doan.npnm.sharerecipe.fragment.user;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.adapter.DirectionsAdapter;
import doan.npnm.sharerecipe.adapter.ImageRecipeAdapter;
import doan.npnm.sharerecipe.adapter.IngridentsAdapter;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentDetailRecipeBinding;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class DetailRecipeFragment extends BaseFragment<FragmentDetailRecipeBinding> {
    private Recipe data;

    public DetailRecipeFragment(Recipe rcp) {
        super();
        this.data = rcp;
    }

    @Override
    protected FragmentDetailRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDetailRecipeBinding.inflate(inflater);
    }

    private DirectionsAdapter directionsAdapter;

    private IngridentsAdapter ingridentsAdapter;

    private ImageRecipeAdapter adapter;

    @Override
    protected void initView() {
        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.PREVIEW, null);
        binding.rcvDirection.setAdapter(directionsAdapter);
        adapter = new ImageRecipeAdapter(null);

        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.PREVIEW, null);
        binding.rcvIngrident.setAdapter(ingridentsAdapter);
        binding.rcvGallery.setAdapter(adapter);

        loadImage(data.ImgUrl, binding.imgProduct);

        binding.nameOfRecipe.setText(data.Name == null ? "" : data.Name);
        binding.description.setText(data.Description == null ? "" : data.Description);
        binding.timePrepare.setText(data.PrepareTime.Time);
        binding.selectMinutePP.setText(data.PrepareTime.TimeType);
        binding.timeCook.setText(data.CookTime.Time);
        binding.selectMinutePP.setText(data.CookTime.TimeType);
        binding.txtLever.setText(data.Level);

        directionsAdapter.setItems(data.Directions);
        ingridentsAdapter.setItems(data.Ingredients);
        adapter.setItems(data);

    }

    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> closeFragment(DetailRecipeFragment.this));
    }
}