package com.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumPackageResponse {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationDays;
    private Boolean active;
    private LocalDateTime updatedAt;
}
