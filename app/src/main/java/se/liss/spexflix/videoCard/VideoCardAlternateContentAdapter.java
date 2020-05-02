package se.liss.spexflix.videoCard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import se.liss.spexflix.R;
import se.liss.spexflix.data.ShowVideo;

public class VideoCardAlternateContentAdapter extends RecyclerView.Adapter implements CardClickListener {
    private Context context;

    List<ShowVideo> data;

    public VideoCardAlternateContentAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setData(List<ShowVideo> data) {
        this.data.clear();
        if (data != null)
            this.data.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.video_card_sublist_item, parent, false);
        VideoCardAlternateContentViewHolder holder = new VideoCardAlternateContentViewHolder(v);
        holder.setListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rawHolder, int position) {
        VideoCardAlternateContentViewHolder holder = (VideoCardAlternateContentViewHolder)rawHolder;

        ShowVideo video = data.get(position);

        holder.setTitle(video.getTitle());
        holder.setKind(video.getVideoType().toString());
        holder.setDescription(video.getInformation());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onCardClicked(View v) {
        Toast.makeText(context, "Card clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardPlayClicked(View v) {
        Toast.makeText(context, "Play clicked", Toast.LENGTH_SHORT).show();
    }
}
