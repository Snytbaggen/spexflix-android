package se.liss.spexflix.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;

public class GlideHeaderLoader extends BaseGlideUrlLoader<String> {
    private Context context;

    protected GlideHeaderLoader(ModelLoader<GlideUrl, InputStream> concreteLoader) {
        super(concreteLoader);
    }

    protected GlideHeaderLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, @Nullable ModelCache<String, GlideUrl> modelCache) {
        super(concreteLoader, modelCache);
    }

    public void setContext(Context context) {
        this.context = context;
        // TODO: Account stuff
    }

    @Override
    protected String getUrl(String s, int width, int height, Options options) {
        return s;
    }

    @Nullable
    @Override
    protected Headers getHeaders(String s, int width, int height, Options options) {
        return new LazyHeaders.Builder().addHeader("Authorization", "Basic U255dGJhZ2dlbjpYNDJhQm42Nw==").build();
    }

    @Override
    public boolean handles(@NonNull String s) {
        return true;
    }
}
