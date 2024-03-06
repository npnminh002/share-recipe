package doan.npnm.sharerecipe.fragment.user;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import doan.npnm.sharerecipe.adapter.users.ChildNotiAdapter;
import doan.npnm.sharerecipe.adapter.users.ParentNotiAdapter;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.notification.Notification;
import doan.npnm.sharerecipe.model.notification.NotyType;
import doan.npnm.sharerecipe.model.notification.ParentNotification;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;
import doan.npnm.sharerecipe.databinding.FragmentNotificationBinding;

public class NotificationFragment extends BaseFragment<FragmentNotificationBinding> {

    private UserViewModel viewModel;

    public NotificationFragment(UserViewModel userViewModel) {
        this.viewModel = userViewModel;
    }

    @Override
    protected FragmentNotificationBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentNotificationBinding.inflate(inflater);
    }

    private ArrayList<Notification> listNoti = new ArrayList<>();
    private ArrayList<ParentNotification> parentNotifications = new ArrayList<>();
    private ParentNotiAdapter adapter;
    private ChildNotiAdapter childNotiAdapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {
        childNotiAdapter = new ChildNotiAdapter(notification -> {
            redirectToView(notification);
        });
        parentNotifications = new ArrayList<>();
        adapter = new ParentNotiAdapter(notification -> {
            redirectToView(notification);
        });
        binding.rcvNotification.setAdapter(adapter);
        getNotification();

    }

    private void redirectToView(Notification notification) {
        if (notification.NotyType.equals(NotyType.USER_ADD)) {
            viewModel.getRecipeByID(notification.Value, new FetchByID<Recipe>() {
                @Override
                public void onSuccess(Recipe data) {
                    replaceFullViewFragment(new DetailRecipeFragment(data, viewModel), android.R.id.content, true);
                }

                @Override
                public void onErr(Object err) {
                    showToast(err);
                }
            });
        }
    }


    private void getNotification() {
        viewModel.fbDatabase.getReference(Constant.NOTIFICATION)
                .child(viewModel.getUsers().getValue().UserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String currentTime = "";

                        ArrayList<Notification> sortData = new ArrayList<>();
                        parentNotifications = new ArrayList<>();


                        for (DataSnapshot doc : snapshot.getChildren()) {
                            Notification notification = doc.getValue(Notification.class);
                            sortData.add(notification);
                        }
                        binding.txtCountNoti.setText(""+sortData.size());

                        Collections.sort(sortData, (o1, o2) -> o2.Time.compareTo(o1.Time));

                        for (Notification notification : sortData) {

                            if (!currentTime.equals(notification.Time.substring(0, 10))) {
                                if (!listNoti.isEmpty()) {
                                    Collections.sort(listNoti, (o1, o2) -> o2.Time.compareTo(o1.Time));
                                    parentNotifications.add(new ParentNotification(currentTime, listNoti));
                                    listNoti = new ArrayList<>();
                                }
                                currentTime = notification.Time.substring(0, 10);
                            }

                            listNoti.add(notification);
                        }

                        if (!listNoti.isEmpty()) {
                            parentNotifications.add(new ParentNotification(currentTime, listNoti));
                        }
                        adapter.setItems(parentNotifications);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public void OnClick() {
        binding.icSearch.setOnClickListener(v -> {
            ArrayList<Notification> searchArr = new ArrayList<>();
            String serachKey = binding.edtSearchNoti.getText().toString();
            if (serachKey != "") {
                for (Notification noti : listNoti) {
                    if (noti.Time.contains(serachKey) || noti.Content.contains(serachKey)) {
                        searchArr.add(noti);
                    }
                }
                childNotiAdapter.setItems(searchArr);
                binding.rcvNotification.setAdapter(childNotiAdapter);
            }
        });

        binding.edtSearchNoti.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    adapter.setItems(parentNotifications);
                    binding.rcvNotification.setAdapter(adapter);
                }

            }
        });

    }
}
