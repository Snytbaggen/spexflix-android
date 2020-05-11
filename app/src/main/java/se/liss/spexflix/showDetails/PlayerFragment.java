package se.liss.spexflix.showDetails;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import se.liss.spexflix.MainActivity;
import se.liss.spexflix.R;
import se.liss.spexflix.account.SpexflixAccountAuthenticator;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.data.ShowSubtitle;
import se.liss.spexflix.data.ShowVideo;

public class PlayerFragment extends Fragment {
    public static final String ARG_START_PLAYBACK = "start_playback";
    public static final String ARG_ENTER_FULLSCREEN = "enter_fullscreen";
    public static final String ARG_VIDEO_INDEX = "video_id";
    public static final int DEFAULT_INDEX = 0;

    private ShowData data;

    private TextView title;
    private TextView alternateTitle;
    private TextView year;
    private TextView name;
    private TextView info;
    private TextView videoInfo;
    private TextView videoTitle;

    private PlayerView playerView;
    private SimpleExoPlayer player;

    private int oldOrientation = Configuration.ORIENTATION_UNDEFINED;

    private boolean immediatePlayback;
    private boolean enterFullscreen;
    private int videoId;
    private int savedOrientation;

    public void setData(ShowData data) {
        this.data = data;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.show_detail_fragment, container, false);
        title = v.findViewById(R.id.production_detail_title);
        alternateTitle = v.findViewById(R.id.production_detail_alternate_title);
        year = v.findViewById(R.id.production_detail_year);
        name = v.findViewById(R.id.production_detail_name);
        info = v.findViewById(R.id.production_detail_info);
        videoInfo = v.findViewById(R.id.production_detail_video_info);
        videoTitle = v.findViewById(R.id.production_detail_video_title);
        playerView = v.findViewById(R.id.video_player);

        immediatePlayback = getArguments().getBoolean(ARG_START_PLAYBACK, false);
        enterFullscreen = getArguments().getBoolean(ARG_ENTER_FULLSCREEN, false);
        videoId = getArguments().getInt(ARG_VIDEO_INDEX, DEFAULT_INDEX);

        if (enterFullscreen || getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            enableFullscreen();

        if (enterFullscreen) {
            savedOrientation = getActivity().getRequestedOrientation();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (data == null)
            return;

        title.setText(data.getTitle());

        alternateTitle.setText(data.getSubtitle());

        name.setText(data.getShortName());

        Integer productionYear = data.getYear();
        String yearString = productionYear == null ? "Okänt" : productionYear.toString();
        year.setText(yearString);

        info.setText(data.getInformation());

        if (data.getVideos() != null && !data.getVideos().isEmpty()) {
            List<ShowVideo> videos = data.getVideos();
            ShowVideo video = videos.get(videoId);

            videoTitle.setText(video.getTitle());

            String description = video.getInformation();
            if (description == null || description.isEmpty()) {
                videoInfo.setVisibility(View.GONE);
            } else {
                videoInfo.setText(description);
                videoInfo.setVisibility(View.VISIBLE);
            }

            String videoUrl = video.getVideoFile();
            if (videoUrl != null) {

                player = new SimpleExoPlayer.Builder(getContext()).build();
                player.setPlayWhenReady(immediatePlayback);
                playerView.setPlayer(player);

                Handler handler = new Handler();

                // Needs to be in a new thread because we're accessing AccountManager
                new Thread(() -> {
                    try {
                        AccountManager manager = AccountManager.get(getContext());
                        String authToken = SpexflixAccountAuthenticator.getAuthToken(manager, handler);
                        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(getContext(), "Spexflix"));
                        dataSourceFactory.getDefaultRequestProperties().set("Authorization", "Basic " + authToken);

                        Uri videoUri = Uri.parse(videoUrl);
                        List<ShowSubtitle> subtitles = video.getSubtitles();
                        int numberOfTracks = 1 + (subtitles == null ? 0 : subtitles.size());

                        MediaSource source = new ProgressiveMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(videoUri);

                        if (numberOfTracks > 1) {
                            // TODO: Multiple subtitles and subtitle styling
                            ShowSubtitle subtitle = subtitles.get(0);
                            Format subtitleFormat = Format.createTextSampleFormat(
                                    null,
                                    MimeTypes.TEXT_VTT,
                                    C.SELECTION_FLAG_DEFAULT,
                                    subtitle.getName());

                            Uri subtitleUri = Uri.parse(subtitle.getSubtitleFile());

                            MediaSource subtitleMediaSource = new SingleSampleMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(subtitleUri, subtitleFormat, C.TIME_UNSET);
                            source = new MergingMediaSource(source, subtitleMediaSource);
                        }

                        final MediaSource finalSource = source;

                        handler.post(() -> {player.prepare(finalSource);});
                    } catch (IOException e) {
                        // TODO: Error handling
                    }
                }).start();
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (enterFullscreen)
            return; // We handle this case in other ways

        if (newConfig.orientation != oldOrientation) {

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                enableFullscreen();
            } else {
                disableFullscreen();
            }
            oldOrientation = newConfig.orientation;
        }
    }

    public void enableFullscreen() {
        ((MainActivity)getActivity()).enableFullscreen();

        ViewGroup.LayoutParams layoutParams =  playerView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(layoutParams);
    }

    public void disableFullscreen() {
        ((MainActivity)getActivity()).disableFullscreen();

        ViewGroup.LayoutParams layoutParams =  playerView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        playerView.setLayoutParams(layoutParams);
        getView().invalidate();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
        }
        if (enterFullscreen) {
            disableFullscreen();
            getActivity().setRequestedOrientation(savedOrientation);
        }
    }
}
