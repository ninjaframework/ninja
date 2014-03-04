package ninja.jetbrick.template;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

@Singleton
public class JetExceptionHandler {
    private final NinjaProperties ninjaProperties;
    private final Logger logger;

    @Inject
    public JetExceptionHandler(NinjaProperties ninjaProperties, Logger logger) {
        this.ninjaProperties = ninjaProperties;
        this.logger = logger;
    }

    public void handleException(Exception e, String response, ResponseStreams outStream) {
        Writer out = null;
        try {
            out = outStream.getWriter();
            PrintWriter pw = (out instanceof PrintWriter) ? (PrintWriter) out : new PrintWriter(out);

            if (!ninjaProperties.isDev()) {
                if (response == null) {
                    response = "Server Error";
                } // prod mode


                pw.println(response);
                logger.error("Templating error. This should not happen in production : {}", e);

            } // prod
            else {
                // dev
                // print out full stacktrace if we are in test or dev mode
                pw.println("<!-- Rythm Template ERROR MESSAGE STARTS HERE -->"
                        + "<script language=javascript>//\"></script>"
                        + "<script language=javascript>//\'></script>"
                        + "<script language=javascript>//\"></script>"
                        + "<script language=javascript>//\'></script>"
                        + "</title></xmp></script></noscript></style></object>"
                        + "</head></pre></table>"
                        + "</form></table></table></table></a></u></i></b>"
                        + "<div align=left "
                        + "style='background-color:#FFFF00; color:#FF0000; "
                        + "display:block; border-top:double; padding:2pt; "
                        + "font-size:medium; font-family:Arial,sans-serif; "
                        + "font-style: normal; font-variant: normal; "
                        + "font-weight: normal; text-decoration: none; "
                        + "text-transform: none'>"
                        + "<b style='font-size:medium'>Rythm template error!</b>"
                        + "<pre><xmp>");
                e.printStackTrace(pw);
                pw.println("</xmp></pre></div></html>");
                logger.error("Templating error. {}", e);

                pw.flush();
                pw.close();
            } // dev mode
        } catch (IOException e1) {
            logger.error("Error while handling error. {}", e1);
        }


    }

}
