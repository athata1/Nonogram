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

        NonogramSolver n = new NonogramSolver(scan);
        n.setSlowSpeed(false);
        long start = System.currentTimeMillis();
        n.displayNonogram(10);
        n.solveNonogram();
        n.printNonogram();
        System.out.println(1.0*(System.currentTimeMillis() - start)/1000 + " seconds");
    }
}