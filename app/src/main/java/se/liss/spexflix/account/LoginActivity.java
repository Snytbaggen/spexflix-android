package se.liss.spexflix.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.model.LazyHeaders;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.liss.spexflix.R;
import se.liss.spexflix.data.ApiInterface;
import se.liss.spexflix.data.ApiService;
import se.liss.spexflix.data.TokenInterceptor;

import static se.liss.spexflix.account.SpexflixAccountAuthenticator.ACCOUNT_TYPE;
import static se.liss.spexflix.account.SpexflixAccountAuthenticator.AUTH_TOKEN_TYPE;

public class LoginActivity extends AccountAuthenticatorActivity implements View.OnClickListener {
    ApiInterface apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        View loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://spexflix.studentspex.se/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    @Override
    public void onClick(View v) {
        EditText username = findViewById(R.id.login_username_text);
        EditText password = findViewById(R.id.login_password_text);

        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();

        if (usernameString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(this, "Du måste skriva in namn och lösenord", Toast.LENGTH_SHORT).show();
            return;
        }

        String stringToEncode = usernameString + ":" + passwordString;
        String encodedString = Base64.encodeToString(stringToEncode.getBytes(), Base64.NO_WRAP);

        apiInterface.login("Basic " + encodedString).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful())
                    createAccount(usernameString, passwordString, encodedString);
                else
                    Toast.makeText(LoginActivity.this, "Fel användarnamn eller lösenord", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Något gick fel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount(String username, String password, String authToken) {
        final Account account = new Account(username, ACCOUNT_TYPE);
        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);

        AccountManager accountManager = AccountManager.get(this);
        accountManager.addAccountExplicitly(account, password, data);
        accountManager.setAuthToken(account, AUTH_TOKEN_TYPE, authToken);

        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);

        finish();
    }
}
