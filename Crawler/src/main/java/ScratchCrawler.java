import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScratchCrawler {
    public static final int MAX_PAGES = 100; // Maximum pages to crawl
    public static long waitTime = 200; // Time to wait between requests in milliseconds

    // Using a HashSet to store visited pages and pages to visit. This is the best data structure 
    // for this use case because it has O(1) time complexity for add, remove, and contains 
    // operations and enforces uniqueness (we do not want to crawl pages more than once). 
    // Also we do not care about the order of the pages.
    public Set<String> pagesVisited = new HashSet<String>(); // Set to store visited pages
    public Set<String> pagesToVisit = new HashSet<String>(); // Set to store pages to visit
    public Set<String> disallowedDomains = new HashSet<String>(); // Set to store disallowed domains

    // In order to store the robots.txt restrictions, we are going to use a HashMap with the domain 
    // as the key and an object representing the restrictions as the value. This is the best data
    // structure for this use case because it allows us to quickly look up the restrictions for a
    // given domain.
    public static HashMap<String, RobotsTXT> visitedRobotsTXTs = new HashMap<String, RobotsTXT>(); // Map to store robots.txt restrictions

    public String getNextPage() {
        String nextPage = null; // Initialize nextPage to null
        Iterator<String> it = pagesToVisit.iterator(); // Create an iterator for pagesToVisit
        
        if (!pagesToVisit.isEmpty()) { // If there are pages to visit
            nextPage = it.next(); // Get the next page
            pagesToVisit.remove(nextPage); // Remove the page from pagesToVisit
            pagesVisited.add(nextPage); // Add the page to pagesVisited
        } else {
            System.out.println("No more pages to visit."); // Print message
        }

        return nextPage; // Return the next page
    }

    // https://docs.oracle.com/javase/tutorial/networking/urls/index.html
    public void getPage(String url) {
        // Code to get the page
        System.out.println("Getting page: " + url); // Print message

        if (url.endsWith(")")) {
            url = url.substring(0, url.length() - 1);
        }
        
        try {
            URL pageURL = new URL(url); // Create a new URL object
            String domain = extractDomain(url); // Extract the domain from the URL

            HttpURLConnection connection = (HttpURLConnection) pageURL.openConnection();
            // Set the User-Agent header
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Error reading page. Response code: " + responseCode);

                // If the page is not found, add the domain to the disallowedDomains set
                disallowedDomains.add(domain);

                if (Debug.DEBUG)
                    System.out.println("Adding domain to disallowedDomains: " + domain); // Print message

                return;
            }
            
            // Check if url is allowed by robots.txt
            if (isInVisitedRobotsTxt(domain)) { // If the URL is in the visited robots.txt
                RobotsTXT robotsTXT = visitedRobotsTXTs.get(domain); // Get the RobotsTXT object for the URL
                if (robotsTXT.getDisallowedPaths().contains(url) || disallowedDomains.contains(domain)) { // If the URL is disallowed
                    System.out.println("URL is disallowed"); // Print message
                    return; // Exit the method
                } else {
                    // URL is allowed
                    // Update the wait time based on the crawl delay
                    waitTime = robotsTXT.getCrawlDelay() * 1000; // Update the wait time based on the crawl delay                       
                }
            } else {
                parseRobotsTXT(url); // Parse the robots.txt file

                // 
            }

            // Update the wait time based on the crawl delay
            if (visitedRobotsTXTs.containsKey(url)) { // If the URL is in the visited robots.txt
                RobotsTXT robotsTXT = visitedRobotsTXTs.get(url); // Get the RobotsTXT object for the URL
                waitTime = robotsTXT.getCrawlDelay() * 1000; // Update the wait time based on the crawl delay
            }
            
            // Code to read the page
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageURL.openStream())); // Create a new BufferReader object
            PrintWriter writer = new PrintWriter(new FileWriter("crawledData.txt",true)); // Create a new PrintWriter object
            
            String line; // Declare a string to store each line of the page
            StringBuilder pageContent = new StringBuilder(); // To store the page content

            
            while ((line = reader.readLine()) != null && pageContent.length() < 1024) { // While there are lines to read
                pageContent.append(line); // Add the line to the page content
                
                // Extract links from the page
                List<String> links = RegexParser.extractLinks(line); // Extract the links from the line
                for (String link : links) { // For each link
                    if (!pagesVisited.contains(link)) { // If the link has not been visited
                        pagesToVisit.add(link); // Add the link to pagesToVisit
                    }
                }
            }
            // Write the page content to the file, up to 1KB
            writer.println(pageContent.toString().substring(0, Math.min(1024, pageContent.length())));      

            writer.close(); // Close the writer
            reader.close(); // Close the reader

        } catch (MalformedURLException e) {
            System.out.println("Error creating URL object.");
            e.printStackTrace();
            return; // Exit the method
        } catch (IOException e) {
            System.out.println("Error reading page.");
            e.printStackTrace();
            return; // Exit the method
        } 

    }

    public static void parseRobotsTXT(String url) {
        // Code to parse robots.txt
        if (Debug.DEBUG)
            System.out.println("Parsing robots.txt for: " + url); // Print message

        // Extract the domain from the URL
        String domain = extractDomain(url);

        // Create the RobotsTXT object
        url = domain + "/robots.txt"; // Append /robots.txt to the domain

        RobotsTXT robotsTXT = new RobotsTXT(url); // Create a new RobotsTXT object
        if (Debug.DEBUG)
            System.out.println("RobotsTXT object created for: " + url); // Print message

        // Fetch the robots.txt file
        try {
            if (Debug.DEBUG)
                System.out.println("Fetching robots.txt file for: " + url); // Print message

            URL pageURL = new URL(url); // Create a new URL object
            
            // Code to read the page
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageURL.openStream())); // Create a new BufferReader object
            //PrintWriter writer = new PrintWriter("src/main/resources/crawledData.txt"); // Create a new PrintWriter object
            String line; // Declare a string to store each line of the page
            while ((line = reader.readLine()) != null) { // While there are lines to read
                //writer.println(line); // Write the line to the file
                // if (Debug.DEBUG)
                //     System.out.println(line); // Print the line
                
                if (line.startsWith("User-agent: *")) {
                    // Read lines until next user agent
                    while ((line = reader.readLine()) != null && !line.startsWith("User-agent:")) {
                        if (line.startsWith("Disallow: ")) {
                            // parse Disallow
                            robotsTXT.addDisallowedPath(line.substring(10)); // Add the disallowed path to the RobotsTXT object 
                            
                            if (Debug.DEBUG_RobotsTXT)
                                System.out.println(line); // Print message
                        } else if (line.startsWith("Allow: ")) {
                            // parse Allow
                            robotsTXT.addAllowedPath(line.substring(7)); // Add the allowed path to the RobotsTXT object
                            
                            if (Debug.DEBUG_RobotsTXT)
                                System.out.println(line); // Print message
                        } else if (line.startsWith("Crawl-delay: ")) {
                            // parse Crawl-delay
                            int delay = Integer.parseInt(line.substring(13)); // Parse the crawl delay
                            robotsTXT.setCrawlDelay(delay); // Set the crawl delay

                            if (Debug.DEBUG_RobotsTXT)
                                System.out.println(line); // Print message
                        }
                    }
                }
            }   

            reader.close(); // Close the reader
        } catch (MalformedURLException e) {
            System.out.println("Error creating URL object for robots.txt file.");
            e.printStackTrace();
            return; // Exit the method
        } catch (IOException e) {
            System.out.println("Error fetching robots.txt file.");
            e.printStackTrace();
            return; // Exit the method
        }

        // Store the RobotsTXT object in the visitedRobotsTXTs map
        visitedRobotsTXTs.put(domain, robotsTXT); // Add the RobotsTXT object to the map
    }

    public static boolean isInVisitedRobotsTxt(String url) {
        // Code to check if URL is in visited robots.txt
        if (Debug.DEBUG)
            System.out.println("Checking if URL is in visited robots.txt: " + url); // Print message

        // Extract the domain from the URL
        String domain = url; // Set the domain to the URL for now
        Pattern pattern = Pattern.compile("((http://|https://)?[^:/]+)"); // Create a pattern to match the domain
        Matcher matcher = pattern.matcher(url); // Create a matcher for the pattern
        if (matcher.find()) {
            domain = matcher.group(1); // Obtain the domain and TLD, including the protocol
            if (Debug.DEBUG)
                System.out.println("Domain: " + domain); // Print the domain
        } else {
            System.out.println("Error extracting domain from URL.");
            return false; // Exit the method
        }

        // Check if the domain is in the visitedRobotsTXTs map
        if (visitedRobotsTXTs.containsKey(domain)) { // If the domain is in the map
            if (Debug.DEBUG)
                System.out.println("Domain is in visited robots.txt: " + domain); // Print message
            return true; // Return true
        } else {
            if (Debug.DEBUG)
                System.out.println("Domain is not in visited robots.txt: " + domain); // Print message
            return false; // Return false
        }
    }

    public static boolean allowedToCrawl(String url) {
        // Code to check if allowed to crawl
        if (Debug.DEBUG)
            System.out.println("Checking if allowed to crawl: " + url); // Print message

        // Extract the domain from the URL
        String domain = extractDomain(url); // Set the domain to the URL for now

        // Check if the domain is in the visitedRobotsTXTs map
        if (visitedRobotsTXTs.containsKey(domain)) { // If the domain is in the map
            RobotsTXT robotsTXT = visitedRobotsTXTs.get(domain); // Get the RobotsTXT object for the domain
            if (robotsTXT.getDisallowedPaths().contains(url.replaceFirst(domain, ""))) { // If the URL is disallowedif (robotsTXT.getDisallowedPaths().contains(domain - url)) { // If the URL is disallowed
                if (Debug.DEBUG)
                    System.out.println("URL is disallowed by robots.txt: " + url); // Print message
                return false; // Return false
            } else {
                // URL is allowed
                return true; // Return true
            }
        } 
        else {
            parseRobotsTXT(url); // Parse the robots.txt file
            return allowedToCrawl(url); // Rerun the method
        }
    }

    public static String extractDomain(String url) {
        // Extract the domain from the URL
        String domain = url; // Set the domain to the URL for now
        Pattern pattern = Pattern.compile("((http://|https://)?[^:/]+)"); // Create a pattern to match the domain
        Matcher matcher = pattern.matcher(url); // Create a matcher for the pattern
        if (matcher.find()) {
            domain = matcher.group(1); // Obtain the domain and TLD, including the protocol
            if (Debug.DEBUG)
                System.out.println("Domain: " + domain); // Print the domain
        } else {
            System.out.println("Error extracting domain from URL.");
            return url; // Exit the method
        }

        return domain; // Return the domain
    }

    public void crawl(String seed) {
        pagesToVisit.add(seed); // Add the seed page to pagesToVisit

        while (pagesVisited.size() < MAX_PAGES && !pagesToVisit.isEmpty()) { // While the number of visited pages is less than MAX_PAGES
            String nextPage = getNextPage(); // Get the next page
            try {
                Thread.sleep(waitTime); // Wait to be polite
                getPage(nextPage); // Get the page
            } catch (InterruptedException e) {
                System.out.println("Error waiting between crawling pages.");
                e.printStackTrace();
            } // 
        }

        System.out.println("Crawling complete."); // Print message
    }
    public void crawl() {
        while (pagesVisited.size() < MAX_PAGES && !pagesToVisit.isEmpty()) { // While the number of visited pages is less than MAX_PAGES
            String nextPage = getNextPage(); // Get the next page
            try {
                Thread.sleep(waitTime); // Wait to be polite
                getPage(nextPage); // Get the page
            } catch (InterruptedException e) {
                System.out.println("Error waiting between crawling pages.");
                e.printStackTrace();
            } // 
        }

        System.out.println("Crawling complete."); // Print message
    }

    public void readURLsFromFile(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            List<String> urls = stream.collect(Collectors.toList()); // Convert the stream to a list
            for (String url : urls) { // Iterate over the list
                pagesToVisit.add(url);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        }
    }


    // For testing purposes
    public static void main(String[] args) {

        ScratchCrawler crawler = new ScratchCrawler(); // Create a new ScratchCrawler object
        //crawler.crawl("https://archive.org/details/bostonpubliclibrary"); // Start off the crawl with the seed page

        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            if ("--file".equals(args[i]) && i + 1 < args.length) {
                String filePath = args[i + 1];
                crawler.readURLsFromFile(filePath);
            }
        }

        crawler.crawl(); // Start off the crawl with the seed pages
    }
}
