package se.liss.spexflix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.mediarouter.app.MediaRouteButton;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.exoplayer2.Player;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.material.navigation.NavigationView;

import se.liss.spexflix.account.LoginActivity;
import se.liss.spexflix.account.SpexflixAccountManager;
import se.liss.spexflix.cast.CastLoader;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.showDetails.PlayerFragment;
import se.liss.spexflix.showPicker.ShowPickerFragment;

public class MainActivity extends AppCompatActivity implements MainListener {
    private SpexflixAccountManager accountManager;

    private Fragment currentFragment;

    private CastContext castContext;
    private MediaRouteButton castButton;

    private DrawerLayout drawer;

    //private View toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountManager = SpexflixAccountManager.getInstance(this);
        accountManager.getCurrentAccount().observe(this, this::checkLogin);

        drawer = findViewById(R.id.drawer_layout);
        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayShowCustomEnabled(true);
        toolbar.setBackgroundDrawable(getDrawable(R.drawable.toolbar_background));
        toolbar.setCustomView(R.layout.custom_toolbar);

        View hamburgerButton = findViewById(R.id.hamburger_button);
        hamburgerButton.setOnClickListener(v -> toggleDrawer());

        View signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(v -> {
            accountManager.removeCurrentAccount();
            closeDrawer();
        });


        drawer.closeDrawer(GravityCompat.START);

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

    public void onSaveInstanceState(@NonNull Bundle outState){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        if (fragment != null)
            getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCardClicked(ShowData showData) {
        changeToPlayerFragment(showData, false, false, PlayerFragment.NO_ID);

    }

    @Override
    public void onPlayClicked(ShowData showData) {
        if (castContext.getCastState() == CastState.CONNECTED) {
            CastSession session = castContext.getSessionManager().getCurrentCastSession();
            if (session != null) {
                CastLoader.castMedia(session, this, showData);
                return;
            }
        }

        changeToPlayerFragment(showData, true, true, PlayerFragment.NO_ID);

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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawer.requestLayout();

        /*if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && currentFragment instanceof PlayerFragment)
            toolbar.setVisibility(View.GONE);
        else
            toolbar.setVisibility(View.VISIBLE);*/
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
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
        drawer.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
    }

    private void toggleDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            openDrawer();
    }
}
