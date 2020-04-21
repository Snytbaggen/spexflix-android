package se.liss.spexflix.showDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import se.liss.spexflix.R;
import se.liss.spexflix.data.ShowData;

public class ShowDetailsFragment extends Fragment {
    private ShowData data;

    private TextView title;
    private TextView alternateTitle;
    private TextView year;
    private TextView info;

    public void setData(ShowData data) {
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.show_detail_fragment, container, false);
        title = v.findViewById(R.id.show_detail_title);
        alternateTitle = v.findViewById(R.id.show_detail_alternate_title);
        year = v.findViewById(R.id.show_detail_year);
        info = v.findViewById(R.id.show_detail_info);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (data != null) {
            title.setText(data.getTitle());

            alternateTitle.setText(data.getSubtitle());

            year.setText(data.getShortName());

            info.setText(data.getInformation());
        }
    }
}
