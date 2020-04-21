package se.liss.spexflix.videoCard;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import se.liss.spexflix.R;

public class VideoCardViewHolder extends RecyclerView.ViewHolder{
    private ImageView posterImage;
    private TextView year;
    private TextView title;
    private TextView alternateTitle;
    private TextView duration;

    private View playButton;
    private View subtitlesIndicator;
    private View videoIndicator;
    private View infoIndicator;

    public VideoCardViewHolder(@NonNull View itemView) {
        super(itemView);

        posterImage = itemView.findViewById(R.id.video_card_poster_image);
        year = itemView.findViewById(R.id.video_card_year_text);
        title = itemView.findViewById(R.id.video_card_title_text);
        alternateTitle = itemView.findViewById(R.id.video_card_alternate_title_text);
        duration = itemView.findViewById(R.id.video_card_duration);

        playButton = itemView.findViewById(R.id.video_card_play_button);
        subtitlesIndicator = itemView.findViewById(R.id.video_card_subtitle_indicator);
        videoIndicator = itemView.findViewById(R.id.video_card_video_indicator);
        infoIndicator = itemView.findViewById(R.id.video_card_info_indicator);

        posterImage.setClipToOutline(true);
    }

    public void setTitle(String title) {
        if (title == null)
            title = "";
        this.title.setText(title);
    }

    public void setAlternatTitle(String alternateTitle) {
        if (alternateTitle == null)
            alternateTitle = "";
        this.alternateTitle.setText(alternateTitle);
    }

    @SuppressLint("SetTextI18n")
    public void setYear(String year) {
        if (year == null)
            year = "";
        this.year.setText(year);
    }

    public void setDuration(String duration) {
        if (duration == null)
            duration = "";
        this.duration.setText(duration);
    }

    public void setVideoEnabled(boolean enabled) {
        videoIndicator.setAlpha(enabled ? 1f : 0.4f);
        playButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void setSubtitlesEnabled(boolean enabled) {
        subtitlesIndicator.setAlpha(enabled ? 1f : 0.4f);
    }

    public void setInfoEnabled(boolean enabled) {
        infoIndicator.setAlpha(enabled ? 1f : 0.4f);
    }

    public void setPosterImage(String posterUrl) {
        // TODO: Fix this when Glide is in place
        posterImage.setImageDrawable(posterImage.getContext().getDrawable(R.drawable.dummy_poster));
    }
}
