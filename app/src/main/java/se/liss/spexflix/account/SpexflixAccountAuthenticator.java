package se.liss.spexflix.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.NetworkErrorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import java.io.IOException;

import retrofit2.Response;
import se.liss.spexflix.data.ApiService;

public class SpexflixAccountAuthenticator  extends AbstractAccountAuthenticator {
    public final static String ACCOUNT_TYPE = "se.liss.account";
    public final static String AUTH_TOKEN_TYPE = "se.liss.account.basic-auth";
    public final static String ACCESS_TOKEN = "se.liss.account.access-token";

    private final Context context;
    private final AccountManager accountManager;

    public SpexflixAccountAuthenticator(Context context) {
        super(context);
        this.context = context;
        accountManager = AccountManager.get(context);
    }

    public static synchronized String getAuthToken(AccountManager accountManager, Handler handler) throws IOException {
        try {
            Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
            if (accounts.length == 0)
                return null;

            Account account = accounts[0];
            AccountManagerFuture<Bundle> accountFuture = accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, null, true, null, handler);
            Bundle result = accountFuture.getResult();
            return result.getString(AccountManager.KEY_AUTHTOKEN);
        } catch (OperationCanceledException | AuthenticatorException e) {
            return null;
        }
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        if (accountType == null || !accountType.equals(ACCOUNT_TYPE)) {
            response.onError(0, "Account type is not supported");
            return null;
        }

        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Bundle result = new Bundle();

        if (!authTokenType.equals(AUTH_TOKEN_TYPE)) {
            result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_REQUEST);
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "Wrong auth token type");
        }

        AccountManager accountManager = AccountManager.get(context);
        String username = account.name;
        String password = accountManager.getPassword(account);
        if (username != null && password != null) {
            try {
                String combinedString = username + ":" + password;
                String authString = Base64.encodeToString(combinedString.getBytes(), Base64.NO_WRAP);
                Response apiResponse = ApiService.getInstance(context).login(authString).execute();

                if (apiResponse.isSuccessful()) {
                    accountManager.setAuthToken(account, AUTH_TOKEN_TYPE, authString);

                    result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                    result.putString(AccountManager.KEY_AUTHTOKEN, authString);
                    accountManager.setUserData(account, ACCESS_TOKEN, authString);
                    return result;
                }
            } catch (IOException e) {
                accountManager.removeAccountExplicitly(account);
                result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_NETWORK_ERROR);
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "Invalid username or password");
            }
        }

        result.putInt(AccountManager.KEY_ERROR_CODE, AccountManager.ERROR_CODE_BAD_AUTHENTICATION);
        result.putString(AccountManager.KEY_ERROR_MESSAGE, "Missing username or password");
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return "LiSS";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }
}
