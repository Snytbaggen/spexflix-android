package se.liss.spexflix.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("productions")
    Call<List<ShowData>> getProductions();
}
