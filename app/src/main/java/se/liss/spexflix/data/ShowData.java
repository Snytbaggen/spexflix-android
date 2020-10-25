package se.liss.spexflix.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShowData {
    private final Integer id;
    private final Integer year;
    @SerializedName("short_name")
    private final String shortName;
    private final String title;
    private final String subtitle;
    @SerializedName("poster_image")
    private final String posterUrl;
    private final String information;
    private final List<ShowVideo> videos;

    public ShowData(Integer id, Integer year, String shortName, String title, String subtitle, String posterUrl, String information, List<ShowVideo> videos) {
        this.id = id;
        this.year = year;
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

    public Integer getYear() {
        return year;
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
        // This hack is done to have different authentication types in Apache, they point to the same
        // locations but with different settings. Should be fixed in a better way, it's not good to
        // depend on server configuration in the client.
        if (posterUrl != null) {
            return posterUrl.replaceFirst("/uploads/", "/oauth20/uploads/");
        }
        return posterUrl;
    }

    public String getInformation() {
        return information;
    }

    public List<ShowVideo> getVideos() {
        return videos;
    }

    public ShowVideo getVideo(int position) {
        if (videos == null || videos.isEmpty() || position < 0 || position >= videos.size())
            return null;

        return videos.get(position);
    }
}
