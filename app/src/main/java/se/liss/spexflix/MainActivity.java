package se.liss.spexflix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.mediarouter.app.MediaRouteButton;

import android.accounts.Account;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;

import se.liss.spexflix.account.SpexflixAccountManager;
import se.liss.spexflix.cast.CastLoader;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.showDetails.PlayerFragment;
import se.liss.spexflix.showPicker.ShowPickerFragment;

public class MainActivity extends AppCompatActivity implements MainListener {
    private SpexflixAccountManager accountManager;

    private Fragment currentFragment;
    private View fragmentContainer;

    private CastContext castContext;
    private MediaRouteButton castButton;

    private DrawerLayout rootView;

    private View toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        accountManager = SpexflixAccountManager.getInstance(this);
        accountManager.getCurrentAccount().observe(this, this::checkLogin);

        rootView = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar_layout);
        fragmentContainer = findViewById(R.id.main_fragment);

        View hamburgerButton = findViewById(R.id.hamburger_button);
        hamburgerButton.setOnClickListener(v -> toggleDrawer());

        View signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(v -> {
            accountManager.removeCurrentAccount();
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
                ((ShowPickerFragment)currentFragment).setListener(this);

            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment, currentFragment);
            fragmentTransaction.commit();
        }


        //toolbar = findViewById(R.id.main_toolbar);
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

    public void onSaveInstanceState(@NonNull Bundle outState){
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

    private void checkLogin(Account account) {
        if (account == null) {
            accountManager.addAccount(this);
        } else if (currentFragment == null) {
            showMainFragment();
        } else if (currentFragment instanceof ShowPickerFragment) {
            ((ShowPickerFragment)currentFragment).updateData(true);
        }
    }

    private void showMainFragment() {
        FragmentManager manager = getSupportFragmentManager();
        currentFragment = new ShowPickerFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        ((ShowPickerFragment)currentFragment).setListener(this);
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
