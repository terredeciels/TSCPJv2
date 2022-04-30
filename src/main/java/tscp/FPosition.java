package tscp;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

public class FPosition extends FFPosition {
    public int au_trait;
    public int non_au_trait;
    public List<Coups> pseudomoves = new ArrayList<>();

    void sub_gen(int _c) {

        if (test(couleur, _c, au_trait)) if (test(piece, _c, PION)) {
            switch (au_trait) {
                case BLANC:
                    fpion_blanc(_c);
                    break;
                case NOIR:
                    fpion_noir(_c);
            }
        } else range(0, champ[piece[_c]]).forEach(dir -> {
            int c0 = _c;
            c0 = fmailbox(_c, dir, c0);
            while (extracted(_c, c0)) c0 = fmailbox(_c, dir, c0);
        });
    }

    private boolean extracted(int c, int c0) {
        if (c0 == OUT) return false;
        if (test(couleur, c0, VIDE)) {
            gen_push(c, c0, 0);
            return slide[piece[c]];
        }
        if (test(couleur, c0, non_au_trait)) {
            gen_push(c, c0, 1);
            return false;
        }
        return false;
    }

    void gen_push(int from, int to, int bits) {
        if ((bits & 16) != 0)
            if (au_trait == BLANC) {
                if (to <= H8) {
                    gen_promote(from, to, bits);
                    return;
                }
            } else if (to >= A1) {
                gen_promote(from, to, bits);
                return;
            }
        pseudomoves.add(new Coups((byte) from, (byte) to, (byte) 0, (byte) bits));

    }

    void gen_promote(int from, int to, int bits) {
        rangeClosed(CAVALIER, DAME).forEach(i -> {
            pseudomoves.add(new Coups((byte) from, (byte) to, (byte) i, (byte) (bits | 32)));
        });
    }

    void fpion_noir(int _c) {
        if (isGenPush(_c)) gen_push(_c, _c + 7, 17);
        if (isGenPush2(_c)) gen_push(_c, _c + 9, 17);
        if (test(couleur, _c + 8, VIDE)) {
            gen_push(_c, _c + 8, 16);
            if (isC_Roi_Vide_N(_c)) gen_push(_c, _c + 16, 24);
        }
    }

    void fpion_blanc(int _c) {
        if (isGenPush_N(_c)) gen_push(_c, _c - 9, 17);
        if (isGenPush2_N(_c)) gen_push(_c, _c - 7, 17);
        if (test(couleur, _c - 8, VIDE)) {
            gen_push(_c, _c - 8, 16);
            if (isC_Roi_Vide_B(_c)) gen_push(_c, _c - 16, 24);
        }
    }

    boolean s_noir(int c_roi, int c) {
        if (isC_Roi_N(c_roi, c)) return true;
        return isC_Roi2_N(c_roi, c);
    }

    boolean s_blanc(int c_roi, int c) {
        if (isC_Roi_B(c_roi, c)) return true;
        return isC_Roi2_B(c_roi, c);
    }

    void print_board() {
        int i;

        System.out.print("\n8 ");
        for (i = 0; i < 64; ++i) {
            switch (couleur[i]) {
                case VIDE:
                    System.out.print(". ");
                    break;
                case BLANC:
                    System.out.printf(piece_char_light[piece[i]] + " ");
                    break;
                case NOIR:
                    System.out.printf(piece_char_dark[piece[i]] + " ");
                    break;
            }
            if ((i + 1) % 8 == 0 && i != 63) {
                System.out.printf("\n%d ", 7 - (i >> 3));
            }
        }
        System.out.print("\n\n   a b c d e f g h\n\n");
    }

}

