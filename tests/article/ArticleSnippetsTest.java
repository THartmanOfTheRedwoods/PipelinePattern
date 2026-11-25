package article;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards THE_PIPELINE_PATTERN.md: every ```java block in the article must appear (ignoring
 * differences in whitespace) somewhere in the compiled sources under src/ or tests/. Together
 * with {@link ArticleExamplesTest} this proves that all of the article's code compiles and runs.
 */
class ArticleSnippetsTest {

    private static final Pattern JAVA_BLOCK = Pattern.compile("```java\\R(.*?)```", Pattern.DOTALL);

    private static String normalize(String code) {
        return code.replaceAll("\\s+", " ").trim();
    }

    @Test
    void everyJavaSnippetInTheArticleIsCompiledByTheProject() throws IOException {
        String article = Files.readString(Path.of("THE_PIPELINE_PATTERN.md"));

        String sources;
        try (Stream<Path> files = Stream.concat(Files.walk(Path.of("src")), Files.walk(Path.of("tests")))) {
            sources = normalize(files.filter(p -> p.toString().endsWith(".java"))
                    .map(ArticleSnippetsTest::read)
                    .collect(Collectors.joining("\n")));
        }

        List<String> snippets = new ArrayList<>();
        Matcher m = JAVA_BLOCK.matcher(article);
        while (m.find()) {
            snippets.add(m.group(1));
        }

        assertFalse(snippets.isEmpty(), "no ```java blocks found in the article");
        for (String snippet : snippets) {
            assertTrue(sources.contains(normalize(snippet)),
                    "This article snippet is not compiled anywhere in src/ or tests/:\n" + snippet);
        }
    }

    private static String read(Path p) {
        try {
            return Files.readString(p);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
