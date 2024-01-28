package doan.npnm.sharerecipe.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemDirectionViewBinding;
import doan.npnm.sharerecipe.databinding.ItemIngredentsViewBinding;
import doan.npnm.sharerecipe.model.recipe.Directions;
import doan.npnm.sharerecipe.model.recipe.Ingredients;

public class DirectionsAdapter extends BaseAdapter<Directions, ItemDirectionViewBinding> {
    final OnDirectionEvent event;

    public DirectionsAdapter(OnDirectionEvent event) {
        this.event = event;
    }

    @Override
    protected ItemDirectionViewBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemDirectionViewBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void bind(ItemDirectionViewBinding binding, Directions item, int position) {
        binding.ingridenName.setText(item.Name);
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
    }

    public interface OnDirectionEvent {
        void onNameChange(Directions directions, String value, int postion);
        void onRemove(Directions id, int pos);
    }
}
