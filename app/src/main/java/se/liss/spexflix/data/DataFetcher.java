package se.liss.spexflix.data;

import android.content.Context;

import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataFetcher {
    private static final int ONE_HOUR_MS = 60 * 60 * 1000;
    private static DataFetcher instance;

    private final List<ShowData> showData = new ArrayList<>();
    private Instant showDataUpdated;
    private ApiInterface apiService;

    public interface Callback<T> {
        void onDataFetched(T data);
    }

    public static DataFetcher getInstance(Context context) {
        if (instance == null)
            instance = new DataFetcher(context);
        return instance;
    }

    private DataFetcher(Context context) {
        apiService = ApiService.getInstance(context);
    }

    public void getShowData(boolean force, Callback<List<ShowData>> callback) {
        Instant now = Instant.now();
        if (force || showDataUpdated == null || now.isAfter(showDataUpdated.plus(ONE_HOUR_MS))) {
            showDataUpdated = now;
            apiService.getProductions().enqueue(new retrofit2.Callback<List<ShowData>>() {
                @Override
                public void onResponse(Call<List<ShowData>> call, Response<List<ShowData>> response) {
                    showData.clear();
                    if (response.isSuccessful() && response.body() != null) {
                        showData.addAll(response.body());
                    }
                    callback.onDataFetched(showData);
                }

                @Override
                public void onFailure(Call<List<ShowData>> call, Throwable t) {
                    showData.clear();
                    callback.onDataFetched(showData);
                }
            });
        } else {
            callback.onDataFetched(showData);
        }
    }

}
