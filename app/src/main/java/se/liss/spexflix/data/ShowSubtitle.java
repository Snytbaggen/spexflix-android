package se.liss.spexflix.data;

import com.google.gson.annotations.SerializedName;

public class ShowSubtitle {
    private final String name;
    @SerializedName("subtitle_file")
    private final String subtitleFile;

    public ShowSubtitle(String name, String subtitleFile) {
        this.name = name;
        this.subtitleFile = subtitleFile;
    }

    public String getName() {
        return name;
    }

    public String getSubtitleFile() {
        return subtitleFile;
    }
}
