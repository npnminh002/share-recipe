package doan.npnm.sharerecipe.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import doan.npnm.sharerecipe.dialog.NotificationDialog;
import doan.npnm.sharerecipe.lib.shared_preference.SharedPreference;
import doan.npnm.sharerecipe.utility.Constant;

public class FirebaseService extends FirebaseMessagingService {
    private final String TAG = this.getClass().getName();

    @Override
    public void onNewToken(@NonNull String token) {

        Log.d("Test", "Deivce Token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(new SharedPreference().getBoolean("IsNoty",true)) {
            String notyType = remoteMessage.getData().get(Constant.NOTIFICATION_TYPE);
            if (notyType.equals("AddRecipe")) {
                NotificationDialog.pushNotiAddSuccess(getApplicationContext(),remoteMessage);
            }
        }

    }

}



