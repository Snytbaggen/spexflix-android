package se.liss.spexflix;

import se.liss.spexflix.data.ShowData;

public interface MainListener {
    void onCardClicked(ShowData showData);
    void onPlayClicked(ShowData showData);
}
