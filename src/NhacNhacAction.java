// NhacNhacAction.java
import aima.core.util.datastructure.XYLocation;

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
}