package org.sen;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class ReadingTILCrawler implements TILCrawler {

    private static final String READING_GITHUB_URL = "https://github.com/dahyen0o/development-books/commits/main";

    @Override
    public Map<String, String> doCrawling(final LocalDate tilDate) throws IOException {
        final Map<String, String> commits = new HashMap<>();
        getCommitsBy(tilDate).forEach(commit ->
                commits.put(commit.text(), "https://github.com" + commit.attr("href"))
        );
        return commits;
    }

    private static List<Element> getCommitsBy(final LocalDate createdDate) throws IOException {
        return Jsoup.connect(READING_GITHUB_URL).get()
                .getElementsByAttributeValueStarting("href", "/dahyen0o/development-books/commit/")
                .stream()
                .filter(element -> element.text().startsWith(getCommitPrefix(createdDate)))
                .toList();
    }

    private static String getCommitPrefix(final LocalDate createdDate) {
        return String.format("[%s]", createdDate.format(DateTimeFormatter.ofPattern("yyMMdd")));
    }
}
