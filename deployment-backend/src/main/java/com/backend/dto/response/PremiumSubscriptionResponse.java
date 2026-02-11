package com.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumSubscriptionResponse {
    
    private Long id;
    private Long userId;
    private String userName;
    private BigDecimal amountPaid;
    private Integer durationDays;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;
    private Boolean active;
}
