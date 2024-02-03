package doan.npnm.sharerecipe.adapter;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import doan.npnm.sharerecipe.databinding.ItemIngredentsViewBinding;
import doan.npnm.sharerecipe.databinding.ItemIngredentsViewPreviewBinding;
import doan.npnm.sharerecipe.model.recipe.Ingredients;

public class IngridentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final ArrayList<Ingredients> ingridents = new ArrayList<>();
    public OnIngridentEvent event;
    final IGR_TYPE dirType;

    public enum IGR_TYPE {
        EDIT,
        PREVIEW
    }

    public IngridentsAdapter(IGR_TYPE dirType, OnIngridentEvent event) {

        this.event = event;
        this.dirType = dirType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return dirType == IGR_TYPE.EDIT ? new IngridentViewholder(ItemIngredentsViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false))
                : new PreviewIngridentViewholder(ItemIngredentsViewPreviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (dirType == IGR_TYPE.EDIT) {
            ((IngridentViewholder) holder).onBind(ingridents.get(position), position);
        } else {
            ((PreviewIngridentViewholder) holder).onBind(ingridents.get(position));
        }
    }

    public void setItems(ArrayList<Ingredients> items) {
        ingridents.clear();
        ingridents.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ingridents.size();
    }


    public class IngridentViewholder extends RecyclerView.ViewHolder {
        private final ItemIngredentsViewBinding binding;

        IngridentViewholder(ItemIngredentsViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(Ingredients item, int position) {
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
    }


    public class PreviewIngridentViewholder extends RecyclerView.ViewHolder {
        private final ItemIngredentsViewPreviewBinding binding;

        PreviewIngridentViewholder(ItemIngredentsViewPreviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(Ingredients item) {
            binding.ingridenName.setText(item.Name);
            binding.txtNumId.setText(""+item.Id);
            binding.quantitive.setText(item.Quantitative+" g");
        }
    }


    public interface OnIngridentEvent {
        void onNameChange(Ingredients ingredients, String value, int postion);

        void onQuantitiveChange(Ingredients ingredients, String value, int postion);

        void onRemove(Ingredients id, int pos);
    }
}
