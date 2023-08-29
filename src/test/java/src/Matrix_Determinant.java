import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Matrix_Determinant {

    //https://www.codewars.com/kata/52a382ee44408cea2500074c/java

    public static int detR(int[][] matrix, int x, int y, int skipRow){
        if(matrix.length - x == 1)
            return matrix[x][y];
        if(matrix.length -x == 2)
            return matrix[x][y] * matrix[x + 1][y + 1] - matrix[x][y + 1] * matrix[x + 1][y];
        int result = 0;
        for(int i = 0; i < matrix.length; i++){
            if(i != skipRow)
                result += matrix[x + i][y] * detR(matrix,x+1,y+1, x + i) * (i % 2 == 0 ? 1 : -1);
        }
        return result;
    }
    public static int[][]extractSubMatrix(int[][] matrix, int skipRow){
        int[][] result = new int[matrix.length - 1][matrix.length -1];
        for(int x = 1; x < matrix.length; x++)
            for(int y = skipRow == 0 ? 1 : 0,y2 = 0; y < matrix.length; y = (y + 1  == skipRow ? y + 2 : y + 1),y2++){
                result[x-1][y2] = matrix[x][y];
            }
        return result;
    }
    public static int determinant(int[][] matrix) {
        if(matrix.length == 1)
            return matrix[0][0];
        if(matrix.length == 2)
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        int sum = 0;
        for(int i = 0; i < matrix.length;i++){
            sum += matrix[0][i] * determinant(extractSubMatrix(matrix,i)) * (i % 2 == 0 ? 1 : -1);
        }

        return sum;
    }

    private static int[][][] matrix =
            { {{1}},
            {{1, 3}, {2,5}},
            {{2,5,3}, {1,-2,-1}, {1, 3, 4}}};

    private static int[] expected = {1, -1, -20};

    private static String[] msg = {"Determinant of a 1 x 1 matrix yields the value of the one element",
            "Should return 1 * 5 - 3 * 2 == -1 ",
            ""};

    @Test
    public void sampleTests() {
        for (int n = 0 ; n < expected.length ; n++)
            Assertions.assertEquals(expected[n], determinant(matrix[n]),msg[n]);
    }
}
