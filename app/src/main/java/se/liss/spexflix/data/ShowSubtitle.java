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
        // This hack is done to have different authentication types in Apache, they point to the same
        // locations but with different settings. Should be fixed in a better way, it's not good to
        // depend on server configuration in the client.
        if (subtitleFile != null) {
            return subtitleFile.replaceFirst("/uploads/", "/oauth20/uploads/");
        }
        return subtitleFile;
    }
}
