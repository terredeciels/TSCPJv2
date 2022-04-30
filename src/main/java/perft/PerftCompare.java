package perft;

import tools.FenToBoard;
import tscp.Constantes;
import tscp.Coups;
import tscp.Position;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class PerftCompare implements Constantes {
    //new brancheA1
    public static void main(String[] args) throws IOException {
        int maxDepth = 4;
        File directory = new File(".");
        FileReader fileReader = new FileReader(directory.getCanonicalPath() + "/src/main/java/perft/perftsuite.epd");
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        int passes = 0;
        int fails = 0;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length >= 3) {
                String fen = parts[0].trim();
                for (int i = 1; i < parts.length; i++) {
                    if (i > maxDepth) {
                        break;
                    }
                    String entry = parts[i].trim();
                    String[] entryParts = entry.split(" ");
                    int perftResult = Integer.parseInt(entryParts[1]);

                    Position position = FenToBoard.toBoard(fen);

                    PerftResult result = Perft.perft(position, i);
                    if (perftResult == result.moveCount) {
                        passes++;
                        System.out.println("PASS: " + fen + ". Moves " + result.moveCount + ", depth " + i);
                    } else {
                        fails++;
                        System.out.println("FAIL: " + fen + ". Moves " + result.moveCount + ", depth " + i);
                        break;
                    }
                }
            }
        }

        System.out.println("Passed: " + passes);
        System.out.println("Failed: " + fails);
    }

    static class PerftResult {

        public long timeTaken = 0;
        long moveCount = 0;

    }

    private static class Perft {

        static PerftResult perft(Position position, int depth) {

            PerftResult result = new PerftResult();
            if (depth == 0) {
                result.moveCount++;
                return result;
            }

            position.gen();
            List<Coups> pseudocoups = position.pseudomoves;
            for (Coups coups : pseudocoups) {
                if (position.makemove(coups)) {
                    PerftResult subPerft = perft(new Position(position), depth - 1);
                    position.takeback();
                    result.moveCount += subPerft.moveCount;
                }
            }
            return result;
        }

    }
}
