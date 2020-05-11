package se.liss.spexflix;

import se.liss.spexflix.data.ShowData;

public interface MainListener {
    void onCardClicked(ShowData showData, int videoPosition);
    void onPlayClicked(ShowData showData, int videoPosition);
}
