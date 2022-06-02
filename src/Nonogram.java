import Nonogram.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Nonogram {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //Get Rules from txt files
        Scanner scan = null;
        do {
            try {
                System.out.print("Enter file name: ");
                scan = new Scanner(new File(scanner.nextLine()));
            } catch (FileNotFoundException e) {
                System.out.println("Count not find file name");
            }
        } while (scan == null);

        int numRows = scan.nextInt();
        int[][] rowRules = new int[numRows][];
        scan.nextLine();
        int pointer = 0;
        while (scan.hasNextLine())
        {
            String s = scan.nextLine();
            if (s.equals(""))
                break;
            String[] rulesString = s.split(",");
            int[] rulesInt = new int[rulesString.length];
            for (int i = 0; i < rulesInt.length; i++)
            {
                rulesInt[i] = Integer.parseInt(rulesString[i]);
            }
            rowRules[pointer++] = rulesInt;
        }

        int numCols = scan.nextInt();
        int[][] colRules = new int[numCols][];
        scan.nextLine();
        pointer = 0;
        while (scan.hasNextLine())
        {
            String s = scan.nextLine();
            if (s.equals(""))
                break;
            String[] rulesString = s.split(",");
            int[] rulesInt = new int[rulesString.length];
            for (int i = 0; i < rulesInt.length; i++)
            {
                rulesInt[i] = Integer.parseInt(rulesString[i]);
            }
            colRules[pointer++] = rulesInt;
        }

        NonogramSolver n = new NonogramSolver(rowRules,colRules);
        long start = System.currentTimeMillis();
        n.solveNonogram();
        n.displayNonogram(5);
        n.printNonogram();
        System.out.println(1.0*(System.currentTimeMillis() - start)/1000 + " seconds");
    }
}