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

        Integer year = show.getYear();
        String yearString = year == null ? "" : Integer.toString(year);
        holder.setYear(yearString);

        holder.setPosterImage(show.getPosterUrl());

        holder.setTitle(show.getTitle());

        holder.setAlternatTitle(show.getAlternateTitle());

        holder.setDuration(show.getRuntime());

        holder.setSubtitlesEnabled(show.getSubtitleUrl() != null);

        holder.setVideoEnabled(show.getVideoUrl() != null);

        holder.setInfoEnabled(show.getInfo() != null);
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
        Toast.makeText(context, "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }
}
