import java.util.ArrayList;

// This class represents the robots.txt file for a given domain
public class RobotsTXT {
    private String domain;
    private ArrayList<String> disallowedPaths; // Excluded paths
    private ArrayList<String> allowedPaths; // Exceptions to the excluded paths
    private int crawlDelay; // in seconds

    public RobotsTXT(String domain) {
        this.domain = domain;
        disallowedPaths = new ArrayList<String>();
        allowedPaths = new ArrayList<String>();
        crawlDelay = 1; // Default crawl delay is 1 second
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getDomain() {
        return domain;
    }

    public void addDisallowedPath(String path) {
        disallowedPaths.add(path);
    }
    public ArrayList<String> getDisallowedPaths() {
        return disallowedPaths;
    }

    public void addAllowedPath(String path) {
        allowedPaths.add(path);
    }
    public ArrayList<String> getAllowedPaths() {
        return allowedPaths;
    }

    public void setCrawlDelay(int delay) {
        crawlDelay = delay;
    }
    public int getCrawlDelay() {
        return crawlDelay;
    }
}
