package se.liss.spexflix;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import se.liss.spexflix.data.ShowData;
import se.liss.spexflix.showDetails.ShowDetailsFragment;
import se.liss.spexflix.showPicker.ShowPickerFragment;

public class MainActivity extends FragmentActivity implements MainListener {

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ShowPickerFragment newFragment = new ShowPickerFragment();
        newFragment.setListener(this);
        transaction.replace(R.id.main_fragment, newFragment);
        transaction.commit();
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
}
