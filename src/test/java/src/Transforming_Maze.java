import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
            for (int i = 0; i < maze.length; i++)
                for (int j = 0; j < maze[i].length; j++) {
                    newMaze[i][j] = maze[i][j];
                    for (int k = 0; k <= times; k++)
                        newMaze[i][j] = k == 0 || newMaze[i][j] < 0 ? maze[i][j]
                                : ((newMaze[i][j] << 1 | newMaze[i][j] >> 3) & 0xf);
                }
            return newMaze;
        }

        int[] getEntrance(int[][] maze ){
            for (int i = 0; i < maze.length; i++)
                if (maze[i][0] == -1)
                    return new int[]{i, 0};
            return new int[]{0,0};
        }

        int[] checkStep(int[][] maze, int[] current, Direction direction){
            int[] nextPos = null;
            switch (direction){
                case N: nextPos = new int[]{current[0] - 1, current[1]}; break;
                case S: nextPos = new int[]{current[0] + 1, current[1]}; break;
                case W: nextPos = new int[]{current[0], current[1] - 1}; break;
                case E: nextPos = new int[]{current[0], current[1] + 1}; break;
            }
            if((nextPos[0] < 0 || nextPos[0] >= maze.length || nextPos[1] < 0 || nextPos[1] >= maze[0].length)
                || isVisited(maze, nextPos)
                || (maze[current[0]][current[1]] > 0 && (maze[current[0]][current[1]] & direction.bitwise) != 0)
                || (maze[nextPos[0]][nextPos[1]] < 0 && maze[nextPos[0]][nextPos[1]] != -2)
                || (maze[nextPos[0]][nextPos[1]] > 0 && (maze[nextPos[0]][nextPos[1]] & direction.getInverse().bitwise) != 0)
            )return null;
            return nextPos;
        }
        void setVisited(int[][] maze, int[] pos){
            if(maze[pos[0]][pos[1]] >= 0)
                maze[pos[0]][pos[1]] = maze[pos[0]][pos[1]] | 16;
        }
        boolean isVisited(int[][] maze, int[] pos) {
            return (maze[pos[0]][pos[1]] > 0 &&  (maze[pos[0]][pos[1]] & 16) != 0)
                    || maze[pos[0]][pos[1]] == -1;
        }

        void findPathRecursive(int[][] maze, int[] pos, int rotations, ArrayList<String> directions){
            if(results.size() > 1 && directions.size() > results.first().size())
                return;
            if(maze[pos[0]][pos[1]] == -2) {
                results.add(directions);
                return;
            }

            for(Direction direction : Direction.values()){
                int[] newPos = checkStep(maze,pos,direction);
                if(newPos != null){
                    ArrayList<String> _directions = new ArrayList<>(directions);
                    _directions.set(_directions.size() - 1 ,_directions.get(_directions.size() -1) + direction);
                    int[][] _maze = rotateMaze(maze,0);
                    _maze[pos[0]][pos[1]] = -1;
                    findPathRecursive(_maze,newPos,0,_directions);
                }
            }
            if(rotations < 4) {
                ArrayList<String> _directions = new ArrayList<>(directions);
                _directions.add("");
                findPathRecursive(rotateMaze(maze, rotations + 1), pos,rotations + 1,_directions);
            }
        }

        private SortedSet<ArrayList<String>> results = new TreeSet<ArrayList<String>>(new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return o1.size() - o2.size();
            }
        });

        ArrayList<String> findPath(int[] currentPos){

            Queue<Object[]> paths = new PriorityQueue<Object[]>(new Comparator<Object[]>() {
                @Override
                public int compare(Object[] o1, Object[] o2) {
                    return ((ArrayList<String>)o1[0]).size() -  ((ArrayList<String>)o2[0]).size();
                }
            });
            paths.add(new Object[]{new ArrayList<String>(List.of("")) ,currentPos});
            paths.add(new Object[]{new ArrayList<String>(List.of("", "")) ,currentPos});
            paths.add(new Object[]{new ArrayList<String>(List.of("", "", "")) ,currentPos});
            paths.add(new Object[]{new ArrayList<String>(List.of("", "", "","")) ,currentPos});


            while (!paths.isEmpty()) {
                ArrayList<String> path = (ArrayList<String>) paths.peek()[0];
                currentPos = (int[]) paths.poll()[1];
                int rotation = (path.size() - 1) % 4;

                if(maze[rotation][currentPos[0]][currentPos[1]] == -2)
                    return path;

                for (Direction direction : Direction.values()) {
                    int[] nextPos = checkStep(maze[rotation], currentPos, direction);
                    if (nextPos != null) {
                        ArrayList<String> _path = new ArrayList<>(path);
                        _path.set(_path.size() - 1, _path.get(_path.size() - 1) + direction);
                        paths.add(new Object[]{_path, nextPos});

                        setVisited(maze[rotation], currentPos);
                    }
                    ArrayList<String> _path = new ArrayList<>(path);
                    for(int i = 1; i < 4; i++) {
                        _path.add("");
                        int[][] _maze = maze[(rotation + i) % 4];
                        if (!isVisited(_maze, currentPos)) {
                            paths.add(new Object[]{new ArrayList<>(_path), currentPos});
                            setVisited(_maze, currentPos);
                        }
                    }
                }
            }

            return null;
        }

        private int[][][] maze = new int[4][][];
        public MazeSolver(int[][] maze) {
            for(int i = 0; i < 4; i++)
                this.maze[i] = rotateMaze(maze,i);

        }

        public List<String> solve() {
            Arrays.stream(maze[0]).forEach(it-> {
                Arrays.stream(it).forEach(d->System.out.printf("%4d",d));
                System.out.println();
            });
            System.out.println("\n");

            int[] startPosition = getEntrance(maze[0]);
            return findPath(startPosition);
//            ArrayList<String> emptyDirections = new ArrayList<>();
//            emptyDirections.add("");
//
//            findPathRecursive(maze[0],startPosition,0,emptyDirections);
//            return results.size() == 0 ? null : results.first();
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
            Arrays.asList("NNE", "EES", "S", "S"),
            Arrays.asList("", "", "E", "", "E", "NESE"),
            Arrays.asList("E", "SE", "", "E", "E", "E"),
            null,
            null
    );

    @Test
    public void shouldBeSolution(){
        int[][] puzle = {
                {-1, 13, 5, 6, 5, -2},
                {10, 10, 12, 10, 13, 3},
                {12, 4, 13, 8, 3, 11},
                {9, 13, 4, 11, 10, 5},
                {14, 2, 4, 1, 1, 9},
                {7, 15, 10, 13, 3, 15},
                {5, 10, 9, 6, 3, 5},
                {12, 5, 12, 4, 9, 4}
        };
        List<String> solution = new MazeSolver(puzle).solve();
        Assertions.assertNotNull(solution);
    }



    @Test
    public void largerNumberOfIterations(){
//        Your solution completes the task in 13 iterations.
//                This test can be completed in 12 iterations.
//                Here is a valid solution: ["", "E", "", "E", "", "", "E", "EE", "", "SSE", "ESSS", "S"]
//        Your solution: ["S", "", "S", "", "S", "ES", "", "SEES", "E", "NE", "E", "", "ES"]

        int[][] puzle = {
                {-1, 13, 1, 12, 5, 2, 12, 9},
                {7, 14, 7, 7, 10, 8, 9, 13},
                {12, 12, 12, 8, 1, 3, 9, 2},
                {1, 0, 8, 7, 9, 13, 15, 4},
                {15, 10, 5, 11, 7, 15, 12, 4},
                {6, 12, 4, 3, 2, 1, 14, 8},
                {15, 0, 6, 6, 8, 9, 11, -2},
                {0, 13, 7, 4, 3, 8, 11, 6},
        };
        List<String> solution = new MazeSolver(puzle).solve();
        Assertions.assertTrue(solution.size() == 12);
    }

    @Test
    public void exampleTests() {
        for (int i=0 ; i < example_sols.size() ; i++) {
            MazeSolver mazeSolver = new MazeSolver(example_tests[i]);
            List<String> solved = mazeSolver.solve();
            if(example_sols.get(i) == null)
                Assertions.assertNull(solved);
//            else
//                Assertions.assertFalse(solved.retainAll(example_sols.get(i)));
        }
    }
}
