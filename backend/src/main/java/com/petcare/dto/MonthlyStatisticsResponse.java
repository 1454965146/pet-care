package com.petcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatisticsResponse {

    private List<String> dates;

    private List<Double> weights;

    private List<Double> temperatures;

    private Double avgWeight;

    private Double weightVolatility;

    private Integer abnormalCount;

    private Integer totalRecords;

    private Integer vaccineCompletionRate;

}
