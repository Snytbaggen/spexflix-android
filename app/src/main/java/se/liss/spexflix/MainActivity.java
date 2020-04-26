package se.liss.spexflix;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.mediarouter.app.MediaRouteButton;

import android.accounts.Account;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;

import se.liss.spexflix.account.SpexflixAccountManager;
import se.liss.spexflix.cast.CastLoader;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.showDetails.ShowDetailsFragment;
import se.liss.spexflix.showPicker.ShowPickerFragment;

public class MainActivity extends FragmentActivity implements MainListener {
    private SpexflixAccountManager accountManager;

    private Fragment currentFragment;

    private CastContext castContext;
    private MediaRouteButton castButton;

    private View toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        castContext = CastContext.getSharedInstance(this);
        castButton = findViewById(R.id.cast_button);

        CastButtonFactory.setUpMediaRouteButton(this, castButton);

        if (currentFragment == null)
            currentFragment = new ShowPickerFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ((ShowPickerFragment)currentFragment).setListener(this);
        transaction.replace(R.id.main_fragment, currentFragment);
        transaction.commit();

        accountManager = SpexflixAccountManager.getInstance(this);
        accountManager.getCurrentAccount().observe(this, this::checkLogin);



        toolbar = findViewById(R.id.main_toolbar);
    }

    @Override
    public void onCardClicked(ShowData showData) {
        ShowDetailsFragment newFragment = new ShowDetailsFragment();
        newFragment.setData(showData);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        currentFragment = newFragment;

    }

    @Override
    public void onPlayClicked(ShowData showData) {
        if (castContext.getCastState() == CastState.CONNECTED) {
            CastSession session = castContext.getSessionManager().getCurrentCastSession();
            if (session != null)
                CastLoader.castMedia(session, this, showData);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && currentFragment instanceof ShowDetailsFragment)
            toolbar.setVisibility(View.GONE);
        else
            toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if (!manager.popBackStackImmediate())
            super.onBackPressed();
    }

    private void checkLogin(Account account) {
        if (account == null) {
            accountManager.addAccount(this);
        } else if (currentFragment instanceof ShowPickerFragment) {
            ((ShowPickerFragment)currentFragment).updateData();
        }
    }
}
