package doan.npnm.sharerecipe.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public abstract class BaseAdapter<T, VB extends ViewBinding> extends RecyclerView.Adapter<BaseAdapter<T, VB>.ViewHolder> {

    private final ArrayList<T> listItem = new ArrayList<>();
    private VB binding;

    private int currentPosition = RecyclerView.NO_POSITION;

    protected abstract VB createBinding(LayoutInflater inflater, ViewGroup parent, int viewType);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = createBinding(inflater, parent, viewType);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        T item = listItem.get(position);
        bind(holder.binding, item, position);
    }

    public String formatToCurrency(float value) {
        Locale locale = new Locale("vi", "VN"); // Set the Vietnamese locale
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        return currencyFormat.format(value);
    }
    @Override
    public int getItemCount() {
        return listItem.size();
    }

    protected abstract void bind(VB binding, T item, int position);

    public void setItems(ArrayList<T> items) {
        listItem.clear();
        listItem.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0) {
            listItem.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addItems(ArrayList<T> items) {
        listItem.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(T item, int index) {
        listItem.add(index, item);
        notifyItemInserted(index);
    }

    public void removeItem(T item) {
        int index = listItem.indexOf(item);
        if (index >= 0) {
            listItem.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void changeItemWithPos(int index, T newItem) {
        if (index >= 0 && index < listItem.size()) {
            listItem.set(index, newItem);
            notifyItemChanged(index);
        }
    }

    public ArrayList<T> getListItem() {
        return new ArrayList<>(listItem);
    }

    public void setCurrentPos(int position) {
        if (currentPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(currentPosition);
        }
        currentPosition = position;
        notifyItemChanged(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        VB binding;

        ViewHolder(VB binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}


