package doan.npnm.sharerecipe.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ApiService {
    @POST("send")
    Call<String> sendMess(
            @HeaderMap HashMap<String, String> header,
            @Body String messbody
    );
}
