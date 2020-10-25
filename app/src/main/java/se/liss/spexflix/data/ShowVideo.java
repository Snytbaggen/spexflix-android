package se.liss.spexflix.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShowVideo {
    private final String title;
    @SerializedName("video_file")
    private final String videoFile;
    private final String information;
    @SerializedName("video_type")
    private final ShowType videoType;
    private final List<ShowSubtitle> subtitles; //TODO

    public ShowVideo(String title, String videoFile, String information, ShowType videoType, List<ShowSubtitle> subtitles) {
        this.title = title;
        this.videoFile = videoFile;
        this.information = information;
        this.videoType = videoType;
        this.subtitles = subtitles;
    }

    public String getVideoFile() {
        // This hack is done to have different authentication types in Apache, they point to the same
        // locations but with different settings. Should be fixed in a better way, it's not good to
        // depend on server configuration in the client.
        if (videoFile != null) {
            return videoFile.replace("/uploads/", "/oauth20/uploads/");
        }
        return videoFile;
    }

    public String getInformation() {
        return information;
    }

    public ShowType getVideoType() {
        return videoType;
    }

    public List<ShowSubtitle> getSubtitles() {
        return subtitles;
    }

    public String getTitle() {
        return title;
    }
}
