package tscp;

/**
 * 1	capture 2	castle 4	en passant capture 8	pushing a pawn 2 squares 16	pawn
 * move 32	promote
 */
public class Coups {

    byte orig;
    byte dest;
    byte promote;
    byte bits;

    Coups() {
    }

    Coups(byte from, byte to, byte promote, byte bits) {
        this.orig = from;
        this.dest = to;
        this.promote = promote;
        this.bits = bits;
    }

}
