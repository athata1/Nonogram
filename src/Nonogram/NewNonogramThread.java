package Nonogram;

import java.util.ArrayList;

public class NewNonogramThread implements Runnable{

    String[] currentRow;
    int[] currentRule;
    int length;
    boolean running;
    ArrayList<ArrayList<int[]>> currentLines;
    public NewNonogramThread(String[] currentRow, int[] currentRule, ArrayList<ArrayList<int[]>> currentLines) {
        this.currentRow = currentRow;
        this.currentRule = currentRule;
        this.currentLines = currentLines;
        this.length = currentRow.length;
        running = false;
    }

    public void run() {
        int filled = 0;
        for (int i = 0; i < length; i++) {
            if (currentRow[i].equals("S")) {
                for (int j = 0; j < currentLines.size(); j++) {
                    ArrayList<int[]> lines = currentLines.get(j);
                    for (int k = lines.size() - 1; k >= 0; k--) {
                        int[] line = lines.get(k);
                        if (line[0] - 1 == i || line[1] + 1 == i) {
                            lines.remove(k);
                            running = true;
                        }
                    }
                }
                filled++;
            }
            else if (currentRow[i].equals("X")) {
                for (int j = 0; j < currentLines.size(); j++) {
                    ArrayList<int[]> lines = currentLines.get(j);
                    for (int k = lines.size() - 1; k >= 0; k--) {
                        int[] line = lines.get(k);
                        if (line[0] >= i && line[1] <= i) {
                            lines.remove(k);
                            running = true;
                        }
                    }
                }
            }
        }

        int sum = 0;
        for (int i = 0; i < currentRule.length; i++) {
            sum += currentRule[i];
        }

        if (sum == filled) {
            for (int i = 0; i < currentRow.length; i++) {
                if (currentRow[i].equals(""))
                    currentRow[i] = "X";
            }
        }

        for (int i = 0; i < length; i++) {
            if (!currentRow[i].equals(""))
                continue;
            int count = 0;
            int index = -1;
            for (int j = 0; j < currentLines.size(); j++) {
                ArrayList<int[]> lines = currentLines.get(j);
                for (int k = 0; k < lines.size(); k++) {
                    int[] line = lines.get(k);
                    if (line[0] <= i && line[1] >= i) {
                        count++;
                        index = j;
                        break;
                    }
                }
                if (count == 2) {
                    break;
                }
            }
            if (count == 0) {
                currentRow[i] = "X";
            }
            if (count == 1) {
                currentRow[i] = "S";
                for (int j = 0; j < currentLines.get(index).size(); j++) {
                    int[] line = currentLines.get(index).get(j);
                    if (line[0] > i || line[1] < i) {
                        currentRow[i] = "";
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < currentLines.size(); i++) {
            if (currentLines.get(i).size() == 1) {
                int[] line = currentLines.get(i).get(0);
                for (int j = line[0]; j <= line[1]; j++) {
                    currentRow[j] = "S";
                }
                if (line[0] - 1 >= 0) {
                    currentRow[line[0] - 1] = "X";
                }
                if (line[1] + 1 < length) {
                    currentRow[line[1] + 1] = "X";
                }

                for (int j = 0; j < i; j++) {
                    for (int k = currentLines.get(j).size() - 1; k >= 0; k--) {
                        if (currentLines.get(j).get(k)[1] >= line[0] - 1) {
                            currentLines.get(j).remove(k);
                            running = true;
                        }
                    }
                }
                for (int j = i + 1; j < currentLines.size(); j++) {
                    for (int k = currentLines.get(j).size() - 1; k >= 0; k--) {
                        if (currentLines.get(j).get(k)[0] <= line[1] + 1) {
                            currentLines.get(j).remove(k);
                            running = true;
                        }
                    }
                }
            }
            if (i + 1 < currentLines.size() && currentLines.get(i).size() > 0 && currentLines.get(i+1).size() > 0) {
                for (int j =  currentLines.get(i + 1).size() - 1; j >= 0; j--) {
                    if (currentLines.get(i + 1).get(j)[0] <= currentLines.get(i).get(0)[1] + 1) {
                        currentLines.get(i + 1).remove(j);
                        running = true;
                    }
                }
                for (int j = currentLines.get(i).size() - 1; j >= 0; j--) {
                    if (currentLines.get(i).get(j)[1] >= currentLines.get(i + 1).get(currentLines.get(i + 1).size() - 1)[0] - 1) {
                        currentLines.get(i).remove(j);
                        running = true;
                    }
                }
            }
        }
    }
}
