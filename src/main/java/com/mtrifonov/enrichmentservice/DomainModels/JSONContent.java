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
public class JSONContent {
    private String action;
    private String page;
    private String msisdn;
    private Username enrichment;
}
