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

import se.liss.spexflix.R;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.videoCard.VideoCardAdapter;
import se.liss.spexflix.videoCard.VideoCardDecorator;
import se.liss.spexflix.MainListener;

public class ShowPickerFragment extends Fragment {
    private Context context;
    private MainListener listener;

    public ShowPickerFragment(MainListener listener) {
        this.listener = listener;
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

        Gson gson = new Gson();
        String jsonString = null;
        AssetManager assetManager = context.getAssets();
        try {
            for (String asset : assetManager.list("")) {
                if (asset.endsWith(".json")) {
                    InputStream is = getContext().getAssets().open(asset);

                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();

                    jsonString = new String(buffer, "UTF-8");
                    break;
                }
            }

            if (jsonString != null) {
                Type showDataType = new TypeToken<List<ShowData>>() {}.getType();
                List<ShowData> dataList = gson.fromJson(jsonString, showDataType);
                adapter.setData(dataList);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Load error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
