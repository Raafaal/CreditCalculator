import exceptions.NullInputException;
import model.CreditPeriod;
import model.InputDTO;
import model.OutputDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CreditCalculator {
    private static final int MAX_LOAN_PERIOD_MONTHS = 100;
    private final static String MAX_LOAN_AMOUNT_PLN = "150000";
    private final static String CONCENTRATION_LIMIT = "200000";
    private final static int MIN_LOAN_PERIOD = 6;
    private final static String MIN_LOAN_AMOUNT_PLN = "5000";

    MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);


    public List<OutputDTO> getResultOfCalculation(InputDTO inputDTO) throws NullInputException {
        if (!validateInput(inputDTO)) {
            throw new NullInputException("Błędne dane wejściowe!");
        }
        int maxLoanPeriod = getMaxLoanPeriod(inputDTO.getEmploymentPeriod());

        if (maxLoanPeriod < MIN_LOAN_PERIOD) {
            return new ArrayList<>();
        }
        List<CreditPeriod> creditPeriods = getDataOfLoanPeriods(maxLoanPeriod);

        List<OutputDTO> resultOutputsDTO = new ArrayList<>();
        for (CreditPeriod period : creditPeriods) {
            OutputDTO outputDTO = new OutputDTO();
            outputDTO.setMaxLoanPeriod(period.getMonthTo());
            outputDTO.setMaximumMonthlyRate(getMaximumMonthlyRate(inputDTO, period.getDti()));
            BigDecimal maxCreditAmount = getMaxCreditAmount(inputDTO, outputDTO, period);
            outputDTO.setMaxCreditAmount(maxCreditAmount);

            outputDTO.setCreditSolvency(!(maxCreditAmount.doubleValue() < new BigDecimal(MIN_LOAN_AMOUNT_PLN).doubleValue()));

            resultOutputsDTO.add(outputDTO);
        }
        return resultOutputsDTO;
    }


    private int getMaxLoanPeriod(int timePeriod) {
        return Math.min(timePeriod, MAX_LOAN_PERIOD_MONTHS);
    }

    private List<CreditPeriod> getDataOfLoanPeriods(int maxLoanPeriod) {
        if (maxLoanPeriod < MIN_LOAN_PERIOD) {
            return new ArrayList<>();
        }

        List<CreditPeriod> creditPeriods = new ArrayList<>();
        if (maxLoanPeriod > 6) creditPeriods.add(new CreditPeriod(6, 12, 60, 2));
        if (maxLoanPeriod > 12) creditPeriods.add(new CreditPeriod(13, 36, 60, 3));
        if (maxLoanPeriod > 36) creditPeriods.add(new CreditPeriod(37, 60, 50, 3));
        if (maxLoanPeriod > 60) creditPeriods.add(new CreditPeriod(61, 100, 55, 3));

        if (!creditPeriods.isEmpty() && maxLoanPeriod < MAX_LOAN_PERIOD_MONTHS) {
            creditPeriods.get(creditPeriods.size() - 1).setMonthTo(maxLoanPeriod);
        }

        return creditPeriods;
    }

    private BigDecimal getMaxCreditAmount(InputDTO inputDTO, OutputDTO outputDTO, CreditPeriod creditPeriod) {
        BigDecimal resultA = new BigDecimal(CONCENTRATION_LIMIT).subtract(inputDTO.getMonthlySumOfCreditObligations());
        BigDecimal resultB = new BigDecimal(MAX_LOAN_AMOUNT_PLN);
        BigDecimal mi = new BigDecimal(creditPeriod.getInterestYear()).divide(new BigDecimal("100"), mc).divide(BigDecimal.valueOf(12), mc);//:12:100 procent
        BigDecimal resultC = (BigDecimal.ONE.subtract(BigDecimal.ONE.divide(((mi.add(BigDecimal.ONE)).pow(creditPeriod.getMonthTo())), mc)))
                .multiply(outputDTO.getMaximumMonthlyRate()).divide(mi, mc);

        BigDecimal result = resultA.min(resultB);
        return result.min(resultC).setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getMaximumMonthlyRate(InputDTO inputDTO, int dti) {
        BigDecimal rateA = inputDTO.getMonthlyIncome()
                .subtract(inputDTO.getMonthlyMaintenanceCosts())
                .subtract(inputDTO.getMonthlySumOfCreditObligations());
        BigDecimal rateB = new BigDecimal(String.valueOf(dti)).divide(BigDecimal.valueOf(100), mc)
                .multiply(inputDTO.getMonthlyIncome())
                .subtract(inputDTO.getMonthlySumOfCreditObligations());


        return rateA.min(rateB).setScale(2, RoundingMode.HALF_EVEN);

    }


    private boolean validateInput(InputDTO inputDTO) {
        return inputDTO != null
                && inputDTO.getMonthlyIncome() != null
                && inputDTO.getMonthlyMaintenanceCosts() != null
                && inputDTO.getTotalInstallmentLoans() != null
                && inputDTO.getMonthlySumOfCreditObligations() != null
                && inputDTO.getMonthlyIncome().doubleValue() >= 0
                && inputDTO.getTotalInstallmentLoans().doubleValue() >= 0
                && inputDTO.getMonthlyMaintenanceCosts().doubleValue() > 0
                && inputDTO.getMonthlySumOfCreditObligations().doubleValue() >= 0;

    }
}


