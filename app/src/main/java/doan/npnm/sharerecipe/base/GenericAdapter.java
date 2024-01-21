package doan.npnm.sharerecipe.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.List;

public class GenericAdapter<T, VB extends ViewBinding> extends RecyclerView.Adapter<GenericAdapter<T, VB>.GenericViewHolder> {

    private final List<T> itemList;
    private final BindingInflater<VB> bindingInflater;
    private final Binder<VB, T> binder;
    private int position = RecyclerView.NO_POSITION;
    public GenericAdapter(List<T> itemList, BindingInflater<VB> bindingInflater, Binder<VB, T> binder) {
        this.itemList = itemList;
        this.bindingInflater = bindingInflater;
        this.binder = binder;
    }

    public class GenericViewHolder extends RecyclerView.ViewHolder {
        public final VB binding;

        public GenericViewHolder(VB binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        VB binding = bindingInflater.inflate(inflater, parent, false);
        return new GenericViewHolder(binding);
    }
    public void setItem(int position) {
        notifyItemChanged(this.position);
        this.position = position;
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onBindViewHolder(GenericViewHolder holder, int position) {
        T item = itemList.get(position);
        binder.bind(holder.binding, item, position);
    }

    public interface BindingInflater<VB extends ViewBinding> {
        VB inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent);
    }

    public interface Binder<VB extends ViewBinding, T> {
        void bind(VB binding, T item, int position);
    }
}