import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Psychic {


    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for (;;) {
            long current = seedUniquifier.get();
            long next = current * 1181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }

    private static final AtomicLong seedUniquifier
            = new AtomicLong(8682522807148012L);




    static Random customRandom = null;
    public static double guess() {

        if(customRandom == null) {
            long nanoTime = System.nanoTime();
            double systemRandom = java.lang.Math.random();
            long nanoAfterTime = System.nanoTime();

            for (long i = nanoTime; i <= nanoAfterTime; i++) {
                customRandom = new Random(3447679086515839964L ^ i);
                double customRandomValue = customRandom.nextDouble();

                if (systemRandom == customRandomValue)
                    return customRandom.nextDouble();

            }
        }
        return customRandom.nextDouble();
    }

    @Test
    public void testRandom() {
        assertEquals(Psychic.guess(), java.lang.Math.random(), 0);
        assertEquals(Psychic.guess(), java.lang.Math.random(), 0);
    }

}
