import Nonogram.*;

import java.util.ArrayList;

public class TestRule {
    public static void main(String[] args) throws Exception{
        String[] arr = {"", "", "", "", "", "", ""};
        int[] rule = {2};
        NonogramProbThread output = new NonogramProbThread(arr, rule);
        Thread th = new Thread(output);
        th.start();
        th.join();
        double[] probabilities = output.getProbabilities();
        for (int i = 0; i < probabilities.length; i++) {
            System.out.print(probabilities[i] + " ");
        }
        System.out.println();
    }
}
