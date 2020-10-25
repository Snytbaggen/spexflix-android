package se.liss.spexflix.cast;

import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

//import se.liss.spexflix.account.SpexflixAccountAuthenticator;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.data.ShowVideo;

public class CastLoader {
    public static void castMedia(CastSession castSession, Context context, ShowData showData) {
        AccountManager accountManager = AccountManager.get(context);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            //try {
                String token = "";//SpexflixAccountAuthenticator.getAuthToken(accountManager, mainHandler);
                mainHandler.post( () -> buildAndStartCast(castSession, showData, token));
            //} catch (IOException e) {
                // Do nothing
            //}
        }).start();
    }

    private static void buildAndStartCast(CastSession castSession, ShowData showData, String authToken) {
        RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient == null)
            return;

        remoteMediaClient.load(new MediaLoadRequestData.Builder()
                .setMediaInfo(buildMediaInfo(showData))
                .setAutoplay(true)
                .setCredentials(authToken)
                .build());
    }

    private static MediaInfo buildMediaInfo(ShowData showData) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        ShowVideo video = showData.getVideo(0);

        movieMetadata.putString(MediaMetadata.KEY_TITLE, video.getTitle());
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, showData.getShortName());
        movieMetadata.addImage(new WebImage(Uri.parse(showData.getPosterUrl())));

        return new MediaInfo.Builder(video.getVideoFile())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetadata)
                .build();
    }
}
