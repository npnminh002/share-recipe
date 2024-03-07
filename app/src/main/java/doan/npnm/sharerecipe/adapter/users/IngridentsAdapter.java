package doan.npnm.sharerecipe.adapter.users;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import doan.npnm.sharerecipe.databinding.ItemIngredentsViewAdminBinding;
import doan.npnm.sharerecipe.databinding.ItemIngredentsViewBinding;
import doan.npnm.sharerecipe.databinding.ItemIngredentsViewPreviewBinding;
import doan.npnm.sharerecipe.model.recipe.Ingredients;

public class IngridentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Ingredients> ingridents = new ArrayList<>();
    public OnIngridentEvent event;
    final IGR_TYPE dirType;

    public enum IGR_TYPE {
        EDIT,
        PREVIEW,
        ADMIN
    }

    public IngridentsAdapter(IGR_TYPE dirType, OnIngridentEvent event) {

        this.event = event;
        this.dirType = dirType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return dirType == IGR_TYPE.EDIT ? new IngridentViewholder(ItemIngredentsViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)) :
                dirType == IGR_TYPE.ADMIN ? new AdminIngridentViewholder(ItemIngredentsViewAdminBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)) :
                        new PreviewIngridentViewholder(ItemIngredentsViewPreviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (dirType == IGR_TYPE.EDIT) {
            ((IngridentViewholder) holder).onBind(ingridents.get(position), position);
        } else if (dirType == IGR_TYPE.PREVIEW) {
            ((PreviewIngridentViewholder) holder).onBind(ingridents.get(position));
        } else {
            ((AdminIngridentViewholder) holder).onBind(ingridents.get(position));
        }
    }

    public void setItems(ArrayList<Ingredients> items) {
        this.ingridents = new ArrayList<>();
        ingridents = items;
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

            binding.ingridenName.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    item.Name = binding.ingridenName.getText().toString();
                    event.onNameChange(item);
                }
            });

            binding.icRemoveItem.setOnClickListener(v -> {
                event.onRemove(item, position);
            });
            binding.quantitive.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {

                    item.Quantitative = Float.parseFloat(binding.quantitive.getText().toString());
                    event.onNameChange(item);
                }
            });
        }
    }


    public static class PreviewIngridentViewholder extends RecyclerView.ViewHolder {
        private final ItemIngredentsViewPreviewBinding binding;

        PreviewIngridentViewholder(ItemIngredentsViewPreviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(Ingredients item) {
            binding.ingridenName.setText(item.Name);
            binding.txtNumId.setText("" + item.Id);
            binding.quantitive.setText(item.Quantitative + " g");
        }
    }

    public class AdminIngridentViewholder extends RecyclerView.ViewHolder {
        private final ItemIngredentsViewAdminBinding binding;

        AdminIngridentViewholder(ItemIngredentsViewAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(Ingredients item) {
            binding.ingridenName.setText(item.Name);
            binding.txtNumId.setText("" + item.Id);
            binding.quantitive.setText(item.Quantitative + " g");
        }
    }


    public interface OnIngridentEvent {
        void onNameChange(Ingredients ingredients);

        void onQuantitiveChange(Ingredients ingredients);

        void onRemove(Ingredients id, int pos);
    }
}
