package doan.npnm.sharerecipe.firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.interfaces.DataEventListener;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.network.ApiClient;
import doan.npnm.sharerecipe.network.ApiService;
import doan.npnm.sharerecipe.utility.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMNotificationSender {

    public enum NOTY_TYPE{
        ADD_RECIPE
    }

    public static void sendNotiAddRecipe(String reciveToken, Recipe recipe, DataEventListener<String> event){
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(reciveToken);
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(Constant.NOTIFICATION_TYPE,"AddRecipe");

            data.put(Constant.NOTI_CONTENT, AppContext.getContext().getString(R.string.add_success));
            data.put(Constant.RECIPE,recipe.toJson());

            body.put(Constant.REMOTE_MSG_DATA, data);
            body.put(Constant.REMOTE_MSG_IDS, tokens);
            sendRemoteMessage(body.toString(), new DataEventListener<String>() {
                @Override
                public void onSuccess(String data) {
                    event.onSuccess(data);
                }

                @Override
                public void onErr(Object err) {
                    event.onErr(err);
                }
            });
        } catch (JSONException ex) {
            event.onErr(ex.getMessage());
        }
    }

    private static void sendRemoteMessage(String remotemess, DataEventListener<String> eventListener) {
        new ApiClient().getClient().create(ApiService.class)
                .sendMess(Constant.GetRemoteMSGHeader(), remotemess)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            eventListener.onSuccess(remotemess);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        eventListener.onErr(t.getMessage());
                    }
                });
    }


}


