package doan.npnm.sharerecipe.dialog;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.activity.user.MainActivity;
import doan.npnm.sharerecipe.lib.ImageDownloader;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class NotificationDialog {
    public static void pushNotiAddSuccess(Context context, RemoteMessage remoteMessage) {
        Recipe recipe = new Recipe().fromJson(remoteMessage.getData().get(Constant.RECIPE));
        ImageDownloader imageDownloaderMain = new ImageDownloader(bitmap -> {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constant.RECIPE, recipe.Id);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_add_recipe_small);
            @SuppressLint("RemoteViewLayout") RemoteViews notificationLayoutBig = new RemoteViews(context.getPackageName(), R.layout.notification_add_recipe_big);
            notificationLayoutBig.setImageViewBitmap(R.id.img_product_header, bitmap);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelId")
                    .setSmallIcon(R.drawable.ic_circle_notifications)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setContentIntent(pendingIntent)
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayoutBig);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "channelId";
                String channelName = "Channel Name";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId(channelId);
            }

            notificationManager.notify(666, builder.build());
        });

        imageDownloaderMain.execute(recipe.ImgUrl);
    }

}


