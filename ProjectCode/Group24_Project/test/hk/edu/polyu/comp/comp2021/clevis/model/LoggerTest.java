package hk.edu.polyu.comp.comp2021.clevis.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    @TempDir
    Path tempDir;

    @Test
    void testInvalidPath_throwsIOException() {
        assertThrows(IOException.class, () -> {
            try (Logger ignored = new Logger("invalid?path.html",
                                             tempDir.resolve("log.txt").toString())) {

            }
        });
    }

    @Test
    void testIoFailureOnHtmlFile_throwsIOException() {
        File htmlAsDir = tempDir.resolve("log.html").toFile();
        htmlAsDir.mkdir();
        assertThrows(IOException.class, () -> {
            try (Logger ignored = new Logger(htmlAsDir.getAbsolutePath(),
                                             tempDir.resolve("log.txt").toString())) {

            }
        });
    }

    @Test
    void testNormalLifecycle_writesHtmlAndTxt_andClose() throws IOException {
        String html = tempDir.resolve("ok.html").toString();
        String txt  = tempDir.resolve("ok.txt").toString();
        try (Logger logger = new Logger(html, txt)) {
            logger.logCommand("rectangle R 0 0 2 2");
            logger.logCommand("quit");
        }
        String htmlContent = Files.readString(tempDir.resolve("ok.html"));
        String txtContent  = Files.readString(tempDir.resolve("ok.txt"));
        assertFalse(htmlContent.isBlank(), "HTML is empty");
        assertTrue(htmlContent.contains("<tr>"), "HTML should contain at least one line");
        assertTrue(htmlContent.toLowerCase().contains("</table>"), "should contain closed label");
        assertTrue(htmlContent.contains("rectangle R 0 0 2 2"));
        assertTrue(txtContent.contains("rectangle R 0 0 2 2"));
        assertTrue(txtContent.contains("quit"));
    }
}