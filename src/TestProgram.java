import java.util.Arrays;

public class TestProgram {
    public static void main(String[] args) {
        int[] rule1 = {2,6,2};
        int[] rule2 = {2,7,2};
        int[] rule3 = {1,4,1,4,1};
        String[] arr = new String[15];
        Arrays.fill(arr,"");
        String[] result = applyRule3(arr,rule1);
        for (String s: result)
        {
            if (s.equals(""))
                System.out.print("_ ");
            else
                System.out.print(s + " ");
        }
    }
    public static String[] applyRule3(String[] arr, int[] rule)
    {
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
        int val = spacesLeft - (rule.length - 1);

        index = arr.length - 1;
        for (int r = rule.length - 1; r >= 0; r--)
        {
            if (r == rule.length - 1)
            {
                if (val > rule[r])
                {
                    for (int i = 0; i < rule[r]; i++)
                    {
                        arr[index--] = "";
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
                else
                {
                    for (int i = 0; i < val; i++)
                    {
                        arr[index--] = "";
                    }
                    for (int i = 0; i < rule[r] - val; i++)
                    {
                        index--;
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
                continue;
            }
            if (r == 0)
            {
                if (val > rule[r])
                {
                    for (int i = 0; i < rule[r]; i++)
                    {
                        arr[index--] = "";
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
                else
                {
                    for (int i = 0; i < rule[r] - val; i++)
                    {
                        index--;
                    }
                    for (int i = 0; i < val; i++)
                    {
                        arr[index--] = "";
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
                continue;
            }
            if (rule[r] < val)
            {
                for (int i = 0; i < rule[r]; i++)
                {
                    arr[index--] = "";
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
            else
            {
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
        }
        return arr;
    }
}
