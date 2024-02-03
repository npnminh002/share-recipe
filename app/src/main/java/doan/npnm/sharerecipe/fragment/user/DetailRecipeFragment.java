package doan.npnm.sharerecipe.fragment.user;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.DirectionsAdapter;
import doan.npnm.sharerecipe.adapter.ImageStringAdapter;
import doan.npnm.sharerecipe.adapter.IngridentsAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentDetailRecipeBinding;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.disscus.Discussion;
import doan.npnm.sharerecipe.model.disscus.DisscusAuth;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class DetailRecipeFragment extends BaseFragment<FragmentDetailRecipeBinding> {
    private Recipe data;
    private AppViewModel viewModel;

    public DetailRecipeFragment(Recipe rcp, AppViewModel vm) {
        super();
        this.viewModel = vm;
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
        new Thread(this::listenerDiscussion).start();

        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.PREVIEW, null);
        binding.rcvDirection.setAdapter(directionsAdapter);
        adapter = new ImageStringAdapter(url -> {

        });

        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.PREVIEW, null);
        binding.rcvIngrident.setAdapter(ingridentsAdapter);
        binding.rcvGallery.setAdapter(adapter);

        loadImage(data.ImgUrl, binding.imgProduct);
        new Thread(() -> {
            viewModel.getDataFromUser(data.RecipeAuth.AuthId, new FetchByID<Users>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(Users data) {
                    binding.chefName.setText(data.UserName);
                    binding.recipeCount.setText(data.Recipe + " " + getString(R.string.recipe));
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

    private ArrayList<Discussion> discussions= new ArrayList<>();
    private void listenerDiscussion() {
        viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION)
                .child(data.Id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Check if the snapshot exists and has data
                        if (snapshot.exists()) {
                            ArrayList<Discussion> discussions = new ArrayList<>();
                            // Iterate through each child snapshot and convert it to Discussion object
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                Discussion dcs = childSnapshot.getValue(Discussion.class);
                                discussions.add(dcs);
                            }
                            // Now you have the updated array of discussions
                            // You can use 'discussions' list as needed

                            StringBuilder discussionsText = new StringBuilder();
                            for (Discussion dcs : discussions) {
                                discussionsText.append(dcs.toJson()).append("\n");
                            }
                            binding.txtDiscuss.setText(discussionsText.toString());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });

    }


    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> closeFragment(DetailRecipeFragment.this));
        binding.icSendDiscuss.setOnClickListener(v -> sendDisscuss());
    }


    private void sendDisscuss() {
        Users us = viewModel.getUsers().getValue();
        String message = binding.txtDiscuss.getText().toString();
        String id = viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION).
                child(data.Id).push().getKey();
        Discussion discussion = new Discussion() {{
            Id = id;
            DisscusAuth = new DisscusAuth() {{
                AuthId = us.UserID;
                AuthName = us.UserName;
                Address = us.Address;
                Image = us.UrlImg;

            }};
            Time = getTimeNow();
            Content = message;
            DiscussIcon = doan.npnm.sharerecipe.model.disscus.DiscussIcon.NONE;
            DiscussType = doan.npnm.sharerecipe.model.disscus.DiscussType.DISSCUS;
            DiscussionArray=null;
        }};
        viewModel.fbDatabase.getReference(Constant.KEY_DICUSSION)
                .child(data.Id).child(id)
                .setValue(discussion)
                .addOnSuccessListener(unused -> {
                    showToast("Success");
                    binding.txtDiscuss.setText("");
                })
                .addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });

    }
}