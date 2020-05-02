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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.List;

import se.liss.spexflix.R;
import se.liss.spexflix.account.SpexflixAccountAuthenticator;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.data.ShowVideo;

public class PlayerFragment extends Fragment {
    public static final String ARG_START_PLAYBACK = "start_playback";
    public static final String ARG_ENTER_FULLSCREEN = "enter_fullscreen";
    public static final String ARG_VIDEO_INDEX = "video_id";
    public static final int NO_ID = -1;

    private ShowData data;

    private TextView title;
    private TextView alternateTitle;
    private TextView year;
    private TextView info;

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
        title = v.findViewById(R.id.show_detail_title);
        alternateTitle = v.findViewById(R.id.show_detail_alternate_title);
        year = v.findViewById(R.id.show_detail_year);
        info = v.findViewById(R.id.show_detail_info);
        playerView = v.findViewById(R.id.show_detail_player);

        immediatePlayback = getArguments().getBoolean(ARG_START_PLAYBACK, false);
        enterFullscreen = getArguments().getBoolean(ARG_ENTER_FULLSCREEN, false);
        videoId = getArguments().getInt(ARG_VIDEO_INDEX, NO_ID);

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

        year.setText(data.getShortName());

        info.setText(data.getInformation());

        if (data.getVideos() != null && !data.getVideos().isEmpty()) {
            List<ShowVideo> videos = data.getVideos();
            // TODO: Pick video depending on kind
            ShowVideo video = videos.get(0);
            String videoUrl = video.getVideoFile();
            if (videoUrl != null) {
                Uri videoUri = Uri.parse(videoUrl);

                player = new SimpleExoPlayer.Builder(getContext()).build();
                player.setPlayWhenReady(immediatePlayback);
                playerView.setPlayer(player);

                Handler handler = new Handler();

                new Thread(() -> {
                    try {
                        AccountManager manager = AccountManager.get(getContext());
                        String authToken = SpexflixAccountAuthenticator.getAuthToken(manager, handler);

                        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(getContext(), "Spexflix"));
                        dataSourceFactory.getDefaultRequestProperties().set("Authorization", "Basic " + authToken);
                        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(videoUri);
                        handler.post(() -> {player.prepare(videoSource);});
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
            oldOrientation = newConfig.orientation;

            if (oldOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                enableFullscreen();
            } else {
                disableFullscreen();
            }
        }
    }

    public void enableFullscreen() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        ViewGroup.LayoutParams layoutParams =  playerView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(layoutParams);
    }

    public void disableFullscreen() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ViewGroup.LayoutParams layoutParams =  playerView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        playerView.setLayoutParams(layoutParams);
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
