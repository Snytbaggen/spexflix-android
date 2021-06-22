package se.liss.spexflix;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.mediarouter.app.MediaRouteButton;

import android.accounts.Account;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;
import com.google.gson.Gson;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import java.util.Map;

import se.liss.spexflix.account.AuthStateManager;
import se.liss.spexflix.account.LoginActivity;
import se.liss.spexflix.cast.CastLoader;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.data.VersionInformation;
import se.liss.spexflix.showDetails.PlayerFragment;
import se.liss.spexflix.showPicker.ShowPickerFragment;

public class MainActivity extends AppCompatActivity implements MainListener {

    private static final String TAG = "MainActivity";
    private Fragment currentFragment;
    private View fragmentContainer;

    private CastContext castContext;
    private MediaRouteButton castButton;

    private DrawerLayout rootView;

    private View toolbar;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private AuthStateManager mAuthStateManager;
    private AuthorizationService mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        mAuthStateManager = AuthStateManager.getInstance(this);
        mAuthService = new AuthorizationService(this);

        rootView = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar_layout);
        fragmentContainer = findViewById(R.id.main_fragment);

        View hamburgerButton = findViewById(R.id.hamburger_button);
        hamburgerButton.setOnClickListener(v -> toggleDrawer());

        View signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(v -> {
            //accountManager.removeCurrentAccount();
            // TODO: sign out
            closeDrawer();
        });

        calculateBottomPadding();

        rootView.closeDrawer(GravityCompat.START);

        castContext = CastContext.getSharedInstance(this);
        castButton = findViewById(R.id.cast_button);

        CastButtonFactory.setUpMediaRouteButton(this, castButton);

        FragmentManager manager = getSupportFragmentManager();
        currentFragment = savedInstanceState == null ? null : manager.getFragment(savedInstanceState, "currentFragment");
        if (currentFragment == null) {

            showMainFragment();
        } else {
            if (currentFragment instanceof ShowPickerFragment)
                ((ShowPickerFragment) currentFragment).setListener(this);

            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment, currentFragment);
            fragmentTransaction.commit();
        }


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful())
                        return;

                    Map<String, FirebaseRemoteConfigValue> values = mFirebaseRemoteConfig.getAll();
                    if (values.isEmpty())
                        return;

                    FirebaseRemoteConfigValue versionInformationValue = values.get("version_check");
                    if (versionInformationValue == null)
                        return;

                    String versionInformationString = versionInformationValue.asString();
                    if (versionInformationString.isEmpty())
                        return;

                    Gson gson = new Gson();
                    VersionInformation information = gson.fromJson(versionInformationString, VersionInformation.class);

                    if (information == null)
                        return;

                    if (BuildConfig.VERSION_CODE >= information.getLatestBuild())
                        return;

                    displayUpdateDialog(information.getBuildUrl(), information.getBuildMessage());
                });

        //toolbar = findViewById(R.id.main_toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthStateManager.getCurrent().isAuthorized()) {
            return;
        }

        // the stored AuthState is incomplete, so check if we are currently receiving the result of
        // the authorization flow from the browser.
        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            mAuthStateManager.updateAfterAuthorization(response, ex);
        }

        if (response != null && response.authorizationCode != null) {
            // authorization code exchange is required
            mAuthStateManager.updateAfterAuthorization(response, ex);
            exchangeAuthorizationCode(response);
        } else if (ex != null) {
            //displayNotAuthorized("Authorization flow failed: " + ex.getMessage());
        } else {
            //displayNotAuthorized("No authorization state retained - reauthorization required");
        }


    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        //displayLoading("Exchanging authorization code");
        performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                this::handleCodeExchangeResponse);
    }

    @WorkerThread
    private void handleCodeExchangeResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {

        mAuthStateManager.updateAfterTokenResponse(tokenResponse, authException);
        if (!mAuthStateManager.getCurrent().isAuthorized()) {
            final String message = "Authorization Code exchange failed"
                    + ((authException != null) ? authException.error : "");

            // WrongThread inference is incorrect for lambdas
            //noinspection WrongThread
            //runOnUiThread(() -> displayNotAuthorized(message));
        } else {
            //runOnUiThread(this::displayAuthorized);
        }
    }

    @MainThread
    private void performTokenRequest(
            TokenRequest request,
            AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = mAuthStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.d(TAG, "Token request cannot be made, client authentication for the token "
                    + "endpoint could not be constructed (%s)", ex);
            //displayNotAuthorized("Client authentication method is unsupported");
            return;
        }

        mAuthService.performTokenRequest(
                request,
                clientAuthentication,
                callback);
    }

    private void displayUpdateDialog(String url, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(message != null ? message : "Det har kommit en ny version av appen! Ladda ner den snarast för att få en bättre spexupplevelse");
        builder.setTitle("Ny version!");
        builder.setPositiveButton("Ladda ner nu", (dialog, which) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://haggmyr.se/daniel/spexflix-apk/downloadpage.html"));
            startActivity(browserIntent);
            dialog.dismiss();
        });
        builder.setNegativeButton("Kanske senare", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void calculateBottomPadding() {
        int bottomPadding = 0;
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            bottomPadding = resources.getDimensionPixelSize(resourceId);
            getWindow().setNavigationBarColor(Color.WHITE);
        }
        fragmentContainer.setPadding(0, 0, 0, bottomPadding);
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        if (fragment != null)
            getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCardClicked(ShowData showData, int videoIndex) {
        changeToPlayerFragment(showData, false, false, videoIndex);
    }

    @Override
    public void onPlayClicked(ShowData showData, int videoIndex) {
        if (castContext.getCastState() == CastState.CONNECTED) {
            CastSession session = castContext.getSessionManager().getCurrentCastSession();
            if (session != null) {
                CastLoader.castMedia(session, this, showData);
                return;
            }
        }

        changeToPlayerFragment(showData, true, true, videoIndex);

    }

    private void changeToPlayerFragment(ShowData showData, boolean enterFullscreen, boolean startPlayback, int videoIndex) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(PlayerFragment.ARG_ENTER_FULLSCREEN, enterFullscreen);
        bundle.putBoolean(PlayerFragment.ARG_START_PLAYBACK, startPlayback);
        bundle.putInt(PlayerFragment.ARG_VIDEO_INDEX, videoIndex);

        PlayerFragment newFragment = new PlayerFragment();
        newFragment.setRetainInstance(true);
        newFragment.setArguments(bundle);
        newFragment.setData(showData);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        currentFragment = newFragment;
    }

    public void enableFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        toolbar.setVisibility(View.GONE);
        //rootView.setFitsSystemWindows(false);

        fragmentContainer.setPadding(0, 0, 0, 0);
    }

    public void disableFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        toolbar.setVisibility(View.VISIBLE);
        //rootView.setFitsSystemWindows(true);

        calculateBottomPadding();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rootView.requestLayout();

        /*if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && currentFragment instanceof PlayerFragment)
            toolbar.setVisibility(View.GONE);
        else
            toolbar.setVisibility(View.VISIBLE);*/
    }

    @Override
    public void onBackPressed() {
        if (rootView.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
            return;
        }

        if (currentFragment instanceof PlayerFragment) {
            //((PlayerFragment)currentFragment).disableFullscreen();
        }

        FragmentManager manager = getSupportFragmentManager();
        if (!manager.popBackStackImmediate())
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

   /*
    private void checkLogin(Account account) {
        if (account == null) {
            accountManager.addAccount(this);
        } else if (currentFragment == null) {
            showMainFragment();
        } else if (currentFragment instanceof ShowPickerFragment) {
            ((ShowPickerFragment)currentFragment).updateData(true);
        }
    }
    */

    private void showMainFragment() {
        FragmentManager manager = getSupportFragmentManager();
        currentFragment = new ShowPickerFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        ((ShowPickerFragment) currentFragment).setListener(this);
        transaction.replace(R.id.main_fragment, currentFragment);
        transaction.commit();
    }

    private void closeDrawer() {
        rootView.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        rootView.openDrawer(GravityCompat.START);
    }

    private void toggleDrawer() {
        if (rootView.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            openDrawer();
    }
}
