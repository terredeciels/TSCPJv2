package tscp;

import java.util.ArrayList;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

public class Position extends FPosition {

    public int roque;
    public int ep;
    public int halfMoveClock;
    public int plyNumber;
    private int fifty;
    private UndoMove um = new UndoMove();

    public Position() {
    }

    public Position(Position position) {
        couleur = position.couleur;
        piece = position.piece;
        au_trait = position.au_trait;
        non_au_trait = position.non_au_trait;
        roque = position.roque;
        ep = position.ep;
        fifty = position.fifty;
        pseudomoves = new ArrayList<>();
        um = new UndoMove();
    }

    public void gen() {
        /* autres coups que roques et ep */
        stream(INDEX_CASES64).forEach(this::sub_gen);
        /* generate castle moves */
        if (au_trait == BLANC) {
            if ((roque & 1) != 0) {
                gen_push(E1, G1, 2);
            }
            if ((roque & 2) != 0) {
                gen_push(E1, C1, 2);
            }
        } else {
            if ((roque & 4) != 0) {
                gen_push(E8, G8, 2);
            }
            if ((roque & 8) != 0) {
                gen_push(E8, C8, 2);
            }
        }
        /* generate en passant moves */
        if (ep != -1) {
            if (au_trait == BLANC) {
                if ((ep & 7) != 0 && couleur[ep + 7] == BLANC && piece[ep + 7] == PION) {
                    gen_push(ep + 7, ep, 21);
                }
                if ((ep & 7) != 7 && couleur[ep + 9] == BLANC && piece[ep + 9] == PION) {
                    gen_push(ep + 9, ep, 21);
                }
            } else {
                if ((ep & 7) != 0 && couleur[ep - 9] == NOIR && piece[ep - 9] == PION) {
                    gen_push(ep - 9, ep, 21);
                }
                if ((ep & 7) != 7 && couleur[ep - 7] == NOIR && piece[ep - 7] == PION) {
                    gen_push(ep - 7, ep, 21);
                }
            }
        }
    }

    private boolean en_echec(int side) {
        final boolean[] b = new boolean[1];
        stream(INDEX_CASES64).filter(c -> piece[c] == ROI && couleur[c] == side).forEach(c -> {
            b[0] = attaque(c, side ^ 1);
        });
        return b[0];
    }

    private boolean attaque(int c_roi_side, int side) {
        final boolean[] b = new boolean[1];
        stream(INDEX_CASES64).filter(c -> couleur[c] == side).forEach(c ->
        {
            if (piece[c] == PION) {
                if (side == BLANC) {
                    if (s_blanc(c_roi_side, c)) b[0] = true;
                } else if (s_noir(c_roi_side, c)) b[0] = true;
            } else if (non_pion(c_roi_side, c)) b[0] = true;
        });
        return b[0];
    }

    private boolean non_pion(int sq, int i) {
        final boolean[] b = new boolean[1];
        range(0, champ[piece[i]]).forEach(j -> {
                    int n = i;
                    while (true) {
                        n = fmailbox(i, j, n);
                        if (n == OUT) break;
                        if (n == sq) {
                            b[0] = true;
                            break;
                        }
                        if (couleur[n] != VIDE) break;
                        if (!slide[piece[i]]) break;
                    }
                }

        );
        return b[0];
    }

    public boolean makemove(Coups m) {
        if ((m.bits & 2) != 0) {
            int from;
            int to;

            if (en_echec(au_trait)) {
                return false;
            }
            switch (m.dest) {
                case 62:
                    if (couleur[F1] != VIDE || couleur[G1] != VIDE || attaque(F1, non_au_trait) || attaque(G1, non_au_trait)) {
                        return false;
                    }
                    from = H1;
                    to = F1;
                    break;
                case 58:
                    if (couleur[B1] != VIDE || couleur[C1] != VIDE || couleur[D1] != VIDE || attaque(C1, non_au_trait) || attaque(D1, non_au_trait)) {
                        return false;
                    }
                    from = A1;
                    to = D1;
                    break;
                case 6:
                    if (couleur[F8] != VIDE || couleur[G8] != VIDE || attaque(F8, non_au_trait) || attaque(G8, non_au_trait)) {
                        return false;
                    }
                    from = H8;
                    to = F8;
                    break;
                case 2:
                    if (couleur[B8] != VIDE || couleur[C8] != VIDE || couleur[D8] != VIDE || attaque(C8, non_au_trait) || attaque(D8, non_au_trait)) {
                        return false;
                    }
                    from = A8;
                    to = D8;
                    break;
                default: // shouldn't get here
                    from = -1;
                    to = -1;
                    break;
            }
            couleur[to] = couleur[from];
            piece[to] = piece[from];
            couleur[from] = VIDE;
            piece[from] = VIDE;
        }

        /* back up information, so we can take the move back later. */
        um.mov = m;
        um.capture = piece[m.dest];
        um.castle = roque;
        um.ep = ep;
        um.fifty = fifty;

        roque &= castle_mask[m.orig] & castle_mask[m.dest];

        if ((m.bits & 8) != 0) {
            if (au_trait == BLANC) {
                ep = m.dest + 8;
            } else {
                ep = m.dest - 8;
            }
        } else {
            ep = -1;
        }
        if ((m.bits & 17) != 0) {
            fifty = 0;
        } else {
            ++fifty;
        }

        /* move the piece */
        couleur[m.dest] = au_trait;
        if ((m.bits & 32) != 0) {
            piece[m.dest] = m.promote;
        } else {
            piece[m.dest] = piece[m.orig];
        }
        couleur[m.orig] = VIDE;
        piece[m.orig] = VIDE;

        /* erase the pawn if this is an en passant move */
        if ((m.bits & 4) != 0) {
            if (au_trait == BLANC) {
                couleur[m.dest + 8] = VIDE;
                piece[m.dest + 8] = VIDE;
            } else {
                couleur[m.dest - 8] = VIDE;
                piece[m.dest - 8] = VIDE;
            }
        }

        au_trait ^= 1;
        non_au_trait ^= 1;
        if (en_echec(non_au_trait)) {
            takeback();
            return false;
        }

        return true;
    }

    public void takeback() {

        au_trait ^= 1;
        non_au_trait ^= 1;

        Coups m = um.mov;
        roque = um.castle;
        ep = um.ep;
        fifty = um.fifty;

        couleur[m.orig] = au_trait;
        if ((m.bits & 32) != 0) {
            piece[m.orig] = PION;
        } else {
            piece[m.orig] = piece[m.dest];
        }
        if (um.capture == VIDE) {
            couleur[m.dest] = VIDE;
            piece[m.dest] = VIDE;
        } else {
            couleur[m.dest] = non_au_trait;
            piece[m.dest] = um.capture;
        }
        if ((m.bits & 2) != 0) {
            int from;
            int to;

            switch (m.dest) {
                case 62:
                    from = F1;
                    to = H1;
                    break;
                case 58:
                    from = D1;
                    to = A1;
                    break;
                case 6:
                    from = F8;
                    to = H8;
                    break;
                case 2:
                    from = D8;
                    to = A8;
                    break;
                default: // shouldn't get here
                    from = -1;
                    to = -1;
                    break;
            }
            couleur[to] = au_trait;
            piece[to] = TOUR;
            couleur[from] = VIDE;
            piece[from] = VIDE;
        }
        if ((m.bits & 4) != 0) {
            if (au_trait == BLANC) {
                couleur[m.dest + 8] = non_au_trait;
                piece[m.dest + 8] = PION;
            } else {
                couleur[m.dest - 8] = non_au_trait;
                piece[m.dest - 8] = PION;
            }
        }
    }

}