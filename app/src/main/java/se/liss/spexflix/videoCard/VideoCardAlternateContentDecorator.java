package se.liss.spexflix.videoCard;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import se.liss.spexflix.R;

public class VideoCardAlternateContentDecorator extends RecyclerView.ItemDecoration {
    private int verticalMargin;

    public VideoCardAlternateContentDecorator(Context context) {
        verticalMargin = context.getResources().getDimensionPixelSize(R.dimen.video_extra_card_vertical_margin);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != 0)
            outRect.top = verticalMargin;
        else
            outRect.top = 0;
    }
}
