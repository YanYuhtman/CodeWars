//import com.google.common.primitives.Ints;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CompleteBinaryTree {


    public int[] apply(int[] input) {
        if(input.length == 1)
            return input;
        if(input.length == 2)
            return new int[]{input[1],input[0]};

        int n =  1 << (int)Math.ceil(Math.log(input.length) / Math.log(2.0));

        ArrayList<Integer> arrayList = new ArrayList<Integer>(Arrays.stream(input).boxed().toList());

        List<Integer> result = new ArrayList<>(arrayList.size());
        for(int j = 0; !arrayList.isEmpty() ; j++) {
            int i = (j == 0) ? arrayList.size() - (n - arrayList.size()) : arrayList.size() - 1;
            for (; i >= 0; i-=2) {
                result.add(0, arrayList.get(i));
                arrayList.remove(i);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    @Test
    public void testSingleNodeTree() {
        int[] input = new int[]{1};
        int[] expected = new int[]{1};
        assertArrayEquals(expected, new CompleteBinaryTree().apply(input));
    }

    @Test
    public void testTwoNodeTree() {
        int[] input = new int[]{1, 2};
        int[] expected = new int[]{2, 1};
        assertArrayEquals(expected, new CompleteBinaryTree().apply(input));
    }

    @Test
    public void testSixNodeTree() {
        int[] input = new int[]{1, 2, 2, 6, 7, 5};
        int[] expected = new int[]{6, 2, 5, 1, 2, 7};
        assertArrayEquals(expected, new CompleteBinaryTree().apply(input));
    }

    @Test
    public void testTenNodeTree() {
        int[] input = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] expected = new int[]{7, 4, 9, 2, 6, 8, 10, 1, 3, 5};
        assertArrayEquals(expected, new CompleteBinaryTree().apply(input));
    }

}
