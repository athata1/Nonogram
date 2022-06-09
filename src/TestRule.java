import Nonogram.*;

import java.util.ArrayList;

public class TestRule {
    public static void main(String[] args) throws Exception{
        /*String[] arr = {"", "", "", "", "", "", "","",""};
        int[] rule = {3, 3,1};
        NonogramProbThread output = new NonogramProbThread(arr, rule);
        Thread th = new Thread(output);
        th.start();
        th.join();
        double[] probabilities = output.getProbabilities();
        for (int i = 0; i < probabilities.length; i++) {
            System.out.print(probabilities[i] + " ");
        }
        System.out.println();*/

        String[] arr = {"", "", "", "", "", "", "","",""};
        int[] rule = {5,1};
        int sum = 0;
        for (int n: rule) {
            sum += n + 1;
        }
        sum--;
        int leftShift = 0;
        ArrayList<ArrayList<int[]>> list = new ArrayList<>();
        for (int n: rule) {
            ArrayList<int[]> curr = new ArrayList<>();
            for (int i = leftShift; i < arr.length - sum + 1; i++) {
                curr.add(new int[]{i,i + n - 1});
            }
            list.add(curr);
            sum -= n + 1;
            leftShift += n + 1;
        }

        /*int[][] union = new int[rule.length][2];
        int i = 0;
        for (ArrayList<int[]> lines: list) {
            int l = 0;
            int r = arr.length - 1;
            for (int[] line: lines) {
                l = Math.max(l, line[0]);
                r = Math.min(r, line[1]);
            }
            union[i][0] = l;
            union[i][1] = r;
            i++;
        }
        for (int[] n: union) {
            for (int j = n[0]; j <= n[1]; j++) {
                arr[j] = "s";
            }
        }*/

        NewNonogramThread n = new NewNonogramThread(arr, rule, list);
        Thread th = new Thread(n);
        th.start();
        th.join();
        for (String s: arr) {
            if (s.equals(""))
                System.out.print("- ");
            System.out.print(s + " ");
        }
        System.out.println();
    }
}
