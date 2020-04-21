package se.liss.spexflix.data;

public class ShowData {
    private final Integer year;
    private final String title;
    private final String alternateTitle;
    private final String posterUrl;
    private final String videoUrl;
    private final String subtitleUrl;
    private final String runtime;
    private final String info;

    public ShowData(String title, String alternateTitle, Integer year, String posterUrl, String videoUrl, String subtitleUrl, String runtime, String info) {
        this.year = year;
        this.title = title;
        this.alternateTitle = alternateTitle;
        this.posterUrl = posterUrl;
        this.videoUrl = videoUrl;
        this.subtitleUrl = subtitleUrl;
        this.runtime = runtime;
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public String getAlternateTitle() {
        return alternateTitle;
    }

    public Integer getYear() {
        return year;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getSubtitleUrl() {
        return subtitleUrl;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getInfo() {
        return info;
    }
}
