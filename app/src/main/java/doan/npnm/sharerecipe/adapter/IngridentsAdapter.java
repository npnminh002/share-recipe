package doan.npnm.sharerecipe.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemIngredentsViewBinding;
import doan.npnm.sharerecipe.model.recipe.Ingredients;

public class IngridentsAdapter extends BaseAdapter<Ingredients, ItemIngredentsViewBinding> {
    final OnIngridetEvent event;

    public IngridentsAdapter(OnIngridetEvent event) {
        this.event = event;
    }

    @Override
    protected ItemIngredentsViewBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemIngredentsViewBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void bind(ItemIngredentsViewBinding binding, Ingredients item, int position) {
        binding.ingridenName.setText(item.Name);
        binding.quantitive.setText("" + item.Quantitative);
        binding.ingridenName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                event.onNameChange(item, s.toString(), position);
            }
        });

        binding.icRemoveItem.setOnClickListener(v -> {
            event.onRemove(item, position);
        });
        binding.quantitive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                event.onQuantitiveChange(item, s.toString(), position);
            }
        });
    }

    public interface OnIngridetEvent {
        void onNameChange(Ingredients ingredients, String value, int postion);

        void onQuantitiveChange(Ingredients ingredients, String value, int postion);

        void onRemove(Ingredients id, int pos);
    }
}
