package doan.npnm.sharerecipe.activity.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import doan.npnm.sharerecipe.databinding.ActivityFacebookBinding;

public class FacebookActivity extends FragmentActivity {
    ActivityFacebookBinding binding;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityFacebookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Handler mHandler = new Handler();
        FacebookSdk.InitializeCallback initializeCallback = new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Code Here
                    }
                });
            }
        };
        //before setContentView()
        FacebookSdk.sdkInitialize(getApplicationContext(),initializeCallback);
        AppEventsLogger.activateApp(this.getApplication());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onError(@NonNull FacebookException e) {

            }

            @Override
            public void onCancel() {

            }
        });

        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .setShareHashtag(new ShareHashtag.Builder()
                                    .setHashtag("#ConnectTheWorld")
                                    .build())
                            .build();
                    shareDialog.show(linkContent);
                }
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}