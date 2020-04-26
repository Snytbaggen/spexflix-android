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

import se.liss.spexflix.MainListener;
import se.liss.spexflix.R;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.data.ShowVideo;

public class VideoCardAdapter extends RecyclerView.Adapter implements CardClickListener {
    private Context context;
    private MainListener listener;
    private RecyclerView recyclerView;

    private final List<ShowData> data;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public VideoCardAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setListener(MainListener listener) {
        this.listener = listener;
    }

    public void setData(List<ShowData> data) {
        this.data.clear();
        if (data != null)
            this.data.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.video_card, parent, false);
        VideoCardViewHolder holder = new VideoCardViewHolder(v);
        holder.setListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rawHolder, int position) {
        VideoCardViewHolder holder = (VideoCardViewHolder)rawHolder;
        ShowData show = data.get(position);

        holder.setYear(show.getShortName());

        holder.setPosterImage(show.getPosterUrl());

        holder.setTitle(show.getTitle());

        holder.setAlternatTitle(show.getSubtitle());

        holder.setDuration(null);

        List<ShowVideo> videos = show.getVideos();
        ShowVideo video = null;
        if (videos != null && !videos.isEmpty())
            video = videos.get(0);

        List<String> subtitles = video == null ? null : video.getSubtitles();
        holder.setSubtitlesEnabled(subtitles != null && subtitles.size() > 0);

        String videoUrl = video == null ? null : video.getVideoFile();
        holder.setVideoEnabled(videoUrl != null);

        holder.setInfoEnabled(show.getInformation() != null && !show.getInformation().isEmpty());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onCardClicked(View v) {
        if (recyclerView == null)
            return;

        int position = recyclerView.getChildAdapterPosition(v);
        if (position < 0 || position >= data.size())
            return;

        if (listener != null)
            listener.onCardClicked(data.get(position));
    }

    @Override
    public void onCardPlayClicked(View v) {
        if (recyclerView == null)
            return;

        int position = recyclerView.getChildAdapterPosition(v);
        if (position < 0 || position >= data.size())
            return;

        if (listener != null)
            listener.onPlayClicked(data.get(position));
    }
}
