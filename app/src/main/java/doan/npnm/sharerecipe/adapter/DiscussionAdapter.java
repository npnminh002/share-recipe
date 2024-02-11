package doan.npnm.sharerecipe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.databinding.ItemDiscussionViewLevel1Binding;
import doan.npnm.sharerecipe.databinding.ItemDiscussionViewLevel2Binding;
import doan.npnm.sharerecipe.model.disscus.DiscussType;
import doan.npnm.sharerecipe.model.disscus.Discussion;

public class DiscussionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Discussion> arrayList = new ArrayList<>();
    final DiscussType discussType;
    public OnDiscussionEvent event;

    public DiscussionAdapter(DiscussType discussType, OnDiscussionEvent event) {
        this.discussType = discussType;
        this.event = event;
    }


    public void setItem(ArrayList<Discussion> discussions) {
        arrayList.clear();
        arrayList = discussions;
        notifyDataSetChanged();
    }

    public interface OnDiscussionEvent {
        void onReply(Discussion dcs);

        void onChangeIcon(Discussion dcs);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return discussType == DiscussType.DISSCUS ? new Level1ViewHolder(ItemDiscussionViewLevel1Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false))
                : new Level2ViewHolder(ItemDiscussionViewLevel2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (discussType == DiscussType.DISSCUS) {
            ((Level1ViewHolder) holder).onBind(arrayList.get(position), position);
        } else {
            ((Level2ViewHolder) holder).onBind(arrayList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Level1ViewHolder extends RecyclerView.ViewHolder {
        final ItemDiscussionViewLevel1Binding binding;

        public Level1ViewHolder(ItemDiscussionViewLevel1Binding item) {
            super(item.getRoot());
            this.binding = item;
        }

        public void onBind(Discussion discussion, int position) {
            binding.view3.setVisibility(position==0? View.VISIBLE : View.GONE);
            binding.view2.setVisibility(position!=0? View.VISIBLE : View.GONE);
            DiscussionAdapter discussionAdapter = new DiscussionAdapter(DiscussType.REPLY, new OnDiscussionEvent() {
                @Override
                public void onReply(Discussion dcs) {
                    event.onReply(discussion);
                }

                @Override
                public void onChangeIcon(Discussion dcs) {
                    event.onChangeIcon(dcs);
                }
            });
            //   binding.imgUser.loadImage(discussion.DisscusAuth.Image == "" ? R.drawable.img_1 : discussion.DisscusAuth.Image);
            binding.txtContent.setText(discussion.Content);
            binding.txtUserName.setText(discussion.DisscusAuth.AuthName);
            if (discussion.DiscussionArray.size() != 0) {
                binding.rcvReply.setVisibility(View.VISIBLE);
                binding.rcvReply.setAdapter(discussionAdapter);
                discussionAdapter.setItem(discussion.DiscussionArray);
            }
            binding.icReply.setOnClickListener(v -> event.onReply(discussion));
            binding.icLove.setOnLongClickListener(v -> {
                event.onChangeIcon(discussion);
                return true;
            });

        }
    }


    public class Level2ViewHolder extends RecyclerView.ViewHolder {
        final ItemDiscussionViewLevel2Binding binding;

        public Level2ViewHolder(ItemDiscussionViewLevel2Binding item) {
            super(item.getRoot());
            this.binding = item;
        }

        public void onBind(Discussion discussion, int position) {


            binding.view3.setVisibility(position==0? View.VISIBLE : View.GONE);
            binding.view2.setVisibility(position!=0? View.VISIBLE : View.GONE);

          //  binding.imgUser.loadImage(discussion.DisscusAuth.Image == "" ? R.drawable.img_1 : discussion.DisscusAuth.Image);
            binding.txtContent.setText(discussion.Content);
            binding.txtUserName.setText(discussion.DisscusAuth.AuthName);
            binding.icReply.setOnClickListener(v -> event.onReply(discussion));
            binding.icLove.setOnLongClickListener(v -> {
                event.onChangeIcon(discussion);
                return true;
            });
        }
    }
}
