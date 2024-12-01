package com.mtrifonov.enrichmentservice.DomainModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @Mikhail Trifonov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    private String content;
    private EnrichmentType enrichment;

    public enum EnrichmentType {
        MSISDN("^(\\+7|8)(\\d{10})$");
        
        private final String regexPattern;
        
        EnrichmentType(String regexPattern) {
            this.regexPattern = regexPattern;
        }
        
        public String getRegexPattern() {
            return this.regexPattern;
        }
    }    
}
