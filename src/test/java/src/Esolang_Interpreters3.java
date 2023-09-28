import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Esolang_Interpreters3 {
    //https://www.codewars.com/kata/5868a68ba44cfc763e00008d/train/java
    public class Paintfuck {

        public static String interpreter(String code, int iterations, int width, int height) {
            System.out.println("Code: " + code + " iterations: " + iterations + " size:" + width + "x" + height );
            int[][] tape = new int[height][width];
            int col = 0,row = 0;
            try {
                for (int i = 0; i < code.length() && iterations > 0; i++) {
                    row = row < 0 ?  height - 1 : (row >= height ? 0 : row);
                    col = col < 0 ?  width - 1 : (col >= width ? 0 : col);
                    int value = tape[row][col];
                    iterations--;
                    switch (code.charAt(i)) {
                        case '*' -> tape[row][col] = ~value & 1;
                        case 'e' -> col++;
                        case 'w' -> col--;
                        case 'n' -> row--;
                        case 's' -> row++;
                        case '[' -> {
                            if (value == 0) {
                                Stack<Character> stack = new Stack<>();
                                while (true) {
                                    char command = code.charAt(++i);
                                    if (command == ']') {
                                        if (stack.isEmpty())
                                            break;
                                        else
                                            stack.pop();
                                    } else if (command == '[')
                                        stack.add(command);
                                }
                            }
                        }
                        case ']' -> {
                            if (value == 1) {
                                Stack<Character> stack = new Stack<>();
                                while (true) {
                                    char command = code.charAt(--i);
                                    if (command == '[') {
                                        if (stack.isEmpty()) {
                                            break;
                                        }
                                        else
                                            stack.pop();
                                    } else if (command == ']')
                                        stack.push(command);
                                }
                            }
                        }
                        default -> iterations++;
                    }


                }
            }catch (IndexOutOfBoundsException e){
                throw e;
            }
            String tmpS = Arrays.stream(tape).map(ints -> Arrays.stream(ints).mapToObj(Integer::toString).collect(Collectors.joining()) + "\r\n" )
                    .collect(Collectors.joining()).trim();
            System.out.println(tmpS);
            return tmpS;
        }
    }
    @Test
    public void examples() {
        assertEquals( "000000\r\n000000\r\n000000\r\n000000\r\n000000\r\n000000\r\n000000\r\n000000\r\n000000", Paintfuck.interpreter("*e*e*e*es*es*ws*ws*w*w*w*n*n*n*ssss*s*s*s*", 0, 6, 9),"Your interpreter should initialize all cells in the datagrid to 0");
        assertEquals( "111100\r\n000000\r\n000000\r\n000000\r\n000000\r\n000000\r\n000000\r\n000000\r\n000000", Paintfuck.interpreter("*e*e*e*es*es*ws*ws*w*w*w*n*n*n*ssss*s*s*s*", 7, 6, 9),"Your interpreter should adhere to the number of iterations specified");
        assertEquals( "111100\r\n000010\r\n000001\r\n000010\r\n000100\r\n000000\r\n000000\r\n000000\r\n000000", Paintfuck.interpreter("*e*e*e*es*es*ws*ws*w*w*w*n*n*n*ssss*s*s*s*", 19, 6, 9),"Your interpreter should traverse the 2D datagrid correctly");
        assertEquals( "111100\r\n100010\r\n100001\r\n100010\r\n111100\r\n100000\r\n100000\r\n100000\r\n100000", Paintfuck.interpreter("*e*e*e*es*es*ws*ws*w*w*w*n*n*n*ssss*s*s*s*", 42, 6, 9),"Your interpreter should traverse the 2D datagrid correctly for all of the \"n\", \"e\", \"s\" and \"w\" commands");
        assertEquals( "111100\r\n100010\r\n100001\r\n100010\r\n111100\r\n100000\r\n100000\r\n100000\r\n100000", Paintfuck.interpreter("*e*e*e*es*es*ws*ws*w*w*w*n*n*n*ssss*s*s*s*", 100, 6, 9),"Your interpreter should terminate normally and return a representation of the final state of the 2D datagrid when all commands have been considered from left to right even if the number of iterations specified have not been fully performed");
    }

    @Test
    public void failintTests(){

        assertEquals("10000\\r\\n01000\\r\\n00100\\r\\n00000\\r\\n00000\\r\\n00000",Paintfuck.interpreter("*[es*]",9,5,6));
    }
}
