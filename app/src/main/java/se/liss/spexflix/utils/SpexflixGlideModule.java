package se.liss.spexflix.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

@GlideModule
public class SpexflixGlideModule extends AppGlideModule {
    private Context context;

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.append(String.class, InputStream.class, new GlideHeaderLoaderFactory());
    }

    static class GlideHeaderLoaderFactory implements ModelLoaderFactory<String, InputStream> {

        @NonNull
        @Override
        public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new GlideHeaderLoader(multiFactory.build(GlideUrl.class, InputStream.class));
        }

        @Override
        public void teardown() {

        }
    }

    static class GlideHeaderLoader extends BaseGlideUrlLoader<String> {

        protected GlideHeaderLoader(ModelLoader<GlideUrl, InputStream> concreteLoader) {
            super(concreteLoader);
        }

        protected GlideHeaderLoader(ModelLoader<GlideUrl, InputStream> concreteLoader, @Nullable ModelCache<String, GlideUrl> modelCache) {
            super(concreteLoader, modelCache);
        }

        @Override
        protected String getUrl(String s, int width, int height, Options options) {
            return s;
        }

        @Nullable
        @Override
        protected Headers getHeaders(String s, int width, int height, Options options) {
            // TODO: Account stuff
            return super.getHeaders(s, width, height, options);}
        }

        @Override
        public boolean handles(@NonNull String s) {
            return true;
        }
    }
}
