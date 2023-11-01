package org.sen;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BlogTILCrawler implements TILCrawler {

    private static final String BLOG_URL = "https://sechoi.tistory.com";

    @Override
    public Map<String, String> doCrawling(final LocalDate tilDate) throws IOException {
        final Map<String, String> posts = new HashMap<>();
        final int lastPostNumber = getLastPostNumber();
        for (int postNumber = lastPostNumber; postNumber >= 1; postNumber--) {
            final String postUrl = BLOG_URL + "/" + postNumber;
            if (scrapPost(postUrl).isEmpty()) {
                continue;
            }

            final Document post = scrapPost(postUrl).get();
            final LocalDateTime createdTime = LocalDateTime.parse(getCreatedTime(post));
            final LocalDate postCreatedDate = createdTime.toLocalDate();
            if (postCreatedDate.isEqual(tilDate)) {
                posts.put(post.title(), postUrl);
            }
            else if (postCreatedDate.isBefore(tilDate)) {
                break;
            }
        }
        return posts;
    }

    private Optional<Document> scrapPost(final String url) {
        try {
            return Optional.of(Jsoup.connect(url).get());
        } catch (final IOException e) {
            return Optional.empty();
        }
    }

    private String getCreatedTime(final Document post) {
        return post.head()
                .getElementsByAttributeValue("property", "article:published_time")
                .first()
                .attr("content")
                .substring(0, 19);
    }

    private int getLastPostNumber() throws IOException {
        final Document mainPage = scrapPost(BLOG_URL).orElseThrow();
        return Integer.parseInt(
                getLastPostPath(mainPage).substring(1)
        );
    }

    private String getLastPostPath(final Document mainPage) {
        return getPostsBy(mainPage)
                .get(0)
                .getElementsByAttribute("href")
                .attr("href");
    }

    private Elements getPostsBy(final Document page) {
        return page.body()
                .getElementsByClass("article-content");
    }
}
