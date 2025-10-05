import aima.core.util.datastructure.XYLocation;

import java.util.*;

public class NhacNhacState implements Cloneable {
    public static final String O = "O";
    public static final String X = "X";

    private Stack<Piece>[] board;
    private Map<String, List<Piece>> playerPieces;

    private String playerToMove;
    private double utility = -1; // 1: Vitoria de X, 0: Vitoria de O, 0.5: Empate

    @SuppressWarnings("unchecked")
    public NhacNhacState() {
        board = new Stack[9];
        for (int i = 0; i < 9; i++) {
            board[i] = new Stack<>();
        }

        playerPieces = new HashMap<>();
        List<Piece> xPieces = new ArrayList<>();
        List<Piece> oPieces = new ArrayList<>();
        for (Piece.Size size : Piece.Size.values()) {
            xPieces.add(new Piece(size, X));
            xPieces.add(new Piece(size, X));
            oPieces.add(new Piece(size, O));
            oPieces.add(new Piece(size, O));
        }
        playerPieces.put(X, xPieces);
        playerPieces.put(O, oPieces);

        playerToMove = X;
    }

    public String getPlayerToMove() {
        return playerToMove;
    }

    public Piece getVisiblePiece(int col, int row) {
        Stack<Piece> stack = board[getAbsPosition(col, row)];
        return stack.isEmpty() ? null : stack.peek();
    }

    public double getUtility() {
        return utility;
    }

    public List<NhacNhacAction> getActions() {
        List<NhacNhacAction> actions = new ArrayList<>();
        if (utility != -1) return actions; // Jogo terminado

        // 1. Ações de colocar nova peça
        for (Piece piece : playerPieces.get(playerToMove)) {
            for (int i = 0; i < 9; i++) {
                Piece topPiece = board[i].isEmpty() ? null : board[i].peek();
                if (topPiece == null || piece.getSize().isLargerThan(topPiece.getSize())) {
                    actions.add(new NhacNhacAction(piece, new XYLocation(i % 3, i / 3)));
                }
            }
        }

        // 2. Ações de mover peça do tabuleiro
        for (int i = 0; i < 9; i++) {
            Piece topPiece = board[i].isEmpty() ? null : board[i].peek();
            if (topPiece != null && topPiece.getPlayer().equals(playerToMove)) {
                for (int j = 0; j < 9; j++) {
                    if (i == j) continue;
                    Piece destTopPiece = board[j].isEmpty() ? null : board[j].peek();
                    if (destTopPiece == null || topPiece.getSize().isLargerThan(destTopPiece.getSize())) {
                        actions.add(new NhacNhacAction(new XYLocation(i % 3, i / 3), new XYLocation(j % 3, j / 3)));
                    }
                }
            }
        }
        return actions;
    }

    public void apply(NhacNhacAction action) {
        if (action.isMoveAction()) {
            // Mover peça existente
            int fromPos = getAbsPosition(action.getFrom().getX(), action.getFrom().getY());
            Piece pieceToMove = board[fromPos].pop();
            int toPos = getAbsPosition(action.getTo().getX(), action.getTo().getY());
            board[toPos].push(pieceToMove);
        } else {
            // Colocar nova peça
            Piece pieceToPlace = action.getPiece();
            playerPieces.get(playerToMove).remove(pieceToPlace);
            int toPos = getAbsPosition(action.getTo().getX(), action.getTo().getY());
            board[toPos].push(pieceToPlace);
        }
        analyzeUtility();
        playerToMove = (Objects.equals(playerToMove, X) ? O : X);
    }

    private void analyzeUtility() {
        if (lineThroughBoard()) {
            // O jogador que ACABOU de mover vence.
            utility = (Objects.equals(playerToMove, X) ? 1 : 0);
        } else if (isBoardFull()) { // Condição de empate pode ser mais complexa
            utility = 0.5;
        }
    }

    private boolean isBoardFull() {
        // Um critério de empate simples: não há mais jogadas legais para o próximo jogador.
        // Uma análise mais profunda poderia detectar ciclos, etc.
        return getActions().isEmpty();
    }

    private boolean lineThroughBoard() {
        return (isAnyRowComplete() || isAnyColumnComplete() || isAnyDiagonalComplete());
    }

    private String getVisiblePlayer(int col, int row) {
        Piece p = getVisiblePiece(col, row);
        return (p != null) ? p.getPlayer() : null;
    }

    private boolean isAnyRowComplete() {
        for (int row = 0; row < 3; row++) {
            String p = getVisiblePlayer(0, row);
            if (p != null && p.equals(getVisiblePlayer(1, row)) && p.equals(getVisiblePlayer(2, row))) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnyColumnComplete() {
        for (int col = 0; col < 3; col++) {
            String p = getVisiblePlayer(col, 0);
            if (p != null && p.equals(getVisiblePlayer(col, 1)) && p.equals(getVisiblePlayer(col, 2))) {
                return true;
            }
        }
        return false;
    }

    private boolean isAnyDiagonalComplete() {
        String p = getVisiblePlayer(0, 0);
        if (p != null && p.equals(getVisiblePlayer(1, 1)) && p.equals(getVisiblePlayer(2, 2))) {
            return true;
        }
        p = getVisiblePlayer(0, 2);
        if (p != null && p.equals(getVisiblePlayer(1, 1)) && p.equals(getVisiblePlayer(2, 0))) {
            return true;
        }
        return false;
    }

    private int getAbsPosition(int col, int row) {
        return row * 3 + col;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NhacNhacState clone() {
        NhacNhacState copy;
        try {
            copy = (NhacNhacState) super.clone();
            copy.board = new Stack[9];
            for(int i=0; i<9; i++) {
                copy.board[i] = (Stack<Piece>) this.board[i].clone();
            }
            copy.playerPieces = new HashMap<>();
            copy.playerPieces.put(X, new ArrayList<>(this.playerPieces.get(X)));
            copy.playerPieces.put(O, new ArrayList<>(this.playerPieces.get(O)));
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Este objeto não é clonável.", e);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Piece p = getVisiblePiece(col, row);
                builder.append(p == null ? " -- " : " " + p + " ");
            }
            builder.append("\n");
        }
        builder.append("Player to move: ").append(playerToMove).append("\n");
        builder.append("X pieces left: ").append(playerPieces.get(X)).append("\n");
        builder.append("O pieces left: ").append(playerPieces.get(O)).append("\n");
        return builder.toString();
    }
}