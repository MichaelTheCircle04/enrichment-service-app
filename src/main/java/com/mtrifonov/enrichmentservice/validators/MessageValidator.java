package com.mtrifonov.enrichmentservice.validators;

import com.mtrifonov.enrichmentservice.domainmodels.Message;

/**
 *
 * @Mikhail Trifonov
 */
public interface MessageValidator {
    public boolean isValid(Message message);
}
