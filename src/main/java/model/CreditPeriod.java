package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class CreditPeriod {
    int monthFrom;
    @Setter
    int monthTo;
    int dti;
    int interestYear;
}