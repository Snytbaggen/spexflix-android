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
    private RecyclerView recyclerView;

    private Listener listener;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    List<ShowVideo> data;

    public interface Listener {
        void onVideoClicked(ShowVideo video, boolean play);
    }

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

    public void setListener(Listener listener) {
        this.listener = listener;
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
        if (recyclerView == null)
            return;

        int position = recyclerView.getChildAdapterPosition(v);
        if (position < 0 || position >= data.size())
            return;

        if (listener != null)
            listener.onVideoClicked(data.get(position), false);
    }

    @Override
    public void onCardPlayClicked(View v) {
        if (recyclerView == null)
            return;

        int position = recyclerView.getChildAdapterPosition(v);
        if (position < 0 || position >= data.size())
            return;

        if (listener != null)
            listener.onVideoClicked(data.get(position), true);
    }
}
