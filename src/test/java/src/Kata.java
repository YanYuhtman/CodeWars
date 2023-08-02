
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Kata {

    public static List<String> combinations(String str){
        ArrayList<String> outList = new ArrayList<>((int)factorial(str.length()));
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

    static final long[] _factorial_cache = new long[]{1L,1L,2L,6L,24L,120L,720L,5040L,40320L,362880L,3628800L,39916800L,479001600L,6227020800L,87178291200L,1307674368000L,20922789888000L,355687428096000L,6402373705728000L,121645100408832000L,2432902008176640000L};
    public static long factorial(long i){
        if(i <= 1)
            return 1L;
        if(_factorial_cache.length < i)
            return _factorial_cache[(int)i];

        return i * factorial(i - 1);

    }
    public static String generatePermutation(int i, int length){
        List<Character> template = new LinkedList<>();
        for(char index = '0'; index < length + 48; index++)
            template.add(index);

        StringBuilder result = new StringBuilder();

        do {
            int n = template.size();
            int A_i_k = n - 1 - (i / (int)factorial(n - 1)) % n;
            result.insert(0,template.remove(A_i_k));
        }while (template.size() > 0);

        return result.toString();
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


    @Test
    public void testPermutationGenerator(){
        String set = "0123456789";
        List<String> combinations = combinations(set);
        assertEquals(combinations.get(0),generatePermutation(0,set.length()));
        assertEquals(combinations.get(6),generatePermutation(6,set.length()));
        assertEquals(combinations.get(13),generatePermutation(13,set.length()));
        assertEquals(combinations.get(21),generatePermutation(21,set.length()));


        assertEquals(combinations.get(102),generatePermutation(102,set.length()));

        assertEquals(combinations.get(301),generatePermutation(301,set.length()));

        for(int i = 0; i< 200; i++) {
            int randomIndex = (int) (Math.random() * combinations.size());
            assertEquals(combinations.get(randomIndex), generatePermutation(randomIndex, set.length()));
        }


        for(int i = 0; i < 24; i++) {
            System.out.println("c: " + combinations.get(i));
            System.out.println("g: " + combinations.get(i));
            System.out.println();
        }
    }
    @Test
    public void testPermutationsSpeed(){
        String set = "0123456789";

        long start = System.currentTimeMillis();

        List<String> combinationList = combinations(set);

        long end = System.currentTimeMillis();
        System.out.println("DEBUG: recursive algorithm took " + (end - start) + " MilliSeconds");


        start = System.currentTimeMillis();


        List<String> generatedList = new ArrayList<>(combinationList.size());
        for(int i = 0; i < combinationList.size(); i++){
             generatedList.add(generatePermutation(i,set.length()));
        }

        end = System.currentTimeMillis();
        System.out.println("DEBUG: iterative algorithm took " + (end - start) + " MilliSeconds");

        assertEquals(combinationList,generatedList);

    }
    @Test
    public void testFactorial(){
        assertEquals (factorial(21),-4249290049419214848L);
    }
}