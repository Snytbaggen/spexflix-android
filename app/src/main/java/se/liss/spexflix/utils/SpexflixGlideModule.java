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
        registry.append(String.class, InputStream.class, new GlideHeaderLoaderFactory(context));
    }

    class GlideHeaderLoaderFactory implements ModelLoaderFactory<String, InputStream> {
        private Context context;
        GlideHeaderLoaderFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ModelLoader<String, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new GlideHeaderLoader(multiFactory.build(GlideUrl.class, InputStream.class), this.context);
        }

        @Override
        public void teardown() {

        }
    }
}
