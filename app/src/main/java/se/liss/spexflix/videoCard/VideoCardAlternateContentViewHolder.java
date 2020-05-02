package se.liss.spexflix.videoCard;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import se.liss.spexflix.R;

public class VideoCardAlternateContentViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView kind;
    private TextView description;

    private CardClickListener listener;

    public VideoCardAlternateContentViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onCardClicked(itemView); // TODO: Replace with real method
        });

        title = itemView.findViewById(R.id.video_card_sub_item_name);
        kind = itemView.findViewById(R.id.video_card_sub_item_video_type);
        description = itemView.findViewById(R.id.video_card_sub_item_description);

        View playButton = itemView.findViewById(R.id.video_card_sub_item_play);
        playButton.setOnClickListener(v ->  {
            if (listener != null)
                listener.onCardPlayClicked(itemView); // TODO: Replace with real method
        });
    }

    public void setListener(CardClickListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        if (title == null)
            title = "";
        this.title.setText(title);
    }

    public void setKind(String kind) {
        if (kind == null)
            kind = "";
        this.kind.setText(kind);
    }

    public void setDescription(String description) {
        if (description == null)
            description = "";
        this.description.setText(description);
    }
}
