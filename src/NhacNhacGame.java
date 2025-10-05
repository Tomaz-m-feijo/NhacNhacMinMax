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
        double result = state.getUtility();
        if (result != -1) {
            // Se o resultado é 1 (vitória de X) e o jogador é O, a utilidade para O é 0.
            // Se o resultado é 0 (vitória de O) e o jogador é O, a utilidade para O é 1.
            if (Objects.equals(player, NhacNhacState.O)) {
                result = 1 - result;
            }
        } else {
            throw new IllegalArgumentException("State is not terminal.");
        }
        return result;
    }
}