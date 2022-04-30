package perft;

import perft.PerftCompare.PerftResult;

import tscp.Position;
import tscp.Coups;

import java.io.IOException;
import java.util.List;
import tools.FenToBoard;

public class PerftSpeed {
    //new brancheA1
    public static void main(String[] args) throws IOException {
        perftTest();
    }

    private static void perftTest() {
        //voir http://chessprogramming.wikispaces.com/Perft+Results     
        String f = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Position position = FenToBoard.toBoard(f);
        int max_depth = 6;
        double t0 = System.nanoTime();
        for (int depth = 1; depth <= max_depth; depth++) {
            PerftResult res = perft(new Position(position), depth);
            double t1 = System.nanoTime();
            System.out.println("Depth " + depth + " : " + (t1 - t0) / 1000000000 + " sec");
            System.out.println("Count = " + res.moveCount);
        }

    }

    private static PerftResult perft(Position position, int depth) {

        PerftResult result = new PerftResult();
        if (depth == 0) {
            result.moveCount++;
            return result;
        }
        position.gen();
        List<Coups> moves = position.pseudomoves;
        for (Coups move : moves) {
            if (position.makemove(move)) {
                PerftResult subPerft = perft(new Position(position), depth - 1);
                position.takeback();
                result.moveCount += subPerft.moveCount;
            }
        }
        return result;
    }
}
