package doan.npnm.sharerecipe.adapter.users;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.content.res.AppCompatResources;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.app.lang.Language;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemLanguageOpenBinding;


public class LanguageAdapter extends BaseAdapter<Language, ItemLanguageOpenBinding> {

    private final OnLanguageClickListener listener;

    public LanguageAdapter( OnLanguageClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected ItemLanguageOpenBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemLanguageOpenBinding.inflate(inflater,parent,false);
    }

    @Override
    protected void bind(ItemLanguageOpenBinding binding, Language item, int position) {
        binding.txtNameLanguage.setText(item.getTitle());
        binding.imgIconLanguage.setImageDrawable(AppCompatResources.getDrawable(AppContext.getContext(), item.getFlag()));

        if (position==currentPosition) {
            binding.imgChooseLanguage.setImageDrawable(AppCompatResources.getDrawable(AppContext.getContext(), R.drawable.ic_select_language));
        } else {
            binding.imgChooseLanguage.setImageDrawable(AppCompatResources.getDrawable(AppContext.getContext(), R.drawable.ic_un_select_lang));
        }

        binding.getRoot().setOnClickListener(v -> {
            listener.onClickItemListener(item,position);
        });

    }

    public interface OnLanguageClickListener {
        void onClickItemListener(Language language,int position);
    }
}

