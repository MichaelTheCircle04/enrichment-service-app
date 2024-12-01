package com.mtrifonov.enrichmentservice.validators;

import com.mtrifonov.enrichmentservice.DomainModels.Message;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.stereotype.Component;

/**
 *
 * @Mikhail Trifonov
 */
@Component
@Slf4j
public class MessageValidatorImpl implements MessageValidator {
    
    @Override
    public boolean isValid(Message message) {
        String enrichmentKey = message.getEnrichment().name().toLowerCase();
        String regexPattern = message.getEnrichment().getRegexPattern();
        CustomComparator comparator = new CustomComparator(JSONCompareMode.LENIENT, 
                new Customization(enrichmentKey, 
                        new RegularExpressionValueMatcher<>(regexPattern)));
        try {
            JSONAssert.assertEquals("{\"" + enrichmentKey + "\":\"value\"}", message.getContent(), comparator);
            return true;
        } catch (JSONException | AssertionError e){
            log.error(e.getMessage());
            return false;
        }
    }
    
}
