package doan.npnm.sharerecipe.fragment.user;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.DirectionsAdapter;
import doan.npnm.sharerecipe.adapter.ImageRecipeAdapter;
import doan.npnm.sharerecipe.adapter.ImageStringAdapter;
import doan.npnm.sharerecipe.adapter.IngridentsAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentDetailRecipeBinding;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class DetailRecipeFragment extends BaseFragment<FragmentDetailRecipeBinding> {
    private Recipe data;
    private AppViewModel viewModel;

    public DetailRecipeFragment(Recipe rcp,AppViewModel vm) {
        super();
        this.viewModel= vm;
        this.data = rcp;
    }

    @Override
    protected FragmentDetailRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDetailRecipeBinding.inflate(inflater);
    }

    private DirectionsAdapter directionsAdapter;

    private IngridentsAdapter ingridentsAdapter;

    private ImageStringAdapter adapter;

    @Override
    protected void initView() {
        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.PREVIEW, null);
        binding.rcvDirection.setAdapter(directionsAdapter);
        adapter = new ImageStringAdapter(url -> {

        });

        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.PREVIEW, null);
        binding.rcvIngrident.setAdapter(ingridentsAdapter);
        binding.rcvGallery.setAdapter(adapter);

        loadImage(data.ImgUrl, binding.imgProduct);
        new Thread(()->{
            viewModel.getDataFromUser(data.RecipeAuth.AuthId, new FetchByID<Users>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(Users data) {
                    binding.chefName.setText(data.UserName);
                    binding.recipeCount.setText(data.Recipe+" "+getString(R.string.recipe));
                    binding.circleImageView.loadImage(data.UrlImg);
                }

                @Override
                public void onErr(Object err) {

                }
            });
        }).start();


        binding.foodName.setText(data.Name == null ? "" : data.Name);
        directionsAdapter.setItems(data.Directions);
        ingridentsAdapter.setItems(data.Ingredients);
        adapter.setItems(data.ImagePreview);

    }

    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> closeFragment(DetailRecipeFragment.this));
    }
}