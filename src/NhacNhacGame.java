// NhacNhacGame.java
import aima.core.search.adversarial.Game;
import java.util.List;
import java.util.Objects;

public class NhacNhacGame implements Game<NhacNhacState, NhacNhacAction, String> {

    private NhacNhacState initialState = new NhacNhacState();

    @Override
    public NhacNhacState getInitialState() {
        return initialState;
    }

    @Override
    public String[] getPlayers() {
        return new String[]{NhacNhacState.X, NhacNhacState.O};
    }

    @Override
    public String getPlayer(NhacNhacState state) {
        return state.getPlayerToMove();
    }

    @Override
    public List<NhacNhacAction> getActions(NhacNhacState state) {
        return state.getActions();
    }

    @Override
    public NhacNhacState getResult(NhacNhacState state, NhacNhacAction action) {
        NhacNhacState result = state.clone();
        result.apply(action);
        return result;
    }

    @Override
    public boolean isTerminal(NhacNhacState state) {
        return state.getUtility() != -1;
    }

    @Override
    public double getUtility(NhacNhacState state, String player) {
        final double WIN = 10000.0;
        final double LOSS = -10000.0;
        final double DRAW = 0.0;

        double result = state.getUtility(); // Isso retorna 1.0 para X, 0.0 para O, 0.5 para empate

        if (result != -1) {
            if (player.equals(NhacNhacState.X)) {
                if (result == 1.0) return WIN;
                if (result == 0.0) return LOSS;
            } else { // player is O
                if (result == 0.0) return WIN;
                if (result == 1.0) return LOSS;
            }
            return DRAW;
        } else {
            throw new IllegalArgumentException("O estado não é terminal.");
        }
    }

    public double getHeuristicValue(NhacNhacState state, String player) {
        double score = 0;
        String opponent = player.equals(NhacNhacState.X) ? NhacNhacState.O : NhacNhacState.X;

        // Todas as 8 linhas possíveis de vitória
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Linhas
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Colunas
                {0, 4, 8}, {2, 4, 6}  // Diagonais
        };

        for (int[] line : lines) {
            int playerCount = 0;
            int opponentCount = 0;
            double pieceSizeBonus = 0; // Bônus por peças maiores na linha

            for (int pos : line) {
                Piece p = state.getVisiblePiece(pos % 3, pos / 3);
                if (p != null) {
                    if (p.getPlayer().equals(player)) {
                        playerCount++;
                        if (p.getSize() == Piece.Size.LARGE) pieceSizeBonus += 0.3;
                        if (p.getSize() == Piece.Size.MEDIUM) pieceSizeBonus += 0.2;
                    } else if (p.getPlayer().equals(opponent)) {
                        opponentCount++;
                    }
                }
            }
            score += evaluateLine(playerCount, opponentCount);
        }
        return score;
    }

    private double evaluateLine(int playerCount, int opponentCount) {
//linha nao usada por ninguem nao tem valor
        if (playerCount > 0 && opponentCount > 0) {
            return 0;
        }

        if (playerCount == 3) return 100; // Vitória
        if (opponentCount == 3) return -100; // Derrota

        // Oportunidades de vitória para a IA
        if (playerCount == 2) return 100;  // Duas peças em linha é uma grande ameaça.
        if (playerCount == 1) return 10;   // Uma peça em uma linha vazia.

        // Ameaças do oponente que precisam ser bloqueadas
        if (opponentCount == 2) return -500; // Prioridade MÁXIMA para bloquear o oponente
        if (opponentCount == 1) return -1;   // Uma pequena ameaça.
        return 0; // Linha neutra ou bloqueada
    }
}