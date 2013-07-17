package ninja.utils;

/**
 * This is a marker class used to handle Results in {@link ResultHandler}.
 * 
 * It causes the ResultHandler to render no body, just the header. Useful
 * when issuing a redirect and no corresponding content should be shown.
 * 
 * @author ra
 *
 */
public class NoHttpBody {
    // intentionally left empty. Just a marker class.
}
