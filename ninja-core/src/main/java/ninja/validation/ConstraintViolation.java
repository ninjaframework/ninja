/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.validation;

/**
 * A validation constraint violation
 * 
 * @author James Roper
 */
public class ConstraintViolation {
    private final String messageKey;
    private final String fieldKey;
    private final String defaultMessage;
    private final Object[] messageParams;

    /**
     * Create a constraint violation
     * 
     * @param messageKey
     *            The message key
     * @param fieldKey
     *            The field key. May be null.
     * @param defaultMessage
     *            The default message. May be null.
     * @param messageParams
     *            The message params
     */
    public ConstraintViolation(String messageKey,
                               String fieldKey,
                               String defaultMessage,
                               Object... messageParams) {
        this.messageKey = messageKey;
        this.fieldKey = fieldKey;
        this.defaultMessage = fieldKey != null ? defaultMessage.replace("{0}", fieldKey) : defaultMessage;
        this.messageParams = messageParams;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public Object[] getMessageParams() {
        return messageParams;
    }

    public static ConstraintViolation create(String messageKey,
                                             Object... messageParams) {
        return new ConstraintViolation(messageKey, null, null, messageParams);
    }

    public static ConstraintViolation createWithDefault(String messageKey,
                                                        String defaultMessage,
                                                        Object... messageParams) {
        return new ConstraintViolation(messageKey, null, defaultMessage,
                messageParams);
    }

    public static ConstraintViolation createForField(String messageKey,
                                                     String fieldKey,
                                                     Object... messageParams) {
        return new ConstraintViolation(messageKey, fieldKey, null,
                messageParams);
    }

    public static ConstraintViolation createForFieldWithDefault(String messageKey,
                                                                String fieldKey,
                                                                String defaultMessage,
                                                                Object... messageParams) {
        return new ConstraintViolation(messageKey, fieldKey, defaultMessage,
                messageParams);
    }

}
