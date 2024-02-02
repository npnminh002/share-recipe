package doan.npnm.sharerecipe.interfaces;

import doan.npnm.sharerecipe.model.recipe.Recipe;

public interface OnRecipeEvent {
    void onView(Recipe rcp);

    void onSave(Recipe recipe);
}
