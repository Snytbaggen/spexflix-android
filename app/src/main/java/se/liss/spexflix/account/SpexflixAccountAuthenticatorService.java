package se.liss.spexflix.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SpexflixAccountAuthenticatorService extends Service {
    private SpexflixAccountAuthenticator authenticator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (authenticator == null)
            authenticator = new SpexflixAccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}
