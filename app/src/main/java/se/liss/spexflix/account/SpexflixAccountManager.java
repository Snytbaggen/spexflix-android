package se.liss.spexflix.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SpexflixAccountManager {
    private static SpexflixAccountManager instance;

    private AccountManager accountManager;

    private MutableLiveData<Account> account;
    private Handler handler;

    public static synchronized SpexflixAccountManager getInstance(Context context) {
        if (instance == null)
            instance = new SpexflixAccountManager(context);
        return instance;
    }

    SpexflixAccountManager(Context context) {
        accountManager = AccountManager.get(context);
        account = new MutableLiveData<>();
        handler = new Handler();
        accountManager.addOnAccountsUpdatedListener(this::accountsUpdated, handler, false);
        accountsUpdated(accountManager.getAccountsByType(SpexflixAccountAuthenticator.ACCOUNT_TYPE));
    }

    private void accountsUpdated(Account[] accounts) {
        Account newAccount = null;
        if (accounts != null) {
            for (Account account : accounts) {
                if (account.type.equals(SpexflixAccountAuthenticator.ACCOUNT_TYPE)) {
                    newAccount = account;
                }
            }
        }

        account.setValue(newAccount);
    }

    public LiveData<Account> getCurrentAccount() {
        return account;
    }

    public void addAccount(Activity launchActivity) {
        accountManager.addAccount(SpexflixAccountAuthenticator.ACCOUNT_TYPE, SpexflixAccountAuthenticator.AUTH_TOKEN_TYPE, null, null, launchActivity, null, handler);
    }

    public void removeCurrentAccount() {
        removeAccount(account.getValue());
    }

    public void removeAccount(Account account) {
        accountManager.removeAccountExplicitly(account);
    }
}
