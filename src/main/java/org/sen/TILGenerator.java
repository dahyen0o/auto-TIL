package org.sen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class TILGenerator {

    private static final String ROOT_DIRECTORY = "TIL";

    public void generate() throws IOException {
        final LocalDate yesterday = getYesterday();

        final TILCrawler blogTILCrawler = new BlogTILCrawler();
        final Map<String, String> blogPosts = blogTILCrawler.doCrawling(yesterday);

        final TILCrawler readingTILCrawler = new ReadingTILCrawler();
        final Map<String, String> readingCommits = readingTILCrawler.doCrawling(yesterday);

        if (blogPosts.isEmpty() && readingCommits.isEmpty()) {
            return;
        }

        final String directoryName = ROOT_DIRECTORY + "/" + yesterday.format(DateTimeFormatter.ofPattern("yy-MM"));
        final File directory = new File(directoryName);
        directory.mkdirs();

        final String fileName = yesterday.format(DateTimeFormatter.ofPattern("dd일")).concat(".md");

        try (final FileOutputStream fileOutputStream = new FileOutputStream(directoryName + "/" + fileName)) {
            final StringBuilder stringBuilder = generateTILContent(yesterday, blogPosts, readingCommits);
            fileOutputStream.write(stringBuilder.toString().getBytes());
        }
    }

    private StringBuilder generateTILContent(final LocalDate yesterday,
                                             final Map<String, String> blogPosts,
                                             final Map<String, String> readingCommits) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("## ✅ %s TIL%n%n", yesterday.format(DateTimeFormatter.ISO_DATE)));

        stringBuilder.append(String.format("### 블로그 포스팅%n%n"));
        blogPosts.forEach((title, url) ->
                stringBuilder.append(String.format("- 📝 [%s](%s)%n%n", title, url))
        );

        stringBuilder.append(String.format("### 독서 기록%n%n"));
        readingCommits.forEach((title, url) ->
                stringBuilder.append(String.format("- 📝 [%s](%s)%n%n", title, url))
        );
        return stringBuilder;
    }

    private LocalDate getYesterday() {
        return LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1);
    }
}
