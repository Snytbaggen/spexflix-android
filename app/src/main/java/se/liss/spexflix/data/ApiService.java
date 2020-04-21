package se.liss.spexflix.data;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static ApiInterface instance;

    public static ApiInterface getInstance() {
        if (instance == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor())
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://spexflix.studentspex.se/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            instance = retrofit.create(ApiInterface.class);
        }
        return instance;
    }
}
