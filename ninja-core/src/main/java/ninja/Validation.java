package ninja;

/**
 * Created with IntelliJ IDEA.
 * User: jroper
 * Date: 17.07.12
 * Time: 18:38
 * To change this template use File | Settings | File Templates.
 */
public interface Validation {
    boolean hasErrors();
    boolean hasFieldError(String field);
    void addFieldError(String field, String key, Object... params);
}
