import java.util.*;
import java.io.*;

public class run2 {
    static class Edge {
        int to, dist;
        int requiredKeys;
        int gainedKeys;

        Edge(int to, int dist, int requiredKeys, int gainedKeys) {
            this.to = to;
            this.dist = dist;
            this.requiredKeys = requiredKeys;
            this.gainedKeys = gainedKeys;
        }
    }

    static class State {
        int[] positions;
        int keys;

        State(int[] positions, int keys) {
            this.positions = Arrays.copyOf(positions, positions.length);
            this.keys = keys;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof State)) return false;
            State state = (State) o;
            return keys == state.keys && Arrays.equals(positions, state.positions);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(keys);
            result = 31 * result + Arrays.hashCode(positions);
            return result;
        }
    }

    static int numKeys;
    static int allKeysMask;
    static List<List<Edge>> graph;
    static int numRobots;

    public static int solve(List<String> maze) {
        int h = maze.size();
        if (h == 0) return 0;
        int w = maze.get(0).length();
        char[][] grid = new char[h][w];
        for (int i = 0; i < h; i++) grid[i] = maze.get(i).toCharArray();

        Map<Character, int[]> keyPositions = new HashMap<>();
        List<int[]> robotPositions = new ArrayList<>();
        List<int[]> allPoints = new ArrayList<>();

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                char c = grid[i][j];
                if (c == '@') {
                    robotPositions.add(new int[]{i, j});
                    allPoints.add(new int[]{i, j});
                } else if (c >= 'a' && c <= 'z') {
                    keyPositions.put(c, new int[]{i, j});
                    allPoints.add(new int[]{i, j});
                }
            }
        }

        numRobots = robotPositions.size();
        numKeys = keyPositions.size();
        allKeysMask = (1 << numKeys) - 1;
        
        Map<String, Integer> pointIndex = new HashMap<>();
        int index = 0;
        for (int[] pos : robotPositions) pointIndex.put(pos[0] + "," + pos[1], index++);
        for (Map.Entry<Character, int[]> e : keyPositions.entrySet())
            pointIndex.put(e.getValue()[0] + "," + e.getValue()[1], index++);

        int totalPoints = index;
        graph = new ArrayList<>();
        for (int i = 0; i < totalPoints; i++) graph.add(new ArrayList<>());
        
        for (int[] from : allPoints) {
            int fromIdx = pointIndex.get(from[0] + "," + from[1]);
            bfsBetweenPoints(grid, from[0], from[1], fromIdx, pointIndex);
        }
        
        int[] robotIndexes = new int[numRobots];
        for (int i = 0; i < numRobots; i++) {
            int[] pos = robotPositions.get(i);
            robotIndexes[i] = pointIndex.get(pos[0] + "," + pos[1]);
        }

        PriorityQueue<Map.Entry<State, Integer>> queue = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
        Map<State, Integer> dist = new HashMap<>();

        State start = new State(robotIndexes, 0);
        queue.add(new AbstractMap.SimpleEntry<>(start, 0));
        dist.put(start, 0);

        while (!queue.isEmpty()) {
            Map.Entry<State, Integer> currEntry = queue.poll();
            State curr = currEntry.getKey();
            int currDist = currEntry.getValue();

            if (currDist > dist.getOrDefault(curr, Integer.MAX_VALUE)) continue;
            if (curr.keys == allKeysMask) return currDist;

            for (int r = 0; r < numRobots; r++) {
                int pos = curr.positions[r];
                for (Edge edge : graph.get(pos)) {
                    if ((curr.keys & edge.requiredKeys) == edge.requiredKeys) {
                        int nextKeys = curr.keys | edge.gainedKeys;
                        int[] newPositions = Arrays.copyOf(curr.positions, numRobots);
                        newPositions[r] = edge.to;
                        State next = new State(newPositions, nextKeys);
                        int newDist = currDist + edge.dist;
                        if (newDist < dist.getOrDefault(next, Integer.MAX_VALUE)) {
                            dist.put(next, newDist);
                            queue.add(new AbstractMap.SimpleEntry<>(next, newDist));
                        }
                    }
                }
            }
        }

        return -1;
    }

    private static void bfsBetweenPoints(char[][] grid, int sx, int sy, int fromIdx, Map<String, Integer> pointIndex) {
        int h = grid.length, w = grid[0].length;
        boolean[][] visited = new boolean[h][w];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{sx, sy, 0, 0, 0});
        visited[sx][sy] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int x = curr[0], y = curr[1], dist = curr[2], reqKeys = curr[3], gotKeys = curr[4];
            String posKey = x + "," + y;

            if (pointIndex.containsKey(posKey) && !(x == sx && y == sy)) {
                int toIdx = pointIndex.get(posKey);
                graph.get(fromIdx).add(new Edge(toIdx, dist, reqKeys, gotKeys));
                continue;
            }

            for (int[] d : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && ny >= 0 && nx < h && ny < w && !visited[nx][ny]) {
                    char c = grid[nx][ny];
                    if (c == '#') continue;
                    visited[nx][ny] = true;
                    int nextReq = reqKeys, nextGot = gotKeys;
                    if (c >= 'A' && c <= 'Z') nextReq |= (1 << (c - 'A'));
                    if (c >= 'a' && c <= 'z') nextGot |= (1 << (c - 'a'));
                    queue.add(new int[]{nx, ny, dist + 1, nextReq, nextGot});
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> maze = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            maze.add(line);
        }

        int result = solve(maze);
        System.out.println(result);
    }
}
