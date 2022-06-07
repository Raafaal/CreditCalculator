package model;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
public class InputDTO {
    private Integer employmentPeriod;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyMaintenanceCosts;
    private BigDecimal monthlySumOfCreditObligations;
    private BigDecimal totalInstallmentLoans;
}

