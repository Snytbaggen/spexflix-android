package se.liss.spexflix.videoCard;

import android.view.View;

import se.liss.spexflix.data.ShowData;

public interface CardClickListener {
    void onCardClicked(View v);
    void onCardPlayClicked(View v);
}
