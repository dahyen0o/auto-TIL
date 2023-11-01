package org.sen;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public interface TILCrawler {

    Map<String, String> doCrawling(final LocalDate tilDate) throws IOException;
}
