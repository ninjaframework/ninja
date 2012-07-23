package ninja.validation;

import ninja.i18n.Lang;

import javax.inject.Inject;
import java.util.*;

/**
 * Validation object
 *
 * @author James Roper
 */
public class ValidationImpl implements Validation {
    private final Lang lang;

    private final Map<String, ConstraintViolation> fieldViolations =
            new HashMap<String, ConstraintViolation>();
    private final List<ConstraintViolation> generalViolations = new ArrayList<ConstraintViolation>();

    @Inject
    public ValidationImpl(Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean hasViolations() {
        return !fieldViolations.isEmpty() || !generalViolations.isEmpty();
    }

    @Override
    public boolean hasFieldViolation(String field) {
        return fieldViolations.get(field) != null;
    }

    @Override
    public void addFieldViolation(String field, ConstraintViolation constraintViolation) {
        if (field == null) {
            generalViolations.add(constraintViolation);
        } else {
            fieldViolations.put(field, constraintViolation);
        }
    }

    @Override
    public void addViolation(ConstraintViolation constraintViolation) {
        generalViolations.add(constraintViolation);
    }

    @Override
    public ConstraintViolation getFieldViolation(String field) {
        return fieldViolations.get(field);
    }

    @Override
    public String getFieldViolationMessage(String field, Locale locale) {
        ConstraintViolation violation = fieldViolations.get(field);
        if (violation == null) {
            return null;
        }
        // First, format field
        String formattedField = lang.getWithDefault(violation.getFieldKey(), field, locale);
        // Create parameters
        Object[] params = new Object[violation.getMessageParams().length + 1];
        params[0] = formattedField;
        if (params.length > 1) {
            System.arraycopy(violation.getMessageParams(), 0, params, 1, violation.getMessageParams().length);
        }
        // Format field
        return lang.getWithDefault(violation.getMessageKey(), violation.getDefaultMessage(), locale, params);
    }
}
