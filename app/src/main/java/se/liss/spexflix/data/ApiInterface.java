package se.liss.spexflix.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiInterface {
    @GET("productions")
    Call<List<ShowData>> getProductions();

    // TODO: Call another endpoint, when/if login endpoint exists
    @GET("productions")
    Call<Object> login(@Header("Authorization") String auth);
}
