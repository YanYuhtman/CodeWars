
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            List<String> combinations = combinations(str.substring(1));
            for(String s: combinations){
                for(int i = 0; i <= s.length(); i++) {
                    outList.add(new StringBuilder(s).insert(i,str.charAt(0)).toString());
                }
            }
        }
        return outList;
    }

    public static long nextBiggerNumber (long n)
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