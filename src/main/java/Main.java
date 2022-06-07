import exceptions.NullInputException;
import model.InputDTO;
import model.OutputDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;


public class Main {
    private static final String WRONG_VALUE = "Wprowdzono błędną wartość, proszę powtórzyć.";

    public static void main(String[] args) {

        Logger log = Logger.getLogger(Main.class.getCanonicalName());
        Scanner scan = new Scanner(System.in);

        while (true) {

            Integer okres = null;
            BigDecimal miesiecznyDochod = null;
            BigDecimal miesieczneKosztyUtrzymania = null;
            BigDecimal miesiecznaSumaZobowiazanKredytowych = null;
            BigDecimal sumaSaldKredytowRatalnych = null;

            System.out.println("Wprowadź dane do kalkulacji kredytu");
            while (okres == null || okres < 0) {
                System.out.println("Okres zatrudnienia w miesiącach:");
                okres = integerFromScan(scan);
            }

            while (miesiecznyDochod == null || miesiecznyDochod.doubleValue() <= 0) {
                System.out.println("Miesięczny dochód [PLN]:");
                miesiecznyDochod = bigDecimalFromScan(scan);
            }

            while (miesieczneKosztyUtrzymania == null || miesieczneKosztyUtrzymania.doubleValue() <= 0) {
                System.out.println("Miesięczne koszty utrzymania [PLN]:");
                miesieczneKosztyUtrzymania = bigDecimalFromScan(scan);
            }

            while (miesiecznaSumaZobowiazanKredytowych == null || miesiecznaSumaZobowiazanKredytowych.doubleValue() < 0) {
                System.out.println("Miesięczne suma zobowiazan kredytowych [PLN]:");
                miesiecznaSumaZobowiazanKredytowych = bigDecimalFromScan(scan);
            }

            while (sumaSaldKredytowRatalnych == null || sumaSaldKredytowRatalnych.doubleValue() < 0) {
                System.out.println("Miesięczne suma sald ratalnych [PLN]:");
                sumaSaldKredytowRatalnych = bigDecimalFromScan(scan);
            }
            InputDTO inputDTO = new InputDTO(okres, miesiecznyDochod, miesieczneKosztyUtrzymania, miesiecznaSumaZobowiazanKredytowych, sumaSaldKredytowRatalnych);
            List<OutputDTO> result = null;
            try {
                result = new CreditCalculator().getResultOfCalculation(inputDTO);
            } catch (NullInputException e) {
                log.warning(e.getMessage());
                e.printStackTrace();
            }

            if (result == null || result.isEmpty()) {
                System.out.println("Brak zdolności kredytowej.");
            }else{
                for (OutputDTO outputDTO : result) {
                    if (!outputDTO.isCreditSolvency()) {
                        System.out.println("Brak zdolności kredytowej.");
                        break;
                    } else {
                        System.out.println(outputDTO);
                    }
                }
            }
            System.out.println("exit?(Y/N)");
            String statement = scan.nextLine();
            if(statement.equalsIgnoreCase("y")){
                System.exit(0);
            }

        }


    }

    private static Integer integerFromScan(Scanner scan) {
        try {
            return Integer.valueOf(scan.nextLine());
        } catch (Exception ex) {
            System.out.println(WRONG_VALUE);
            return null;
        }
    }

    private static BigDecimal bigDecimalFromScan(Scanner scan) {
        try {
            return new BigDecimal(scan.nextLine());
        } catch (Exception ex) {
            System.out.println(WRONG_VALUE);
            return null;
        }
    }
}
