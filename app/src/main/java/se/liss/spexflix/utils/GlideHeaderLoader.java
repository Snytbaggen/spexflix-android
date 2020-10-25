package se.liss.spexflix.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import java.io.InputStream;

import se.liss.spexflix.account.AuthStateManager;

public class GlideHeaderLoader extends BaseGlideUrlLoader<String> {
    private android.os.Handler handler = new Handler(Looper.getMainLooper());
    private AuthStateManager mAuthStateManager;
    private AuthorizationService mAuthService;

    protected GlideHeaderLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, Context context) {
        super(concreteLoader);
        mAuthStateManager = AuthStateManager.getInstance(context);
        mAuthService = new AuthorizationService(context);
    }

    //protected GlideHeaderLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, @Nullable ModelCache<String, GlideUrl> modelCache) {
    //    super(concreteLoader, modelCache);
    //}

    @Override
    protected String getUrl(String s, int width, int height, Options options) {
        return s;
    }

    private void handleAccessTokenResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {
        mAuthStateManager.updateAfterTokenResponse(tokenResponse, authException);
    }

    @Nullable
    @Override
    protected Headers getHeaders(String s, int width, int height, Options options) {
        if (mAuthStateManager.getCurrent().getNeedsTokenRefresh()) {
            mAuthService.performTokenRequest(mAuthStateManager.getCurrent().createTokenRefreshRequest(), this::handleAccessTokenResponse);
        }
        String accessToken = mAuthStateManager.getCurrent().getAccessToken();
        if (accessToken != null)
            return new LazyHeaders.Builder().addHeader("Authorization", "Bearer " + accessToken).build();

        return super.getHeaders(s, width, height, options);
    }

    @Override
    public boolean handles(@NonNull String s) {
        return true;
    }
}
