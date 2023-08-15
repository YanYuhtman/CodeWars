import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TheMillionthFibonacci {

    static long quickFibonacci(int n) {
        long[] f_n = new long[]{0, 1, 1};
        if(n <= 3)
            return f_n[n-1];

        for (int i = 1; i < n - 1;) {
            long[] tmpF_n = Arrays.copyOf(f_n, f_n.length);

            if (i % 2 == 0 && n - 1 > i + i*2) {
                f_n[0] = tmpF_n[0] * tmpF_n[0] + tmpF_n[1] * tmpF_n[1];
                f_n[1] = tmpF_n[1] * tmpF_n[2] + tmpF_n[1] * tmpF_n[0];
                f_n[2] = tmpF_n[1] * tmpF_n[1] + tmpF_n[2] * tmpF_n[2];
//                System.out.println("mult");
                i*=2;
            } else {
                i++;
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
    static int findNOf(long F_n){
        long[] f_n = new long[]{0, 1, 1};
        if(F_n < 2)
            return (int)f_n[(int)F_n] + 1;
        int i = 1;
        while (F_n > f_n[2]) {
            long[] tmpF_n = Arrays.copyOf(f_n, f_n.length);
            if(i % 2 == 0) {
                f_n[0] = tmpF_n[0] * tmpF_n[0] + tmpF_n[1] * tmpF_n[1];
                f_n[1] = tmpF_n[1] * tmpF_n[2] + tmpF_n[1] * tmpF_n[0];
                f_n[2] = tmpF_n[1] * tmpF_n[1] + tmpF_n[2] * tmpF_n[2];

                if (f_n[2] <= F_n) {
                    i *= 2;
                    continue;
                }
            }

            f_n[0] = tmpF_n[1];
            f_n[1] = tmpF_n[2];
            f_n[2] = tmpF_n[1] + tmpF_n[2];
            i++;
        }
        return i + 1;
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

        assertEquals(1, findNOf(0));
        assertEquals(2, findNOf(1));
        assertEquals(4, findNOf(3));
        assertEquals(5, findNOf(5));
        assertEquals(6, findNOf(8));
        assertEquals(7, findNOf(13));
        assertEquals(11, findNOf(89));
        assertEquals(30, findNOf(832040));
    }

    static BigInteger lastIndex = null;
    static BigInteger[] lastFbState = null;
    static BigInteger quickFibonacci(BigInteger n) {
        if(lastFbState != null && n.equals(lastIndex))
            return lastFbState[2];

        BigInteger[] f_n = new BigInteger[]{BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE};
        if(n.intValue() < 3)
            return f_n[n.intValue()];

        BigInteger i = lastIndex == null || lastIndex.compareTo(n) > 0 ? BigInteger.ONE : lastIndex.subtract(BigInteger.ONE);
        BigInteger[] tmpF_n = lastIndex == null || lastIndex.compareTo(n) > 0 ? null : lastFbState;
        while (n.subtract(BigInteger.ONE).compareTo(i) > 0) {
            if(tmpF_n == null)
                tmpF_n = Arrays.copyOf(f_n, f_n.length);
            if (i.mod(BigInteger.TWO).equals(BigInteger.ZERO) && n.subtract(BigInteger.ONE).compareTo(i.add(i.multiply(BigInteger.TWO))) > 0) {
                f_n[0] = tmpF_n[0].multiply(tmpF_n[0]).add(tmpF_n[1].multiply(tmpF_n[1]));
                f_n[1] = tmpF_n[1].multiply(tmpF_n[2]).add(tmpF_n[1].multiply(tmpF_n[0]));
                f_n[2] = tmpF_n[1].multiply(tmpF_n[1]).add(tmpF_n[2].multiply(tmpF_n[2]));
                i = i.multiply(BigInteger.TWO);
            } else {
                f_n[0] = tmpF_n[1];
                f_n[1] = tmpF_n[2];
                f_n[2] = tmpF_n[1].add(tmpF_n[2]);
                i = i.add(BigInteger.ONE);
            }
            tmpF_n = null;
        }

        lastIndex = n;
        lastFbState = f_n;
        return f_n[2];
    }

    static BigInteger findNOf(BigInteger F_n){
        BigInteger[] f_n = new BigInteger[]{BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE};
        if(F_n.compareTo(BigInteger.TWO) < 0)
            return f_n[F_n.intValue()].add(BigInteger.ONE);

        BigInteger i = BigInteger.ONE;
        while (F_n.compareTo(f_n[2]) > 0) {
            BigInteger[] tmpF_n = Arrays.copyOf(f_n, f_n.length);
            if(i.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                f_n[0] = tmpF_n[0].multiply(tmpF_n[0]).add(tmpF_n[1].multiply(tmpF_n[1]));
                f_n[1] = tmpF_n[1].multiply(tmpF_n[2]).add(tmpF_n[1].multiply(tmpF_n[0]));
                f_n[2] = tmpF_n[1].multiply(tmpF_n[1]).add(tmpF_n[2].multiply(tmpF_n[2]));

                if (F_n.compareTo(f_n[2]) > 0) {
                    i = i.multiply(BigInteger.TWO);
                    continue;
                }
            }

            f_n[0] = tmpF_n[1];
            f_n[1] = tmpF_n[2];
            f_n[2] = tmpF_n[1].add(tmpF_n[2]);
            i = i.add(BigInteger.ONE);
        }
        return i.add(BigInteger.ONE);
    }

    @Test
    void testQuickFibonachiBigIntegerTiming(){
        long start = System.currentTimeMillis();
//        for(long i = 0; i < 1_000_000; i++)
//        quickFibonacci(BigInteger.valueOf(1_088_200));
//        quickFibonacci(BigInteger.valueOf(1_088_242));
            F_n(BigInteger.valueOf(1_088_242));

        System.out.println("QuickFibonacci up to 1_000_000 took: " + (System.currentTimeMillis() - start) + "msec");
    }
    @Test
    void testQuickFibonachiBigInteger() {
        assertEquals(BigInteger.ZERO, quickFibonacci(BigInteger.ZERO));
        assertEquals(BigInteger.ONE, quickFibonacci(BigInteger.TWO));
        assertEquals(BigInteger.TWO, quickFibonacci(BigInteger.valueOf(3)));
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
        assertEquals(BigInteger.valueOf(102334155L), quickFibonacci((BigInteger.valueOf(40))));
        assertEquals(BigInteger.valueOf(2111485077978050L), quickFibonacci(BigInteger.valueOf(75)));
        assertEquals(BigInteger.valueOf(12586269025L), quickFibonacci(BigInteger.valueOf(50)));

        assertEquals(new BigInteger("222232244629420445529739893461909967206666939096499764990979600"), quickFibonacci(BigInteger.valueOf(300)));

//        assertEquals(new BigInteger("222232244629420445529739893461909967206666939096499764990979600"), quickFibonacci(BigInteger.valueOf(2_000_000)));


        assertEquals(BigInteger.valueOf(1), findNOf(BigInteger.ZERO));
        assertEquals(BigInteger.valueOf(2), findNOf(BigInteger.ONE));
        assertEquals(BigInteger.valueOf(4), findNOf(BigInteger.valueOf(3)));
        assertEquals(BigInteger.valueOf(5), findNOf(BigInteger.valueOf(5)));
        assertEquals(BigInteger.valueOf(6), findNOf(BigInteger.valueOf(8)));
        assertEquals(BigInteger.valueOf(7), findNOf(BigInteger.valueOf(13)));
        assertEquals(BigInteger.valueOf(11), findNOf(BigInteger.valueOf(89)));
        assertEquals(BigInteger.valueOf(30), findNOf(BigInteger.valueOf(832040)));
        assertEquals(BigInteger.valueOf(300), findNOf(new BigInteger("222232244629420445529739893461909967206666939096499764990979600")));
    }


    //https://www.codewars.com/kata/53d40c1e2f13e331fc000c26/train/java

    static WeakHashMap<BigInteger,BigInteger> fCache = new WeakHashMap<>(20000);
    static BigInteger factorial(BigInteger v) {
        if(v == BigInteger.ZERO || v == BigInteger.ONE)
            return BigInteger.ONE;

        if(fCache.containsKey(v))
            return fCache.get(v);

        BigInteger n = null, initValue = null;
        for(int i = 1; i < 6; i++)
            if(fCache.containsKey(n = v.subtract(BigInteger.valueOf(i)))) {
                initValue = fCache.get(n);
                n = n.add(BigInteger.ONE);
                break;
            }

        if(initValue == null) {
            n = BigInteger.TWO;
            initValue = BigInteger.ONE;
        }
        while (v.compareTo(n) >= 0) {
            initValue = initValue.multiply(n);
            fCache.put(n, initValue);
//            System.out.print("new BigInteger(\"" + fCache.get(n) + "\"), ");
            n = n.add(BigInteger.ONE);
        }

        return initValue;
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

            System.out.println(n);
            Function<BigInteger, BigInteger> f = val -> F_n(val);

            if (n.signum() < 0 && n.mod(BigInteger.TWO) != BigInteger.ZERO) {
                return f.apply(n.abs());
            } else
                return f.apply(n.abs()).multiply(BigInteger.valueOf(n.signum()));



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
        assertEquals(BigInteger.valueOf(40320), factorial(BigInteger.valueOf(8)));
        assertEquals(BigInteger.valueOf(40320), factorial(BigInteger.valueOf(8)));
        assertEquals(BigInteger.valueOf(6), factorial(BigInteger.valueOf(3)));
        assertEquals(new BigInteger("30414093201713378043612608166064768844377641568960512000000000000"), factorial(BigInteger.valueOf(50)));
        assertEquals(new BigInteger("1551118753287382280224243016469303211063259720016986112000000000000"), factorial(BigInteger.valueOf(51)));
        assertEquals(new BigInteger("720"),factorial(BigInteger.valueOf(6)));
        assertEquals(new BigInteger("2432902008176640000"),factorial(BigInteger.valueOf(20)));
        assertEquals(new BigInteger("30414093201713378043612608166064768844377641568960512000000000000"),factorial(BigInteger.valueOf(50)));
        assertEquals(new BigInteger("8320987112741390144276341183223364380754172606361245952449277696409600000000000000"),factorial(BigInteger.valueOf(60)));




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
