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
public class User {
    
    private Integer id;
    private String msisdn;
    private String email;
    private Username username;
}
