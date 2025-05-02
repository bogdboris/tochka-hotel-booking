import java.util.*;
import java.io.*;

public class run2 {

    private static final int[] dx = {-1, 1, 0, 0};
    private static final int[] dy = {0, 0, -1, 1};

    static class State {
        int[] x; // x-координаты роботов
        int[] y; // y-координаты роботов
        BitSet keys; // Набор собранных ключей
        int steps; // Количество шагов

        public State(int[] x, int[] y, BitSet keys, int steps) {
            this.x = x.clone();
            this.y = y.clone();
            this.keys = (BitSet) keys.clone();
            this.steps = steps;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return steps == state.steps &&
                    Arrays.equals(x, state.x) &&
                    Arrays.equals(y, state.y) &&
                    keys.equals(state.keys);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(steps);
            result = 31 * result + Arrays.hashCode(x);
            result = 31 * result + Arrays.hashCode(y);
            result = 31 * result + keys.hashCode();
            return result;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        List<char[]> grid = new ArrayList<>();
        String line;

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            grid.add(line.toCharArray());
        }

        char[][] maze = grid.toArray(new char[0][]);
        int result = solve(maze);
        System.out.println(result);
    }

    private static int solve(char[][] maze) {
        int m = maze.length;
        int n = maze[0].length;

        List<int[]> robots = new ArrayList<>();
        int totalKeys = 0;
        Map<Character, Integer> keyIndices = new HashMap<>();

        // Собираем информацию о роботах и ключах
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                char c = maze[i][j];
                if (c == '@') {
                    robots.add(new int[]{i, j});
                } else if (Character.isLowerCase(c)) {
                    keyIndices.put(c, totalKeys++);
                }
            }
        }

        if (totalKeys == 0) return 0;

        // Подготавливаем начальное состояние
        int[] startX = new int[4];
        int[] startY = new int[4];
        for (int i = 0; i < 4; i++) {
            if (i < robots.size()) {
                startX[i] = robots.get(i)[0];
                startY[i] = robots.get(i)[1];
            } else {
                startX[i] = -1;
                startY[i] = -1;
            }
        }

        State initialState = new State(startX, startY, new BitSet(totalKeys), 0);
        Queue<State> queue = new LinkedList<>();
        queue.offer(initialState);

        Set<State> visited = new HashSet<>();
        visited.add(initialState);

        while (!queue.isEmpty()) {
            State current = queue.poll();

            // Проверяем, собраны ли все ключи
            if (current.keys.cardinality() == totalKeys) {
                return current.steps;
            }

            // Пробуем переместить каждого робота
            for (int robot = 0; robot < 4; robot++) {
                if (current.x[robot] == -1) continue;

                for (int dir = 0; dir < 4; dir++) {
                    int nx = current.x[robot] + dx[dir];
                    int ny = current.y[robot] + dy[dir];

                    if (nx < 0 || nx >= m || ny < 0 || ny >= n) continue;

                    char cell = maze[nx][ny];

                    if (cell == '#') continue;
                    if (Character.isUpperCase(cell)) {
                        Integer keyIndex = keyIndices.get(Character.toLowerCase(cell));
                        if (keyIndex == null || !current.keys.get(keyIndex)) continue;
                    }

                    // Создаем новое состояние
                    int[] newX = current.x.clone();
                    int[] newY = current.y.clone();
                    newX[robot] = nx;
                    newY[robot] = ny;

                    BitSet newKeys = (BitSet) current.keys.clone();
                    if (Character.isLowerCase(cell)) {
                        Integer keyIndex = keyIndices.get(cell);
                        if (keyIndex != null) {
                            newKeys.set(keyIndex);
                        }
                    }

                    State newState = new State(newX, newY, newKeys, current.steps + 1);

                    if (!visited.contains(newState)) {
                        visited.add(newState);
                        queue.offer(newState);
                    }
                }
            }
        }

        return -1;
    }
}
