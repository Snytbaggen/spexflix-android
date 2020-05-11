package se.liss.spexflix.data;

public class VersionInformation {
    private final Integer latestBuild;
    private final String buildUrl;
    private final String buildMessage;

    public VersionInformation(Integer latestBuild, String buildUrl, String buildMessage) {
        this.latestBuild = latestBuild;
        this.buildUrl = buildUrl;
        this.buildMessage = buildMessage;
    }

    public Integer getLatestBuild() {
        return latestBuild;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public String getBuildMessage() {
        return buildMessage;
    }
}
