package doan.npnm.sharerecipe.fragment.user;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Locale;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.RecipeAdapter;
import doan.npnm.sharerecipe.adapter.users.SearchAdapter;
import doan.npnm.sharerecipe.adapter.users.SearchCategoryAdapter;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.Search;
import doan.npnm.sharerecipe.databinding.FragmentSearchBinding;
import doan.npnm.sharerecipe.interfaces.OnGetEvent;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class SeachFragment extends BaseFragment<FragmentSearchBinding> {
    private UserViewModel viewModel;

    public SeachFragment(UserViewModel userViewModel) {
        this.viewModel = userViewModel;
    }

    @Override
    protected FragmentSearchBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSearchBinding.inflate(inflater);
    }

    SearchCategoryAdapter categoryAdapter;
    SearchAdapter searchAdapter;
    RecipeAdapter recipeAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {
        searchAdapter = new SearchAdapter(s -> {
            binding.edtSearchData.setText(s);
            searchValue(s, this::setToView);
        });
        searchAdapter.setItems((ArrayList<Search>) viewModel.database.searchDao().getListCurrent());

        categoryAdapter = new SearchCategoryAdapter(category -> {
            searchValue(category.Id, this::setToView);
            binding.edtSearchData.setText(category.Name);
        });

        viewModel.categoriesArr.observe(this, data -> {
            categoryAdapter.setItems(data);
        });

        String key = getData("Key").toString();
        if (key.length() != 0) {
            showToast(key);
            Category ct = checkIDCategory(key);
            if (ct != null) {
                binding.edtSearchData.setText(ct.Name);
            } else {
                binding.edtSearchData.setText(key);
            }
            searchValue(key, this::setToView);
        }
        binding.rcvManufact.setAdapter(categoryAdapter);
        binding.rcvCurrent.setAdapter(searchAdapter);
        recipeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeEvent() {
            @Override
            public void onView(Recipe rcp) {
                if (viewModel.database.recentViewDao().checkExistence(rcp.Id)) {
                    viewModel.database.recentViewDao().removeRecent(rcp.Id);
                }
                viewModel.database.recentViewDao().addRecentView(new RecentView() {{
                    AuthID = rcp.RecipeAuth;
                    RecipeID = rcp.Id;
                    ViewTime = getTimeNow();
                    Recipe = rcp.toJson();
                }});

                addFragment(new DetailRecipeFragment(rcp, viewModel), android.R.id.content, true);
            }

            @Override
            public void onLove(Recipe rcp, boolean isLove) {
                if (viewModel.auth.getCurrentUser() == null) {
                    showToast(getString(R.string.no_us));
                } else {
                    if (isLove) {
                        viewModel.onLoveRecipe(rcp);
                    } else {
                        viewModel.onUnlove(rcp);
                    }
                }
            }
        }, viewModel.database);
        binding.rcvResultSreach.setAdapter(recipeAdapter);

    }

    private Category checkIDCategory(String key) {
        ArrayList<Category> categories = viewModel.categoriesArr.getValue();
        for (Category ct : categories) {
            if (ct.Id.equals(key)) {
                return ct;
            }
        }
        return null;
    }

    private Category checkCategory(String key) {
        ArrayList<Category> categories = viewModel.categoriesArr.getValue();
        for (Category ct : categories) {
            if (ct.Name.equals(key)) {
                return ct;
            }
        }
        return null;

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(requireContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    binding.edtSearchData.setText(result.get(0));
                }
                break;
            }
        }
    }

    @Override
    public void OnClick() {
        binding.icSearch.setOnClickListener(v -> {
            searchValue(binding.edtSearchData.getText().toString(), this::setToView);
        });

        binding.icBack.setOnClickListener(v -> {
            searchAdapter.setItems((ArrayList<Search>) viewModel.database.searchDao().getListCurrent());
            binding.llResult.setVisibility(View.GONE);

        });
        binding.icInputSpeech.setOnClickListener(v -> {
            promptSpeechInput();
        });
    }

    private void setToView(ArrayList<Recipe> data) {
        String key = binding.edtSearchData.toString();
        Category ct = checkCategory(key);
        if (ct != null) {
            viewModel.database.searchDao().addRecentView(new Search() {{
                CurrentKey = ct.Name;
            }});
        } else {
            viewModel.database.searchDao().addRecentView(new Search() {{
                CurrentKey = binding.edtSearchData.getText().toString();
            }});
        }
        hideKeyboard();
        binding.llResult.setVisibility(View.VISIBLE);
        binding.txtResult.setText(binding.edtSearchData.getText().toString());
        if (data == null || data.size() == 0) {
            binding.llNoResult.setVisibility(View.VISIBLE);
            binding.rcvResultSreach.setVisibility(View.GONE);
        } else {
            binding.rcvResultSreach.setVisibility(View.VISIBLE);
            recipeAdapter.setItems(data);
            binding.llNoResult.setVisibility(View.GONE);
        }
    }

    private void searchValue(String string, OnGetEvent<Recipe> event) {
        loaddingDialog.show();
        if (string.isEmpty()) {
            event.onSuccess(null);
        } else {
            ArrayList<Recipe> arrayList = new ArrayList<>();
            viewModel.firestore.collection(Constant.RECIPE)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            if (recipe.Name.toLowerCase().replace(" ", "").contains(string.toLowerCase().replace(" ", "")) ||
                                    checkContaintValue(string, recipe.Category)) {
                                arrayList.add(recipe);
                            }
                        }
                        event.onSuccess(arrayList);
                        loaddingDialog.dismiss();

                    }).addOnFailureListener(exception -> {
                        loaddingDialog.dismiss();
                        event.onSuccess(null);
                    });
        }
    }

    private boolean checkContaintValue(String string, ArrayList<String> category) {
        for (String s : category) {
            if (string.contains(s)) {

                return true;
            }
        }
        return false;
    }
}















