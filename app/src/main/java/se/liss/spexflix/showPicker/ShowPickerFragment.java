package se.liss.spexflix.showPicker;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.liss.spexflix.R;
import se.liss.spexflix.data.ApiInterface;
import se.liss.spexflix.data.ApiService;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.videoCard.VideoCardAdapter;
import se.liss.spexflix.videoCard.VideoCardDecorator;
import se.liss.spexflix.MainListener;

public class ShowPickerFragment extends Fragment {
    private Context context;
    private MainListener listener;

    private ApiInterface apiInterface;

    public ShowPickerFragment(MainListener listener) {
        this.listener = listener;
        this.apiInterface = ApiService.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.show_picker_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        RecyclerView recyclerView = getView().findViewById(R.id.main_recycler_view);
        VideoCardAdapter adapter = new VideoCardAdapter(context);
        adapter.setListener(listener);
        recyclerView.addItemDecoration(new VideoCardDecorator(context));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        apiInterface.getProductions().enqueue(new Callback<List<ShowData>>() {
            @Override
            public void onResponse(Call<List<ShowData>> call, Response<List<ShowData>> response) {
                if (response.isSuccessful())
                    adapter.setData(response.body());
                else
                    Toast.makeText(context, "oops", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<ShowData>> call, Throwable t) {
                Toast.makeText(context, "oops", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
