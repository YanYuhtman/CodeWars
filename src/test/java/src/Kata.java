
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Kata {

    public static List<String> combinations(String str){
        ArrayList<String> outList = new ArrayList<>();
        if(str.length() < 2) {
            outList.add(str);
            return outList;
        }else if(str.length() == 2){
            outList.add(str);
            outList.add(new StringBuilder().append(str.charAt(1)).append(str.charAt(0)).toString());
        }else{
            for(int i = str.length()-1; i >= 0; i--){
                for(String prefix : combinations(str.substring(0,i) + str.substring(i+1))) {
                    outList.add(prefix + str.charAt(i));
                }
            }
        }
        return outList;
    }

    public static List<String> combinationSet = null;//combinations("0123456789");

    public static long nextBiggerNumber (long n) {
        long start = System.currentTimeMillis();

        if(combinationSet == null){
            combinationSet = combinations("0123456789");
            System.out.println("Compilation of combination set took: " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();
        }

        if(n / 10L == 0)
            return -1;
        long result = -1;
        long distance = Long.MAX_VALUE;

        char[] numArray = Long.toString(Math.abs(n)).toCharArray();
        char[] tmpArray = new char[numArray.length];
        try {
            for (String combination : combinationSet) {
                for (int i = 0; i < numArray.length; i++) {
                    try {
                        tmpArray[i] = numArray[combination.charAt(i) - 48];
                    } catch (IndexOutOfBoundsException e) {
                        return result;
                    }
                }
                long val = Long.parseLong(new String(tmpArray)) * (n < 0 ? -1 : 1);
                if (val - n > 0 && val - n < distance) {
                    distance = val - n;
                    result = val;
                }
            }
            System.out.println("Evaluation of vale: " + n + " to: " + result + " took:"  + (System.currentTimeMillis() - start));
            return result;
        }catch (Exception e){
            return -2;
        }

    }

    public static long nextBiggerNumber2 (long n)
    {
        if(n / 10L == 0)
            return -1;
        Optional<Long> result = combinations(Long.toString(Math.abs(n)))
                .stream().map(Long::parseLong)
                .sorted((o1, o2) -> {
                    int r = 0;
                    if(o1 > o2) r = 1;
                    else if(o1 < o2) r = -1;
                    return n < 0 ? r *= -1 : r;
                })
                .reduce((acc, l) -> {
                    if(n > 0)
                        if (l == n) return l; else if (acc == n) return l; else return acc;
                    else
                        if(acc == -n) return l; else if (l == -n) return l; else return acc;

                });
        return n < 0 ? result.get() * -1 : result.get();

    }
    @Test
    public void timeoutTest(){
        long start = System.currentTimeMillis();

        for(int i = 0; i < 50; i++)
            nextBiggerNumber(1000000000L + (long) (Math.random() * (9999999999L - 1000000000L )));

        long end = System.currentTimeMillis();
        System.out.println("DEBUG: Logic A took " + (end - start) + " MilliSeconds");
    }

    @Test
    public void basicTests() {
        long start = System.currentTimeMillis();

        assertEquals(-1, Kata.nextBiggerNumber(9));
        assertEquals(21, Kata.nextBiggerNumber(12));
        assertEquals(531, Kata.nextBiggerNumber(513));
        assertEquals(2071, Kata.nextBiggerNumber(2017));
        assertEquals(441, Kata.nextBiggerNumber(414));
        assertEquals(414, Kata.nextBiggerNumber(144));
        assertEquals(19009, Kata.nextBiggerNumber(10990));
        assertEquals(-2017, Kata.nextBiggerNumber(-2071));


        assertEquals(1079914539, Kata.nextBiggerNumber(1079914395));
        assertEquals(123456798, Kata.nextBiggerNumber(123456789));

        assertEquals(1234567908, Kata.nextBiggerNumber(1234567890));



        long end = System.currentTimeMillis();
        System.out.println("DEBUG: Logic A took " + (end - start) + " MilliSeconds");
    }
}