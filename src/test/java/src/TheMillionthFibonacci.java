import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class TheMillionthFibonacci {

    static long quickFibonacci(int n) {
        long[] f_n = new long[]{0, 1, 1};
        if(n <= 3)
            return f_n[n-1];

        for (int i = 1,k = 1; i < n - 1;) {
            long[] tmpF_n = Arrays.copyOf(f_n, f_n.length);

            if (k % 2 == 0 && n - 1 > i + k*2) {
                f_n[0] = tmpF_n[0] * tmpF_n[0] + tmpF_n[1] * tmpF_n[1];
                f_n[1] = tmpF_n[1] * tmpF_n[2] + tmpF_n[1] * tmpF_n[0];
                f_n[2] = tmpF_n[1] * tmpF_n[1] + tmpF_n[2] * tmpF_n[2];
//                System.out.println("mult");
                i = k*=2;
            } else {
                k = ++i;
                f_n[0] = tmpF_n[1];
                f_n[1] = tmpF_n[2];
                f_n[2] = tmpF_n[1] + tmpF_n[2];

//                System.out.println("add");
            }
//            System.out.println("i = " + i + " k = " + k);
//            System.out.println(f_n[0] + " " + f_n[1] + "\n"
//                    + f_n[1] + " " + f_n[2] + "\n");
        }
        return f_n[2];
    }

    @Test
    void testQuickFibonachi() {
        assertEquals(0, quickFibonacci(1));
        assertEquals(1, quickFibonacci(2));
        assertEquals(1, quickFibonacci(3));
        assertEquals(3, quickFibonacci(4));
        assertEquals(5, quickFibonacci(5));
        assertEquals(8, quickFibonacci(6));
        assertEquals(13, quickFibonacci(7));
        assertEquals(21, quickFibonacci(8));
        assertEquals(34, quickFibonacci(9));
        assertEquals(55, quickFibonacci(10));
        assertEquals(89, quickFibonacci(11));
        assertEquals(144, quickFibonacci(12));
        assertEquals(75025, quickFibonacci(25));
        assertEquals(832040, quickFibonacci(30));
        assertEquals(102334155L, quickFibonacci(40));
        assertEquals(2111485077978050L, quickFibonacci(75));
//        assertEquals(218922995834555169026, quickFibonacci(100));
    }

    static BigInteger quickFibonacci(BigInteger n) {
        BigInteger[] f_n = new BigInteger[]{BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE};
        if(n.intValue() <= 3)
            return f_n[n.intValue()-1];

        for (BigInteger i = BigInteger.ZERO; n.compareTo(i.add(BigInteger.TWO)) > 0;) {
            BigInteger[] tmpF_n = Arrays.copyOf(f_n, f_n.length);
            if (i.compareTo(BigInteger.ONE) > 0 && n.compareTo(i.pow(2).add(i)) > 0) {
                i = i.pow(2).add(i.add(BigInteger.ONE).mod(BigInteger.TWO));
                f_n[0] = tmpF_n[0].multiply(tmpF_n[0]).add(tmpF_n[1].multiply(tmpF_n[1]));
                f_n[1] = tmpF_n[1].multiply(tmpF_n[2]).add(tmpF_n[1].multiply(tmpF_n[0]));
                f_n[2] = tmpF_n[1].multiply(tmpF_n[1]).add(tmpF_n[2].multiply(tmpF_n[2]));

            } else {
                f_n[0] = tmpF_n[1];
                f_n[1] = tmpF_n[2];
                f_n[2] = tmpF_n[1].add(tmpF_n[2]);
                i = i.add(BigInteger.ONE);
            }
        }
        return f_n[2];
    }
    @Test
    void testQuickFibonachiBigInteger() {
        assertEquals(BigInteger.ZERO, quickFibonacci(BigInteger.ONE));
        assertEquals(BigInteger.ONE, quickFibonacci(BigInteger.TWO));
        assertEquals(BigInteger.ONE, quickFibonacci(BigInteger.valueOf(3)));
        assertEquals(BigInteger.valueOf(3), quickFibonacci(BigInteger.valueOf(4)));
        assertEquals(BigInteger.valueOf(5), quickFibonacci(BigInteger.valueOf(5)));
        assertEquals(BigInteger.valueOf(8), quickFibonacci(BigInteger.valueOf(6)));
        assertEquals(BigInteger.valueOf(13), quickFibonacci(BigInteger.valueOf(7)));
        assertEquals(BigInteger.valueOf(21), quickFibonacci(BigInteger.valueOf(8)));
        assertEquals(BigInteger.valueOf(34), quickFibonacci(BigInteger.valueOf(9)));
        assertEquals(BigInteger.valueOf(55), quickFibonacci(BigInteger.valueOf(10)));
        assertEquals(BigInteger.valueOf(89), quickFibonacci(BigInteger.valueOf(11)));
        assertEquals(BigInteger.valueOf(144), quickFibonacci(BigInteger.valueOf(12)));
        assertEquals(BigInteger.valueOf(75025), quickFibonacci(BigInteger.valueOf(25)));
        assertEquals(BigInteger.valueOf(832040), quickFibonacci(BigInteger.valueOf(30)));
        assertEquals(BigInteger.valueOf(832040), quickFibonacci(BigInteger.valueOf(40)));
        assertEquals(BigInteger.valueOf(12586269025L), quickFibonacci(BigInteger.valueOf(50)));
        assertEquals(BigInteger.valueOf(2111485077978050L), quickFibonacci(BigInteger.valueOf(75)));
    }


    //https://www.codewars.com/kata/53d40c1e2f13e331fc000c26/train/java
    static BigInteger factorial(BigInteger v) {
        if (v.compareTo(BigInteger.ONE) <= 0)
            return BigInteger.ONE;
        return factorial(v.subtract(BigInteger.ONE)).multiply(v);
    }

    static BigInteger nCr(BigInteger n, BigInteger k) {
        if (k.compareTo(n) > 0 || k.compareTo(BigInteger.ZERO) < 0)
            return BigInteger.ZERO;
        if (k.compareTo(BigInteger.ZERO) == 0)
            return BigInteger.ONE;
        return factorial(n).divide(factorial(k).multiply(factorial(n.subtract(k))));

    }

    public static BigInteger F_n(BigInteger n) {
        BigInteger result = BigInteger.ZERO;
        if (n.equals(BigInteger.ZERO))
            return BigInteger.ZERO;
        for (int k = 0; k <= n.intValue() / 2; k++)
            result = result.add(
                    nCr(n, BigInteger.TWO.multiply(BigInteger.valueOf(k)).add(BigInteger.ONE))
                            .multiply(BigInteger.valueOf(5).pow(k))
            );
        return result.divide(BigInteger.TWO.pow(n.intValue() - 1));
    }

    public static final double fi = (1.0 + Math.sqrt(5.0)) / 2.0;

    public static double nApproximation(BigInteger F_n) {
        if (F_n.equals(BigInteger.ZERO))
            return 0;
        if (F_n.equals(BigInteger.TWO))
            return 1;
        if (F_n.equals(BigInteger.valueOf(3)))
            return 2;
        if (F_n.equals(BigInteger.valueOf(4)))
            return 3;

        long[] parts = takeBigIntegerApart(F_n);
        double logResult = Math.log(parts[0]) + Math.log(parts[1]);
        return (logResult + Math.log(Math.sqrt(5.0))) / Math.log(fi);
    }


    public static long[] takeBigIntegerApart(BigInteger big) {
        long[] result = new long[]{0, 1, 0};

        while (big.bitLength() + 10 > Long.bitCount(-1)) {
            result[2] += (big.mod(BigInteger.TWO).equals(BigInteger.ONE) ? 1 : 0) * result[1];
            big = big.divide(BigInteger.TWO);
            result[1] *= 2;
        }
        result[0] = big.longValueExact();
        return result;
    }

    public static BigInteger fib(BigInteger n) {
        int sign = n.compareTo(BigInteger.ZERO) < 0 ? -1 : 1;
        return BigInteger.valueOf(((long) Math.floor(nApproximation(n.multiply(BigInteger.valueOf(sign))))) * sign);

    }

    public static BigInteger combineBigInteger(long[] value) {
        return BigInteger.valueOf(value[0])
                .multiply(BigInteger.valueOf(value[1])).add(BigInteger.valueOf(value[2]));

    }


    @Test
    public void testFib0() {
        testFib(0, 0);
    }

    @Test
    public void testFib1() {
        testFib(1, 1);
    }

    @Test
    public void testFib2() {
        testFib(1, 2);
    }

    @Test
    public void testFib3() {
        testFib(2, 3);
    }

    @Test
    public void testFib4() {
        testFib(3, 4);
    }

    @Test
    public void testFib5() {
        testFib(5, 5);
    }

    private static void testFib(long expected, long input) {
        BigInteger found;
        try {
            found = fib(BigInteger.valueOf(input));
        } catch (Throwable e) {
            // see https://github.com/Codewars/codewars.com/issues/21
            throw new AssertionError("exception during test: " + e, e);
        }
        assertEquals(BigInteger.valueOf(expected), found);
    }

    @Test
    void testFactorial() {
        assertEquals(BigInteger.ONE, factorial(BigInteger.ZERO));
        assertEquals(BigInteger.ONE, factorial(BigInteger.ONE));
        assertEquals(BigInteger.TWO, factorial(BigInteger.TWO));
        assertEquals(BigInteger.valueOf(6), factorial(BigInteger.valueOf(3)));

    }

    @Test
    void test_nCr() {
        assertEquals(BigInteger.ONE, nCr(BigInteger.ONE, BigInteger.ZERO));
        assertEquals(BigInteger.ONE, nCr(BigInteger.valueOf(3), BigInteger.ZERO));
        assertEquals(BigInteger.ZERO, nCr(BigInteger.valueOf(3), BigInteger.ONE.negate()));
        assertEquals(BigInteger.valueOf(3), nCr(BigInteger.valueOf(3), BigInteger.ONE));
        assertEquals(BigInteger.valueOf(3), nCr(BigInteger.valueOf(3), BigInteger.ONE));
        assertEquals(BigInteger.ZERO, nCr(BigInteger.valueOf(3), BigInteger.valueOf(5)));
        assertEquals(BigInteger.valueOf(10), nCr(BigInteger.valueOf(5), BigInteger.valueOf(2)));
    }

    @Test
    void testFibonachi() {
        assertEquals(BigInteger.ZERO, F_n(BigInteger.ZERO));
        assertEquals(BigInteger.ONE, F_n(BigInteger.ONE));
        assertEquals(BigInteger.ONE, F_n(BigInteger.TWO));
        assertEquals(BigInteger.TWO, F_n(BigInteger.valueOf(3)));
        assertEquals(BigInteger.valueOf(21), F_n(BigInteger.valueOf(8)));
        assertEquals(BigInteger.valueOf(5), F_n(BigInteger.valueOf(5)));
        assertEquals(BigInteger.valueOf(8), F_n(BigInteger.valueOf(6)));
    }

    @Test
    void test_nApproximation() {
        assertEquals(5, Math.floor(nApproximation(BigInteger.valueOf(5))));

        assertEquals(25, Math.floor(nApproximation(BigInteger.valueOf(75025))));
        assertEquals(23, Math.floor(nApproximation(BigInteger.valueOf(28657))));

        assertEquals(15, Math.floor(nApproximation(BigInteger.valueOf(610))));
        assertEquals(9, Math.floor(nApproximation(BigInteger.valueOf(34))));
        assertEquals(7, Math.floor(nApproximation(BigInteger.valueOf(13))));


    }

    @Test
    void bigIntegerRecombination() {
        BigInteger bigInteger = new BigInteger("9223372036854775807");
        assertEquals(bigInteger, combineBigInteger(takeBigIntegerApart(bigInteger)));

        bigInteger = new BigInteger("92233720368547758070");
        assertEquals(bigInteger, combineBigInteger(takeBigIntegerApart(bigInteger)));

        bigInteger = new BigInteger("9999999999999999999999999999999998");
        assertEquals(bigInteger, combineBigInteger(takeBigIntegerApart(bigInteger)));

        bigInteger = new BigInteger("9999999999999999999999999999999999");
        assertEquals(bigInteger, combineBigInteger(takeBigIntegerApart(bigInteger)));
    }

}
