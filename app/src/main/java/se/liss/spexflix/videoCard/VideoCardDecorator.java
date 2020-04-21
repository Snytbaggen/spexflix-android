package se.liss.spexflix.videoCard;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import se.liss.spexflix.R;

public class VideoCardDecorator extends RecyclerView.ItemDecoration {
    private int bottomMargin;
    private int topMargin;

    public VideoCardDecorator(Context context) {
        bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.video_card_bottom_margin);
        topMargin = context.getResources().getDimensionPixelSize(R.dimen.video_card_top_margin);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = topMargin;
        else
            outRect.top = 0;

        outRect.bottom = bottomMargin;
    }
}
