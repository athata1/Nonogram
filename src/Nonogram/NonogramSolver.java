package Nonogram;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class NonogramSolver {

    private final int[][] rowRules;
    private final int[][] colRules;
    String[][] board;

    public NonogramSolver(int[][] rowRules, int[][] colRules)
    {
        this.rowRules = rowRules;
        this.colRules = colRules;
        board = new String[rowRules.length][colRules.length];
        for (int r = 0; r < board.length; r++)
        {
            Arrays.fill(board[r], "");
        }
    }
    public void displayNonogram(int size)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI(size);
            }
        });
    }
    public void createGUI(int size)
    {
        DisplayNonogram t = new DisplayNonogram(board,size,rowRules,colRules);
        JFrame jf = new JFrame();
        jf.setTitle("Nonogram");
        jf.setSize(1000,1000);
        t.setBackground(new Color(250,251,245));
        jf.add(t);
        jf.setLayout(new FlowLayout(FlowLayout.LEFT));
        jf.pack();
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    public void printNonogram()
    {
        for (int r = 0; r < board.length; r++)
        {
            for (int c = 0; c < board[r].length; c++)
            {
                if (board[r][c].equals(""))
                    System.out.print("- ");
                else
                    System.out.print(board[r][c] + " ");
            }
            System.out.println();
        }
    }
    //Solves the Nonogram given the rules provided in the constructor
    public String[][] solveNonogram()
    {
        String[][] prev = null;
        runThroughFirstBoard();
        while (true)
        {
            determineNonogram();
            if (isBoardFull())
                break;
            if (boardEqualsPrevious(prev))
                break;
            prev = copyOfBoard();
            System.out.println("Went through entire board");
        }
        return board;
    }

    public void determineNonogram()
    {
        int r = 0;
        int c = 0;
        int numThreads = 5;
        while (r + numThreads-1 < board.length && c + numThreads-1 < board[0].length)
        {
            long time = System.currentTimeMillis();

            //Generate rows and rules for those columns
            String[][] rowArrays = new String[numThreads][];
            int[][] rowRulesArray = new int[numThreads][];
            for (int i = 0; i < numThreads; i++)
            {
                rowArrays[i] = board[r+i];
                rowRulesArray[i] = rowRules[r+i];
            }

            //Generate columns and rules for those columns
            String[][] colArrays = new String[numThreads][];
            int[][] colRulesArray = new int[numThreads][];
            for (int i = 0; i < numThreads; i++)
            {
                colArrays[i] = generateColumnArray(c+i);
                colRulesArray[i] = colRules[c+i];
            }


            //Add nthreads for rows and columns
            ArrayList<NonogramProbThread> nThreads = new ArrayList<NonogramProbThread>();
            for (int i = 0; i < numThreads; i++)
            {
                nThreads.add(new NonogramProbThread(rowArrays[i],rowRulesArray[i]));
            }
            for (int i = 0; i < numThreads; i++)
            {
                nThreads.add(new NonogramProbThread(colArrays[i],colRulesArray[i]));
            }

            //Add all nthreads into a thread
            ArrayList<Thread> threads = new ArrayList<Thread>();
            for (int i = 0; i < nThreads.size(); i++)
            {
                threads.add(new Thread(nThreads.get(i)));
                threads.get(i).start();
            }


            //Halt runtime of main thread until all threads have been completed
            for (Thread t: threads)
            {
                try{t.join();}catch(Exception e){}
            }

            //Add thread results into row array
            for (int i = 0; i < numThreads; i++) {
                double[] probabilities = nThreads.get(i).getProbabilities();
                for (int j = 0; j < probabilities.length; j++) {
                    if (probabilities[j] == 1.0) {
                        board[r][j] = "S";
                    }
                    if (probabilities[j] == 0.0) {
                        board[r][j] = "X";
                    }
                }
                r++;
            }

            //Add thread results into column array
            for (int i = 0; i < numThreads; i++) {
                probabilities = nThreads.get(i+numThreads).getProbabilities();
                for (int j = 0; j < board.length; j++) {
                    if (probabilities[j] == 1) {
                        board[j][c] = "S";
                    }
                    if (probabilities[j] == 0)
                        board[j][c] = "X";
                }
                c++;
            }
            System.out.println("Row " + r + ": " + "Col " + c + ": " + (1.0*(System.currentTimeMillis()-time)/1000));
        }

        while (r < board.length)
        {
            System.out.println("Here");
            double[] probabilities = findProbabilityOfOccurrence(board[r],rowRules[r]);
            for (int j = 0; j < probabilities.length; j++)
            {
                if (probabilities[j] == 1.0)
                {
                    board[r][j] = "S";
                }
                if (probabilities[j] == 0.0)
                {
                    board[r][j] = "X";
                }
            }
            r++;
        }

        while (c < board[0].length)
        {
            System.out.println("Here");
            String[] colArray = generateColumnArray(c);
            double[] probabilities = findProbabilityOfOccurrence(colArray,colRules[c]);
            for (int i = 0; i < board.length; i++)
            {
                if (probabilities[i] == 1)
                {
                    board[i][c] = "S";
                }
                if (probabilities[i] == 0)
                    board[i][c] = "X";
            }
            c++;
        }
    }
    public void runThroughFirstBoard()
    {
        runRule1();
        runRule2();
        runRule3();
        printNonogram();
    }
    public void runRule3()
    {
        for (int i = 0; i < board.length; i++) {
            if (board[i][0].equals("S")) {
                for (int j = 0; j < rowRules[i][0]; j++) {
                    board[i][j] = "S";
                }
                if (rowRules[i][0] <= board[i].length - 1)
                    board[i][rowRules[i][0]] = "X";
            }
            if (board[i][board[i].length - 1].equals("S")) {
                for (int j = 0; j < rowRules[i][rowRules[i].length - 1]; j++) {
                    board[i][board[i].length - 1 - j] = "S";
                }
                if (board[i].length - 1 - rowRules[i][rowRules[i].length - 1] >= 0)
                    board[i][board[i].length - 1 - rowRules[i][rowRules[i].length - 1]] = "X";
            }
        }
        for (int i = 0; i < board[0].length; i++) {
            if (board[0][i].equals("S")) {
                for (int j = 0; j < colRules[i][0]; j++) {
                    board[j][i] = "S";
                }
                if (colRules[i][0] <= board.length - 1)
                    board[colRules[i][0]][i] = "X";
            }
            if (board[board.length - 1][i].equals("S")) {
                for (int j = 0; j < colRules[i][colRules[i].length - 1]; j++) {
                    board[board.length - 1 - j][i] = "S";
                }
                if (board.length - 1 - colRules[i][colRules[i].length - 1] >= 0) {
                    board[board.length - 1 - colRules[i][colRules[i].length - 1]][i] = "X";
                }
            }
        }
    }
    public void runRule1()
    {
        String[][] temp1 = new String[board.length][board[0].length];
        for (int r = 0; r < temp1.length; r++)
        {
            Arrays.fill(temp1[r], "");
        }

        for (int r = 0; r < rowRules.length; r++)
        {
            if (rowRules[r].length == 1)
                applyRule1(r,true,rowRules[r][0],temp1);
        }

        String[][] temp2 = new String[board.length][board[0].length];
        for (int r = 0; r < temp1.length; r++)
        {
            Arrays.fill(temp2[r], "");
        }
        for (int c = 0; c < colRules.length; c++)
        {
            if (colRules[c].length == 1)
                applyRule1(c,false,colRules[c][0],temp2);
        }

        for (int r = 0; r < board.length; r++)
        {
            for (int c = 0; c < board[r].length; c++)
            {
                if (temp1[r][c].equals("S") || temp2[r][c].equals("S"))
                    board[r][c] = "S";
            }
        }
    }
    public void runRule2()
    {
        String[][] temp1 = new String[board.length][board[0].length];
        for (int r = 0; r < temp1.length; r++)
        {
            Arrays.fill(temp1[r], "");
        }

        for (int r = 0; r < rowRules.length; r++)
        {
            if (rowRules[r].length >= 2)
                applyRule2(r,true,rowRules[r],temp1);
        }

        String[][] temp2 = new String[board.length][board[0].length];
        for (int r = 0; r < temp1.length; r++)
        {
            Arrays.fill(temp2[r], "");
        }
        for (int c = 0; c < colRules.length; c++)
        {
            if (colRules[c].length >= 2)
                applyRule2(c,false,colRules[c],temp2);
        }

        for (int r = 0; r < board.length; r++)
        {
            for (int c = 0; c < board[r].length; c++)
            {
                if (temp1[r][c].equals("S") || temp2[r][c].equals("S"))
                    board[r][c] = "S";
            }
        }
    }
    public void applyRule2(int value, boolean isRow, int[] rule, String[][] board)
    {
        String[] arr = (isRow) ? new String[board[0].length] : new String[board.length];
        Arrays.fill(arr,"");
        int spacesLeft = arr.length;
        for (int i = 0; i < rule.length; i++)
        {
            spacesLeft -= rule[i];
        }
        int spacesPerGap = spacesLeft / (rule.length - 1);
        int remainder = spacesLeft % (rule.length - 1);
        int placeholderRemainder = remainder;

        int index = arr.length - 1;
        for (int r = rule.length - 1; r >= 0; r--)
        {
            for (int i = 0; i < rule[r]; i++)
            {
                arr[index--] = "S";
            }
            for (int i = 0; i < spacesPerGap; i++)
            {
                index--;
            }
            if (remainder != 0)
            {
                remainder--;
                index--;
            }
        }
        remainder = placeholderRemainder;

        //Simplified version of 15 - (15 - spaceLeft + (rule.length - 1))
        int val = spacesLeft - (rule.length - 1);
        index = arr.length - 1;
        for (int r = rule.length - 1; r >= 0; r--)
        {
            if (val > rule[r]) {
                for (int i = 0; i < rule[r]; i++) {
                    arr[index--] = "";
                }
                index -= spacesPerGap;
                if (remainder != 0) {
                    remainder--;
                    index--;
                }
                continue;
            }
            if (r == rule.length - 1)
            {
                for (int i = 0; i < val; i++)
                {
                    arr[index--] = "";
                }
                for (int i = 0; i < rule[r] - val; i++)
                {
                    index--;
                }
                index -= spacesPerGap;
                if (remainder != 0)
                {
                    remainder--;
                    index--;
                }
                continue;
            }
            if (r == 0)
            {
                for (int i = 0; i < rule[r] - val; i++)
                {
                    index--;
                }
                for (int i = 0; i < val; i++)
                {
                    arr[index--] = "";
                }
                index -= spacesPerGap;
                if (remainder != 0) {
                    remainder--;
                    index--;
                }
                continue;
            }

            //In middle
            int firstHalf = val/2;
            int secondHalf = val/2 + val%2;
            for (int i = 0; i < firstHalf; i++)
            {
                arr[index--] = "";
            }
            for (int i = 0; i < rule[r] - firstHalf - secondHalf; i++)
            {
                index--;
            }
            for (int i = 0; i < secondHalf; i++)
            {
                arr[index--] = "";
            }
            index -= spacesPerGap;
            if (remainder != 0)
            {
                remainder--;
                index--;
            }
        }
        if (isRow)
        {
            for (int c = 0; c < arr.length; c++)
            {
                board[value][c] = arr[c];
            }
        }
        else
        {
            for (int r = 0; r < arr.length; r++)
            {
                board[r][value] = arr[r];
            }
        }
    }
    public void applyRule1(int index, boolean isRow, int rule, String[][] board)
    {
        if (isRow)
        {
            for (int c = 0; c < rule;c++)
            {
                board[index][c] = "S";
            }
            int val = board[0].length - rule;
            for (int c = 0; c < val; c++)
            {
                board[index][c] = "";
            }
            return;
        }
        for (int r = 0; r < rule;r++)
        {
            board[r][index] = "S";
        }
        int val = board.length - rule;
        for (int r = 0; r < val; r++)
        {
            board[r][index] = "";
        }
        return;

    }
    public String[] generateColumnArray(int c)
    {
        String[] colArray = new String[board.length];
        for (int i = 0; i < colArray.length; i++)
        {
            colArray[i] = board[i][c];
        }
        return colArray;
    }

    // Find probability of block occurring at specific point in row/column
    static double[] probabilities;
    static int total;
    private double[] findProbabilityOfOccurrence(String[] arr, int[] rule)
    {
        probabilities = new double[arr.length];
        total = 0;
        helper2(0,new ArrayList<Integer>(),arr,rule);
        for(int i = 0; i < probabilities.length; i++)
        {
            probabilities[i] = probabilities[i]/total;
        }
        return probabilities;
    }
    private void helper2(int depth, ArrayList<Integer> currentIndexes, String[] arr, int[] rule) {
        if (depth == rule.length) {
            int[] row = new int[arr.length];
            Arrays.fill(row,0);
            for (int i = 0; i < currentIndexes.size(); i++) {
                for (int j = currentIndexes.get(i); j < rule[i] + currentIndexes.get(i); j++) {
                    row[j]++;
                }
            }
            for (int i = 0; i < arr.length; i++) {
                if ((row[i] == 0 && arr[i].equals("S")) || (row[i] == 1 && arr[i].equals("X")))
                    return;
            }
            total++;
            for (int i = 0; i < arr.length; i++) {
                if (row[i] == 1)
                    probabilities[i]++;
            }
            return;
        }
        int longestVal = 0;
        for (int i = depth; i < rule.length; i++) {
            longestVal += rule[i];
            longestVal++;
        }
        longestVal--;
        int start = 0;
        if (currentIndexes.size() != 0)
            start = currentIndexes.get(currentIndexes.size() - 1) + rule[depth - 1] + 1;
        for (int i = start; i < arr.length - longestVal + 1; i++) {
            boolean isValid = true;
            int countBlack = 0;
            for (int j = i; j < rule[depth] + i; j++) {
                if (arr[j].equals("X")) {
                    isValid = false;
                    break;
                }
                else if (arr[j].equals("S")) {
                    countBlack++;
                }
            }
            if (rule[depth] + i < arr.length && arr[rule[depth] + i].equals("S"))
                continue;
            if (i - 1 >= 0 && arr[i - 1].equals("S"))
                continue;
            if (!isValid)
                continue;

            currentIndexes.add(i);
            helper2(depth + 1, currentIndexes, arr, rule);
            currentIndexes.remove(currentIndexes.size() - 1);
            if (countBlack == rule[depth]) {
                i += rule[depth] - 1;
            }
        }
    }
    private void helper(String[] arr, int[] rule)
    {
        if (isFull(arr))
        {
            if (ruleMatches(arr,rule))
            {
                total++;
                for(int i = 0; i < arr.length; i++)
                {
                    char c = arr[i].charAt(0);
                    if (c == 'S')
                        probabilities[i]++;
                }
            }
            return;
        }
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i].equals(""))
            {
                String[] stringArray = {"X","S"};
                for(String s: stringArray)
                {
                    arr[i] = s;
                    helper(arr,rule);
                }
                arr[i] = "";
                break;
            }
        }
    }

    //Determine if board is completely filled
    public static boolean isFull(String[] arr)
    {
        for (String s: arr)
        {
            if (s.equals(""))
                return false;
        }
        return true;
    }

    //Determine if rule matches given String
    public static boolean ruleMatches(String[] arr, int[] rule)
    {
        int[] currentRule = getRule(arr);

        if (currentRule.length != rule.length)
            return false;

        for (int i = 0; i < rule.length; i++)
        {
            if (rule[i] != currentRule[i])
                return false;
        }
        return true;
    }

    //Covert a String array into the Nonogram Rule for the String array
    public static int[] getRule(String[] arr)
    {
        int count = 0;
        ArrayList<Integer> currentRule = new ArrayList<Integer>();
        for (String s: arr)
        {
            if (s.length() == 1)
            {
                char c = s.charAt(0);
                if (c == 'S')
                    count++;
                else
                {
                    if (count != 0)
                    {
                        currentRule.add(count);
                        count = 0;
                    }
                }
            }
            else
            {
                if (count != 0)
                {
                    currentRule.add(count);
                    count = 0;
                }
            }
        }
        if (count != 0)
            currentRule.add(count);

        if (currentRule.size() == 0)
            return new int[]{0};

        int[] output = new int[currentRule.size()];
        for (int i = 0; i < output.length; i++)
        {
            output[i] = currentRule.get(i);
        }
        return output;
    }

    //Create copy of current board
    private String[][] copyOfBoard()
    {
        String[][] output = new String[board.length][board[0].length];
        for (int r = 0; r < output.length; r++)
        {
            for (int c = 0; c < output[r].length; c++)
            {
                output[r][c] = board[r][c];
            }
        }
        return output;
    }

    //Determine if board is the same as the given 2D String array
    private boolean boardEqualsPrevious(String[][] prev)
    {
        if (prev == null)
            return false;
        for (int r = 0; r < board.length; r++)
        {
            for (int c = 0; c < board[r].length; c++)
            {
                if (!board[r][c].equals(prev[r][c]))
                    return false;
            }
        }
        return true;
    }

    //Determine if board is completely filled
    private boolean isBoardFull()
    {
        for (String[] rows: board)
        {
            for (String col: rows)
            {
                if (col.equals(""))
                    return false;
            }
        }
        return true;
    }
}