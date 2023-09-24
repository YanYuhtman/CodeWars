import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
//https://www.codewars.com/kata/5b5fe164b88263ad3d00250b
public class AlphametricSolver {




    public class Alphametics {
        static List<int[]> combinationsGenerator(int size, int[] from){
            ArrayList<int[]> combinations = new ArrayList<>();
            if(size == 1){
                for(int value : from)
                    combinations.add(new int[]{value});
                return combinations;
            }
            for(int value : from) {
                int[] next = new int[from.length - 1];
                int index = 0;
                for(int v : from)
                    if(v != value)
                        next[index++] = v;
                for (int[] combination : combinationsGenerator(size - 1, next)) {
                    int[] newCombination = Arrays.copyOf(combination, combination.length + 1);
                    newCombination[newCombination.length - 1] = value;
                    combinations.add(newCombination);

                }
            }
            return combinations;
        }
        static List<AlphametricChar>[] getSumVariations(char[] chars, int sum, int[] fromDigits, int addition){
            if(chars.length == 0)
                return new List[0];

            List<AlphametricChar> aChars = new ArrayList<>();
            Arrays.sort(chars);
            AlphametricChar prevChar = null;
            for (char ch : chars) {
                if(prevChar == null){
                    prevChar = new AlphametricChar(ch);
                }else if (ch == prevChar.aChar) {
                    prevChar.repeats += 1;
                }else {
                    aChars.add(prevChar);
                    prevChar = new AlphametricChar(ch);
                }
            }
            if (aChars.size() == 0 || aChars.get(aChars.size() - 1) != prevChar)
                aChars.add(prevChar);


            List<int[]> combinations = combinationsGenerator(aChars.size(),fromDigits);
            for(Iterator<int[]> cIter = combinations.iterator(); cIter.hasNext();){
                int[] combination = cIter.next();
                int _sum = 0;
                for(int i = 0; i < combination.length;i++)
                    _sum += combination[i] * aChars.get(i).repeats;

                if((_sum + addition) % 10 != sum)
                    cIter.remove();
            }

            List<AlphametricChar>[] variations = new ArrayList[combinations.size()];
            int listIndex = 0;
            for (int[] combination : combinations) {
                List<AlphametricChar> tmpList = aChars.stream().map(AlphametricChar::new).collect(Collectors.toList());
                for (int i = 0; i < tmpList.size(); i++)
                    tmpList.get(i).digitSubstitute = combination[i];
                variations[listIndex++] = tmpList;
            }
            return variations;

        }
        static class AlphametricChar{
            public final char aChar;
            public int repeats = 1;
            public int digitSubstitute = -1;

            public AlphametricChar(AlphametricChar original){
                this.aChar = original.aChar;
                this.digitSubstitute = original.digitSubstitute;
                this.repeats = original.repeats;
            }
            public AlphametricChar(char aChar) {
                this.aChar = aChar;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                AlphametricChar that = (AlphametricChar) o;

                return aChar == that.aChar;
            }

            @Override
            public int hashCode() {
                return aChar;
            }

            @Override
            public String toString() {
                return "[" + aChar + "x" + repeats + "->" + digitSubstitute + "]";
            }
        }
        private final String puzzle;
        private String result = null;
        public Alphametics(String s) {
            puzzle = s;
        }
        private boolean validateEquation(String[] words){
            try{
                int sum = 0;
                for(int i = 0; i < words.length; i++) {
                    if(words[i].charAt(0) == '0')
                        return false;
                    if(i < words.length - 1)
                        sum += Integer.parseInt(words[i]);
                }
                return sum == Integer.parseInt(words[words.length - 1]);
            }catch (NumberFormatException e){
                return false;
            }
        }

        private String[] replaceAll(String[] words, char ch, int d){
            String[] result = new String[words.length];
            for(int i = 0; i < words.length;i++){
                result[i] = words[i].replace(ch,Character.forDigit(d,10));
            }
            return result;
        }
        private int[] copyExclude(int[] original, int exclude){
            int contains = 0;
            for(int o : original)
                if(o == exclude)
                    contains +=1;

            int[] result = new int[original.length - contains];
            for(int i=0,j=0; i < original.length;i++){
                if(exclude != original[i])
                    result[j++] = original[i];

            }
            return result;
        }

        private char[] collectFromCombinations(String[] words, int rLen){
            char[] collectForCombinations = new char[words.length];
            int i = 0;
            for(String word : words){
                if(word.length() >= rLen) {
                    char ch = word.charAt(word.length() - rLen);
                    if (Character.isLetter(ch))
                        collectForCombinations[i++] = ch;
                }
            }
            return Arrays.copyOf(collectForCombinations,i);
        }

        private void solve(String puzzle, int[] availableDigits, int rLen, int addition){
//            System.out.println(puzzle);

            String[] words = puzzle.split("\\s*[+=]\\s*");
            if(!puzzle.matches(".*[A-Z].*")){
                if(validateEquation(words))
                    result = puzzle;
                return;
            }
            if(rLen > words[words.length - 1].length()) {
                if(validateEquation(words))
                    result = puzzle;
                return;
            }
            String eqResult = words[words.length - 1];
            char eqChar = eqResult.charAt(eqResult.length() - rLen);
            for(int digit : Character.isDigit(eqChar) ? new int[]{Character.digit(eqChar,10)} : availableDigits){
                String[] _words = replaceAll(words,eqChar,digit);
                int partialSum = 0;
                boolean isFullSum = true;
                for (int i = 0; i < _words.length - 1; i++)
                    if (rLen <= _words[i].length()) {
                        char ch = _words[i].charAt(_words[i].length() - rLen);
                        if (Character.isDigit(ch))
                            partialSum += Character.digit(ch, 10);
                        else
                            isFullSum = false;
                    }
                if(isFullSum && (partialSum + addition) % 10 == digit){
                    solve(puzzle.replace(eqChar,Character.forDigit(digit,10)), availableDigits, rLen + 1, (partialSum + addition) / 10);
                    continue;
                }
                List<AlphametricChar>[] aCharsLists = getSumVariations(collectFromCombinations(_words,rLen),digit
                        ,copyExclude(availableDigits,digit),addition + partialSum);

                for(List<AlphametricChar> aCharsList : aCharsLists){
                    String _puzzle = puzzle.replace(eqChar,Character.forDigit(digit,10));
                    int[] _availableDigits = copyExclude(availableDigits,digit);
                    int sum = 0;
                    for (AlphametricChar aChar : aCharsList) {
                        _puzzle = _puzzle.replace(aChar.aChar, Character.forDigit(aChar.digitSubstitute, 10));
                        _availableDigits = copyExclude(_availableDigits,aChar.digitSubstitute);
                        sum+= aChar.digitSubstitute * aChar.repeats;
                    }
                    solve(_puzzle,_availableDigits,rLen+1,(partialSum + sum)/10);
                }

            }
        }

        public String solve() {
           solve(puzzle,new int[]{0,1,2,3,4,5,6,7,8,9},1,0);
           return result;
        }

    }

    private static final String[][] fixedTests = {
            {"SEND + MORE = MONEY",                  "9567 + 1085 = 10652"},
            {"ZEROES + ONES = BINARY",               "698392 + 3192 = 701584"},
            {"COUPLE + COUPLE = QUARTET",            "653924 + 653924 = 1307848"},
            {"DO + YOU + FEEL = LUCKY",              "57 + 870 + 9441 = 10368"},
            {"ELEVEN + NINE + FIVE + FIVE = THIRTY", "797275 + 5057 + 4027 + 4027 = 810386"},
    };

    private static final String[][] failedTests = {
            {"XI + QGG = EQX", "?"},
            {"CLARA + DIANE = LADIES", "?"},
    };

    private void _testVariations(int expected, List<Alphametics.AlphametricChar>[] variations){
        for(List<Alphametics.AlphametricChar> l : variations){
            l.stream().forEach(System.out::print);
            System.out.println();
        }
        System.out.println();

        assertEquals(expected,variations.length);

    }
    @Test
    public void testVariations(){
       _testVariations(1, Alphametics.getSumVariations(new char[]{'A'},1,new int[]{0,1},0));
       _testVariations(2, Alphametics.getSumVariations(new char[]{'A','B'},1,new int[]{0,1},0));
       _testVariations(2, Alphametics.getSumVariations(new char[]{'A','B'},1,new int[]{2,9},0));
       _testVariations(2, Alphametics.getSumVariations(new char[]{'A','A','B'},3,new int[]{0,1,2},0));
       _testVariations(6, Alphametics.getSumVariations(new char[]{'A','B','C'},0,new int[]{9,8,3},0));
        _testVariations(0, Alphametics.getSumVariations(new char[]{'A','A','C'},0,new int[]{9,8,3},0));
        _testVariations(4, Alphametics.getSumVariations(new char[]{'A','B'},1,new int[]{0,1,2,9},0));
        _testVariations(4, Alphametics.getSumVariations(new char[]{'A','B'},2,new int[]{0,1,2,9},1));
    }

    private void _testCombinations(int expected, List<int[]> combinations){
        combinations.stream().forEach(c->{
            System.out.print("[");
            Arrays.stream(c).forEach(System.out::print);
            System.out.print("],");

        });
        System.out.println();

        assertEquals(expected,combinations.size());

    }
    @Test
    public void testCombinations(){

        _testCombinations(2, Alphametics.combinationsGenerator(2,new int[]{0,1}));
        _testCombinations(6, Alphametics.combinationsGenerator(2, new int[]{0,1,2}));
        _testCombinations(20, Alphametics.combinationsGenerator(2,  new int[]{0,1,2,3,4}));
        _testCombinations(60, Alphametics.combinationsGenerator(3, new int[]{0,1,2,3,4}));

    }

    @Test
    public void FailedTests(){
        for (String[] test: failedTests)
            assertEquals(test[1], new Alphametics(test[0]).solve());
    }

    @Test
    public void FixedTests() {
        for (String[] test: fixedTests)
            assertEquals(test[1], new Alphametics(test[0]).solve());
    }
}

