package se.liss.spexflix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.videoCard.VideoCardAdapter;
import se.liss.spexflix.videoCard.VideoCardDecorator;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
        VideoCardAdapter adapter = new VideoCardAdapter(this);
        recyclerView.addItemDecoration(new VideoCardDecorator(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Gson gson = new Gson();
        String jsonString = null;
        AssetManager assetManager = getAssets();
        try {
            for (String asset : assetManager.list("")) {
                if (asset.endsWith(".json")) {
                    InputStream is = getAssets().open(asset);

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
            Toast.makeText(this, "Load error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
