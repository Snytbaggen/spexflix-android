package se.liss.spexflix.data;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import se.liss.spexflix.account.SpexflixAccountAuthenticator;

public class TokenInterceptor implements Interceptor {
    private final AccountManager accountManager;
    private Handler handler = new Handler(Looper.getMainLooper());

    public TokenInterceptor(Context context) {
        accountManager = AccountManager.get(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String authToken = SpexflixAccountAuthenticator.getAuthToken(accountManager, handler);
        Request request = chain.request();

        if (authToken != null) {

            Request newRequest = request.newBuilder()
                    .addHeader("Authorization", "Basic " + authToken)
                    .build();

            Response response = chain.proceed(newRequest);

            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                accountManager.invalidateAuthToken(SpexflixAccountAuthenticator.ACCOUNT_TYPE, authToken);
            }

            return response;
        }
        return chain.proceed(request);
    }
}
