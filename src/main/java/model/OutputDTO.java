package model;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OutputDTO {
    private boolean creditSolvency = false;
    private int maxLoanPeriod;
    private BigDecimal maximumMonthlyRate;
    private BigDecimal maxCreditAmount;

    public String toString() {
        return String.format("Maksymalny okres kredytowania: %s , maksymalna miesięczna rata: %s, maksymalna kwota kredytu: %s"
                , maxLoanPeriod, maximumMonthlyRate, maxCreditAmount);
    }
}
