// NhacNhacAction.java
import aima.core.util.datastructure.XYLocation;

import java.util.Objects;

public class NhacNhacAction {
    private final XYLocation from; // null se for uma peça nova (da mão)
    private final XYLocation to;
    private final Piece piece; // A peça sendo jogada/movida

    // Construtor para colocar uma nova peça
    public NhacNhacAction(Piece piece, XYLocation to) {
        this.from = null;
        this.piece = piece;
        this.to = to;
    }

    // Construtor para mover uma peça do tabuleiro
    public NhacNhacAction(XYLocation from, XYLocation to) {
        this.from = from;
        this.to = to;
        this.piece = null; // A peça será determinada pelo estado do tabuleiro
    }

    public XYLocation getFrom() {
        return from;
    }

    public XYLocation getTo() {
        return to;
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean isMoveAction() {
        return from != null;
    }

    @Override
    public String toString() {
        if (isMoveAction()) {
            return "Move from " + from + " to " + to;
        } else {
            return "Place " + piece + " at " + to;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhacNhacAction that = (NhacNhacAction) o;
        // Compara as ações independentemente de como foram criadas (place vs move)
        // Se a ação de "mover" for criada, a peça será nula, mas a lógica de validação
        // irá compará-la com uma ação de "mover" gerada pelo getActions, que também terá peça nula.
        return Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(piece, that.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, piece);
    }
}