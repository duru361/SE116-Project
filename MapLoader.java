package objectville;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class MapLoader {

    private MapLoader() {}

    public static Cell[][] load(String path) throws IOException {
        List<char[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
                    line = line.substring(1);
                }
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#")) continue;
                rows.add(parseRow(line));
            }
        }
        if (rows.isEmpty()) throw new IOException("Map file is empty: " + path);

        int height = rows.size();
        int width = 0;
        for (char[] r : rows) width = Math.max(width, r.length);

        Cell[][] grid = new Cell[height][width];
        for (int r = 0; r < height; r++) {
            char[] row = rows.get(r);
            for (int c = 0; c < width; c++) {
                char sym = (c < row.length) ? row[c] : 'E';
                grid[r][c] = CellFactory.create(sym, r, c);
            }
        }
        return grid;
    }

    private static char[] parseRow(String line) {
        String body = line.replaceAll("\\s+$", "");
        if (body.contains(" ") || body.contains("\t") || body.contains(",")) {
            String[] tokens = body.trim().split("[\\s,]+");
            char[] cells = new char[tokens.length];
            for (int i = 0; i < tokens.length; i++) cells[i] = tokens[i].charAt(0);
            return cells;
        }
        return body.toCharArray();
    }
}
