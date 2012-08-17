package ninja.template;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;

import freemarker.cache.TemplateLoader;

/**
 * This class html-escapes variables like ${...} in all templates (ftl.html). If
 * you don't want to have something escaped you can use at your own risk:
 * <p>
 * 
 * <code>
 * <#noescape>${...}</#noescape>
 * </code>
 * 
 * (inside your template).
 * <p>
 * See also http://freemarker.org/docs/ref_directive_escape.html
 * 
 */
public class TemplateEngineFreemarkerEscapedLoader implements TemplateLoader {

    public static final String ESCAPE_PREFIX = "[#ftl strip_whitespace=true][#escape x as x?html]";
    public static final String ESCAPE_SUFFIX = "[/#escape]";

    private final TemplateLoader delegate;

    public TemplateEngineFreemarkerEscapedLoader(TemplateLoader delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        return delegate.findTemplateSource(name);
    }

    @Override
    public long getLastModified(Object templateSource) {
        return delegate.getLastModified(templateSource);
    }

    @Override
    public Reader getReader(Object templateSource, String encoding)
            throws IOException {
        Reader reader = delegate.getReader(templateSource, encoding);
        try {
            String templateText = IOUtils.toString(reader);
            return new StringReader(ESCAPE_PREFIX + templateText
                    + ESCAPE_SUFFIX);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        delegate.closeTemplateSource(templateSource);
    }
}