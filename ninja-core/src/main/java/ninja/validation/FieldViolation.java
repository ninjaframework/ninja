package ninja.validation;

public class FieldViolation {
    public FieldViolation(String field, ConstraintViolation constraintViolation) {
        this.field = field;
        this.constraintViolation = constraintViolation;
    }

    public String field;
    public ConstraintViolation constraintViolation;
}
