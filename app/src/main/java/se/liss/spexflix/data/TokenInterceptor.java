package se.liss.spexflix.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
//import se.liss.spexflix.account.SpexflixAccountAuthenticator;
import se.liss.spexflix.account.AuthStateManager;

public class TokenInterceptor implements Interceptor {
    private final AuthStateManager mAuthStateManager;
    private final AuthorizationService mAuthService;
    private Handler handler = new Handler(Looper.getMainLooper());

    public TokenInterceptor(Context context) {
        mAuthStateManager = AuthStateManager.getInstance(context);
        mAuthService = new AuthorizationService(context);
    }

    private void handleAccessTokenResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {
        mAuthStateManager.updateAfterTokenResponse(tokenResponse, authException);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        AuthState currentState = mAuthStateManager.getCurrent();
        if (currentState.isAuthorized()) {

            if (mAuthStateManager.getCurrent().getNeedsTokenRefresh()) {
                mAuthService.performTokenRequest(mAuthStateManager.getCurrent().createTokenRefreshRequest(), this::handleAccessTokenResponse);
            }
            String accessToken = mAuthStateManager.getCurrent().getAccessToken();
            if (accessToken != null) {

                Request newRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                Response response = chain.proceed(newRequest);

                return response;
            }
        }
        return chain.proceed(request);
    }
}
