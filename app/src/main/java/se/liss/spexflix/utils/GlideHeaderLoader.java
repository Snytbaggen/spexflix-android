package se.liss.spexflix.utils;

import android.accounts.AccountManager;
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

import java.io.IOException;
import java.io.InputStream;

import se.liss.spexflix.account.SpexflixAccountAuthenticator;

public class GlideHeaderLoader extends BaseGlideUrlLoader<String> {
    private Context context;
    private android.os.Handler handler = new Handler(Looper.getMainLooper());
    private AccountManager accountManager;

    protected GlideHeaderLoader(ModelLoader<GlideUrl, InputStream> concreteLoader) {
        super(concreteLoader);
    }

    protected GlideHeaderLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, @Nullable ModelCache<String, GlideUrl> modelCache) {
        super(concreteLoader, modelCache);
    }

    public void setContext(Context context) {
        this.context = context;
        accountManager = AccountManager.get(context);
    }

    @Override
    protected String getUrl(String s, int width, int height, Options options) {
        return s;
    }

    @Nullable
    @Override
    protected Headers getHeaders(String s, int width, int height, Options options) {
        try {
            String authToken = SpexflixAccountAuthenticator.getAuthToken(accountManager, handler);
            if (authToken != null)
                return new LazyHeaders.Builder().addHeader("Authorization", "Basic " + authToken).build();
        } catch (IOException e) {
            // Do nothing
        }

        return super.getHeaders(s, width, height, options);
    }

    @Override
    public boolean handles(@NonNull String s) {
        return true;
    }
}
