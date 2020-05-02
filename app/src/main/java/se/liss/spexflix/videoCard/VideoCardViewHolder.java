package se.liss.spexflix.videoCard;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;

import se.liss.spexflix.R;
import se.liss.spexflix.utils.GlideApp;
import se.liss.spexflix.utils.GlideRequests;

public class VideoCardViewHolder extends RecyclerView.ViewHolder{
    private Context context;

    private ImageView posterImage;
    private TextView year;
    private TextView title;
    private TextView alternateTitle;
    private TextView duration;

    private View playButton;
    private View subtitlesIndicator;
    private View videoIndicator;
    private View infoIndicator;

    private TextView videoNumberText;

    private View extraData;
    private View cardRoot;
    private View expandButton;

    private boolean hasExtraData = false;

    private int contentHeight = -1;
    private int extraDataHeight = -1;

    private GlideRequests glide;

    private CardClickListener listener;

    public VideoCardViewHolder(@NonNull final View itemView) {
        super(itemView);

        context = itemView.getContext();
        glide = GlideApp.with(context);

        itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onCardClicked(itemView);
        });

        contentHeight = context.getResources().getDimensionPixelSize(R.dimen.video_card_height);

        posterImage = itemView.findViewById(R.id.video_card_poster_image);
        year = itemView.findViewById(R.id.video_card_year_text);
        title = itemView.findViewById(R.id.video_card_title_text);
        alternateTitle = itemView.findViewById(R.id.video_card_alternate_title_text);
        duration = itemView.findViewById(R.id.video_card_duration);

        extraData = itemView.findViewById(R.id.video_card_alternate_content_list);
        cardRoot = itemView.findViewById(R.id.video_card_root);
        expandButton = itemView.findViewById(R.id.video_card_expand_button);

        videoNumberText = itemView.findViewById(R.id.video_number_text);

        expandButton.setOnClickListener(v -> {
            if (hasExtraData)
                toggleCardExpanded();
        });

        playButton = itemView.findViewById(R.id.video_card_play_button);
        playButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCardPlayClicked(itemView);
            }
        });
        subtitlesIndicator = itemView.findViewById(R.id.video_card_subtitle_indicator);
        videoIndicator = itemView.findViewById(R.id.video_card_video_indicator);
        infoIndicator = itemView.findViewById(R.id.video_card_info_indicator);

        posterImage.setClipToOutline(true);
    }

    public void setListener(CardClickListener listener) {
        this.listener = listener;
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

    public void setVideoNumberText(String text) {
        if (text == null)
            text = "";
        videoNumberText.setText(text);
    }

    public void setHasExtraData(boolean hasExtraData, int length) {
        this.hasExtraData = hasExtraData;
        expandButton.setVisibility(hasExtraData ? View.VISIBLE : View.GONE);

        if (hasExtraData) {
            expandButton.setRotation(0);
            extraDataHeight = (length * context.getResources().getDimensionPixelSize(R.dimen.video_extra_card_height))
                    + ((length - 1) * context.getResources().getDimensionPixelSize(R.dimen.video_extra_card_vertical_margin));

            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = contentHeight;
            itemView.setLayoutParams(layoutParams);
        }
    }

    private void toggleCardExpanded() {
        int startHeight = itemView.getHeight();
        boolean isExpanding = startHeight == contentHeight;

        int endHeight = isExpanding ? contentHeight + extraDataHeight : contentHeight;
        ValueAnimator anim = ValueAnimator.ofInt(startHeight, endHeight);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer)valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = val;
            itemView.setLayoutParams(layoutParams);
        });
        anim.setDuration(300);
        anim.start();

        expandButton.animate().rotation(isExpanding ? 180 : 0).start();
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

        glide.load(posterUrl).into(posterImage);
        //posterImage.setImageDrawable(posterImage.getContext().getDrawable(R.drawable.dummy_poster));
    }
}
