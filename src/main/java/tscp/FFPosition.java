package tscp;

public class FFPosition implements Constantes {
    public int[] couleur = new int[64];
    public int[] piece = new int[64];

    boolean test(int[] tab, int c, int val) {
        return tab[c] == val;
    }


    int fmailbox(int _c, int dir, int c0) {
        return mailbox[CASES64[c0] + delta[piece[_c]][dir]];
    }

    boolean isGenPush2(int _c) {
        return (_c & 7) != 7 && couleur[_c + 9] == BLANC;
    }

    boolean isGenPush(int _c) {
        return (_c & 7) != 0 && couleur[_c + 7] == BLANC;
    }

    boolean isGenPush2_N(int _c) {
        return (_c & 7) != 7 && couleur[_c - 7] == NOIR;
    }

    boolean isGenPush_N(int _c) {
        return (_c & 7) != 0 && couleur[_c - 9] == NOIR;
    }

    boolean isC_Roi2_N(int c_roi, int c) {
        return (c & 7) != 7 && c + 9 == c_roi;
    }

    boolean isC_Roi_N(int c_roi, int c) {
        return (c & 7) != 0 && c + 7 == c_roi;
    }

    boolean isC_Roi2_B(int c_roi, int c) {
        return (c & 7) != 7 && c - 7 == c_roi;
    }

    boolean isC_Roi_B(int c_roi, int c) {
        return (c & 7) != 0 && c - 9 == c_roi;
    }

    boolean isC_Roi_Vide_B(int _c) {
        return _c >= 48 && couleur[_c - 16] == VIDE;
    }

    boolean isC_Roi_Vide_N(int _c) {
        return _c <= 15 && couleur[_c + 16] == VIDE;
    }
}
