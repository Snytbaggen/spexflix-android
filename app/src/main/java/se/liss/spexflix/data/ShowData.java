package se.liss.spexflix.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShowData {
    private final Integer id;
    @SerializedName("short_name")
    private final String shortName;
    private final String title;
    private final String subtitle;
    @SerializedName("poster_image")
    private final String posterUrl;
    private final String information;
    private final List<ShowVideo> videos;

    public ShowData(Integer id, String shortName, String title, String subtitle, String posterUrl, String information, List<ShowVideo> videos) {
        this.id = id;
        this.shortName = shortName;
        this.title = title;
        this.subtitle = subtitle;
        this.posterUrl = posterUrl;
        this.information = information;
        this.videos = videos;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getShortName() {
        return shortName;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getInformation() {
        return information;
    }

    public List<ShowVideo> getVideos() {
        return videos;
    }
}
