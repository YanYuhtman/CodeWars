import com.sun.source.tree.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testng.collections.Lists;

import java.security.InvalidParameterException;
import java.util.*;

public class Transforming_Maze {

    //https://www.codewars.com/kata/5b86a6d7a4dcc13cd900000b/kotlin
    public class MazeSolver {
        public enum Direction {
             N(0x8), S(0x2),E(0x1), W(0x4) ;
            Direction(int bitwise){
                this.bitwise = bitwise;
            }
            public final int bitwise;
            public Direction getInverse(){
                return switch (this){
                    case E -> W;
                    case N -> S;
                    case S -> N;
                    case W -> E;
                };
            }

        }

        int[][] rotateMaze(int[][] maze, int times){
            int[][] newMaze = new int[maze.length][maze[0].length];
            for(int k = 0; k <= times; k++){
                for(int i = 0; i < maze.length; i++)
                    for(int j = 0; j < maze[i].length;j++)
                        newMaze[i][j] = k == 0 || newMaze[i][j] < 0 ? maze[i][j]
                                : ((maze[i][j] << 1 | maze[i][j] >> 3) & 0xf);
            }
            return newMaze;
        }

        int[] getEntrance(int[][] maze ){
            for(int i = 0; i < maze.length;i++)
                for(int j = 0; j < maze[i].length;j++)
                    if(maze[i][j] == -1) return new int[]{i,j};
            throw new InvalidParameterException("Maze has no entrance");
        }

        int[] makeStep(int[][] maze, int[] current, Direction direction){
            int[] nextPos = null;
            switch (direction){
                case N: nextPos = new int[]{current[0] - 1, current[1]}; break;
                case S: nextPos = new int[]{current[0] + 1, current[1]}; break;
                case W: nextPos = new int[]{current[0], current[1] - 1}; break;
                case E: nextPos = new int[]{current[0], current[1] + 1}; break;
            }
            if((nextPos[0] < 0 || nextPos[0] >= maze.length || nextPos[1] < 0 || nextPos[1] >= maze[0].length)
                || (maze[current[0]][current[1]] > 0 && (maze[current[0]][current[1]] & direction.bitwise) != 0)
                || (maze[nextPos[0]][nextPos[1]] < 0 && maze[nextPos[0]][nextPos[1]] != -2)
                || (maze[nextPos[0]][nextPos[1]] > 0 && (maze[nextPos[0]][nextPos[1]] & direction.getInverse().bitwise) != 0)
            )return null;
            return nextPos;
        }

        void findPath(int[][] maze, int[] pos, int rotations, ArrayList<String> directions){
            if(results.size() > 1 && directions.size() > results.first().size())
                return;
            if(maze[pos[0]][pos[1]] == -2) {
                results.add(directions);
                return;
            }

            for(Direction direction : Direction.values()){
                int[] newPos = makeStep(maze,pos,direction);
                if(newPos != null){
                    ArrayList<String> _directions = new ArrayList<>(directions);
                    _directions.set(_directions.size() -1 ,_directions.get(_directions.size() -1) + direction);
                    int[][] _maze = rotateMaze(maze,0);
                    _maze[pos[0]][pos[1]] = -1;
                    findPath(_maze,newPos,0,_directions);
                }
            }
            if(rotations < 4) {
                ArrayList<String> _directions = new ArrayList<>(directions);
                _directions.add("");
                findPath(rotateMaze(maze, rotations + 1), pos,rotations + 1,_directions);
            }
        }

        private SortedSet<ArrayList<String>> results = new TreeSet<ArrayList<String>>(new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return o1.size() - o2.size();
            }
        });

        private int[][] originalMaze;
        public MazeSolver(int[][] maze) {
            originalMaze = maze;
        }

        public List<String> solve() {
//            Arrays.stream(originalMaze).forEach(it-> {
//                Arrays.stream(it).forEach(d->System.out.printf("%4d",d));
//                System.out.println();
//            });

            int[] startPosition = getEntrance(originalMaze);
            ArrayList<String> emptyDirections = new ArrayList<>();
            emptyDirections.add("");
            findPath(originalMaze,startPosition,0,emptyDirections);
            return results.first();
        }
    }


    final static private int[][][] example_tests = {
            {
                    {4,2,5,4},
                    {4,15,11,1},
                    {-1,9,6,8},
                    {12,7,7,-2}
            },
            {
                    {6,3,10,4,11},
                    {8,10,4,8,5},
                    {-1,14,11,3,-2},
                    {15,3,4,14,15},
                    {14,7,15,5,5}
            },
            {
                    {9,1,9,0,13,0},
                    {14,1,11,2,11,4},
                    {-1,2,11,0,0,15},
                    {4,3,9,6,3,-2}
            },
            {
                    {-1,6,12,15,11},
                    {8,7,15,7,10},
                    {13,7,13,15,-2},
                    {11,10,8,1,3},
                    {12,6,9,14,7}
            },
            {
                    {6,3,0,9,14,13,14},
                    {-1,14,9,11,15,14,15},
                    {2,15,0,12,6,15,-2},
                    {4,10,7,6,15,5,3},
                    {7,3,13,13,14,7,0}
            }
    };

    final static private  List<List<String>> example_sols = Arrays.asList(
            Arrays.asList("NNE", "EE", "S", "SS"),
            Arrays.asList("", "", "E", "", "E", "NESE"),
            Arrays.asList("E", "SE", "", "E", "E", "E"),
            null,
            null
    );

    @Test
    public void exampleTests() {
        for (int i=0 ; i < example_sols.size() ; i++) {
            MazeSolver mazeSolver = new MazeSolver(example_tests[i]);
            if(example_sols.get(i) == null)
                Assertions.assertNull(mazeSolver.solve());
            else
                Assertions.assertFalse(mazeSolver.solve().retainAll(example_sols.get(i)));
        }
    }
}
