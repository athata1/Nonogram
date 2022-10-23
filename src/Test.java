public class Test {
    public static void main(String[] args) {
        int alpha = 0;
        int beta = 0;
        int gamma = 0;
        int n = 10;
        int c = 1;
        for (int i = 0; i <= (n*n - 1)/2; i+=1) {
            alpha++;
            int k = 2*i + 1;
            if (c == k) {
                beta++;
                for (int j = 1; j < c; j += j) {
                    gamma++;
                }
                c *= 3;
            }
        }
        /*for (int i = 0; i <= Math.floor(Math.log((1.0*n*n)/Math.log(3))); i++) {
            beta++;
        }*/
        System.out.println(alpha);
        System.out.println(beta);
        System.out.println(gamma);
    }
}
