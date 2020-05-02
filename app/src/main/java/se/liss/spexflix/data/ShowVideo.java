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
