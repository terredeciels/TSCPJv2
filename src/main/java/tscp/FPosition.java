package tscp;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.IntStream.range;

public class FPosition implements Constantes {
    public int au_trait;
    public int non_au_trait;
    public int[] couleur = new int[64];
    public int[] piece = new int[64];
    public List<Coups> pseudomoves = new ArrayList<>();

    void sub_gen(int _c) {
        if (couleur[_c] == au_trait) if (piece[_c] == PION) {
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

    int fmailbox(int _c, int dir, int c0) {
        return mailbox[CASES64[c0] + delta[piece[_c]][dir]];
    }

    private boolean extracted(int c, int c0) {

        if (c0 == OUT) return false;
        if (couleur[c0] == VIDE) {
            gen_push(c, c0, 0);
            return slide[piece[c]];
        }
        if (couleur[c0] == non_au_trait) {
            gen_push(c, c0, 1);
            return false;
        }
        return false;
    }

    void gen_push(int from, int to, int bits) {
        if ((bits & 16) != 0) {
            if (au_trait == BLANC) {
                if (to <= H8) {
                    gen_promote(from, to, bits);
                    return;
                }
            } else if (to >= A1) {
                gen_promote(from, to, bits);
                return;
            }
        }
        pseudomoves.add(new Coups((byte) from, (byte) to, (byte) 0, (byte) bits));

    }

    void gen_promote(int from, int to, int bits) {
        for (int i = CAVALIER; i <= DAME; ++i) {
            pseudomoves.add(new Coups((byte) from, (byte) to, (byte) i, (byte) (bits | 32)));
        }
    }

    void fpion_noir(int _c) {
        if ((_c & 7) != 0 && couleur[_c + 7] == BLANC) gen_push(_c, _c + 7, 17);
        if ((_c & 7) != 7 && couleur[_c + 9] == BLANC) gen_push(_c, _c + 9, 17);
        if (couleur[_c + 8] == VIDE) {
            gen_push(_c, _c + 8, 16);
            if (_c <= 15 && couleur[_c + 16] == VIDE) gen_push(_c, _c + 16, 24);
        }
    }

    void fpion_blanc(int _c) {
        if ((_c & 7) != 0 && couleur[_c - 9] == NOIR) gen_push(_c, _c - 9, 17);
        if ((_c & 7) != 7 && couleur[_c - 7] == NOIR) gen_push(_c, _c - 7, 17);
        if (couleur[_c - 8] == VIDE) {
            gen_push(_c, _c - 8, 16);
            if (_c >= 48 && couleur[_c - 16] == VIDE) gen_push(_c, _c - 16, 24);
        }
    }

    boolean s_noir(int c_roi, int c) {
        if ((c & 7) != 0 && c + 7 == c_roi) return true;
        return (c & 7) != 7 && c + 9 == c_roi;
    }

    boolean s_blanc(int c_roi, int c) {
        if ((c & 7) != 0 && c - 9 == c_roi) return true;
        return (c & 7) != 7 && c - 7 == c_roi;
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

