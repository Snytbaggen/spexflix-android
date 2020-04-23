package se.liss.spexflix;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;

import se.liss.spexflix.account.SpexflixAccountManager;
import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.showDetails.ShowDetailsFragment;
import se.liss.spexflix.showPicker.ShowPickerFragment;

public class MainActivity extends FragmentActivity implements MainListener {

    private Fragment fragment;
    SpexflixAccountManager accountManager;

    private ShowPickerFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        currentFragment = new ShowPickerFragment();
        currentFragment.setListener(this);
        transaction.replace(R.id.main_fragment, currentFragment);
        transaction.commit();

        accountManager = SpexflixAccountManager.getInstance(this);
        accountManager.getCurrentAccount().observe(this, this::checkLogin);
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
        } else {
            currentFragment.updateData();
        }
    }
}
