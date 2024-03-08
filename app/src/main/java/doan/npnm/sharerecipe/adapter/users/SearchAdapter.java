package doan.npnm.sharerecipe.adapter.users;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.function.Consumer;

import doan.npnm.sharerecipe.database.models.Search;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemSearchViewBinding;

public class SearchAdapter extends BaseAdapter<Search, ItemSearchViewBinding> {

    private final Consumer<String> stringConsumer;

    public SearchAdapter(Consumer<String> stringConsumer) {
        this.stringConsumer = stringConsumer;
    }

    @Override
    protected ItemSearchViewBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemSearchViewBinding.inflate(inflater,parent,false);
    }

    @Override
    protected void bind(ItemSearchViewBinding binding, Search item, int position) {
        binding.txtView.setText(item.CurrentKey);
        binding.getRoot().setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stringConsumer.accept(item.CurrentKey);
            }
        });
    }
}
