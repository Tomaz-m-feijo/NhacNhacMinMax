
import  aima.core.search.adversarial.Game;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): page 169.<br>
 * <p>
 * <pre>
 * <code>
 * function MINIMAX-DECISION(state) returns an action
 *   return argmax_[a in ACTIONS(s)] MIN-VALUE(RESULT(state, a))
 *
 * function MAX-VALUE(state) returns a utility value
 *   if TERMINAL-TEST(state) then return UTILITY(state)
 *   v = -infinity
 *   for each a in ACTIONS(state) do
 *     v = MAX(v, MIN-VALUE(RESULT(s, a)))
 *   return v
 *
 * function MIN-VALUE(state) returns a utility value
 *   if TERMINAL-TEST(state) then return UTILITY(state)
 *     v = infinity
 *     for each a in ACTIONS(state) do
 *       v  = MIN(v, MAX-VALUE(RESULT(s, a)))
 *   return v
 * </code>
 * </pre>
 * <p>
 * Figure 5.3 An algorithm for calculating minimax decisions. It returns the
 * action corresponding to the best possible move, that is, the move that leads
 * to the outcome with the best utility, under the assumption that the opponent
 * plays to minimize utility. The functions MAX-VALUE and MIN-VALUE go through
 * the whole game tree, all the way to the leaves, to determine the backed-up
 * value of a state. The notation argmax_[a in S] f(a) computes the element a of
 * set S that has the maximum value of f(a).
 *
 * @param <S> Type which is used for states in the game.
 * @param <A> Type which is used for actions in the game.
 * @param <P> Type which is used for players in the game.
 * @author Ruediger Lunde
 */
public class MinimaxSearch<S, A, P> implements AdversarialSearch<S, A> {

	public final static String METRICS_NODES_EXPANDED = "nodesExpanded";

	private final Game<S, A, P> game;
	private final int maxDepth;
	private final HeuristicEvaluationFunction<S, P> evalFn;
	private Metrics metrics = new Metrics();


	// Interface para nossa função de avaliação
	public interface HeuristicEvaluationFunction<S, P> {
		double evaluate(S state, P player);
	}

	public MinimaxSearch(Game<S, A, P> game, HeuristicEvaluationFunction<S, P> evalFn, int maxDepth) {
		if (maxDepth <= 0) {
			throw new IllegalArgumentException("A profundidade máxima da busca (maxDepth) deve ser maior que 0.");
		}
		this.game = game;
		this.evalFn = evalFn;
		this.maxDepth = maxDepth;
	}

	/**
	 * Creates a new search object for a given game.
	 */
	public static <S, A, P> MinimaxSearch<S, A, P> createFor(Game<S, A, P> game, HeuristicEvaluationFunction<S, P> evalFn, int maxDepth) {
		return new MinimaxSearch<>(game, evalFn, maxDepth);
	}


	//função de avaliação de estado
	private double eval(S state, P player) {
		if (game.isTerminal(state)) {
			return game.getUtility(state, player);
		} else {
			// Se não for terminal, usamos nossa heurística
			return evalFn.evaluate(state, player);
		}
	}
	//modificada a isTerminal para verificar profundidade
	private boolean isTerminal(S state, int depth) {
		// Se maxDepth < 0, a busca é ilimitada.
		return maxDepth >= 0 && depth >= maxDepth || game.isTerminal(state);
	}





	@Override
	public A makeDecision(S state) {
		metrics = new Metrics();
		A result = null;
		double resultValue = Double.NEGATIVE_INFINITY;
		P player = game.getPlayer(state);
		for (A action : game.getActions(state)) {
			double value = minValue(game.getResult(state, action), player, 0);
			if (value > resultValue) {
				result = action;
				resultValue = value;
			}
		}
		return result;
	}

//	  Note: This version looks cleaner but expands almost twice as much nodes (Comparator...)
//    @Override
//    public A makeDecision(S state) {
//        metrics = new Metrics();
//        P player = game.getPlayer(state);
//        return game.getActions(state).stream()
//                .max(Comparator.comparing(action -> minValue(game.getResult(state, action), player)))
//                .orElse(null);
//    }

	public double maxValue(S state, P player,int depth) { // returns an utility value
		metrics.incrementInt(METRICS_NODES_EXPANDED);
		if (isTerminal(state, depth)) { // jogo acabou ou chegou na profundidade limite
			return eval(state, player); //modificado para usar eval heuristico
		}
		return game.getActions(state).stream()
				.mapToDouble(action -> minValue(game.getResult(state, action), player, depth+1))
				.max().orElse(Double.NEGATIVE_INFINITY);
	}

	public double minValue(S state, P player, int depth) { // returns an utility value
		metrics.incrementInt(METRICS_NODES_EXPANDED);
		if (isTerminal(state, depth)) { // jogo acabou ou chegou na profundidade limite
			return eval(state, player); //modificado para usar eval heuristico
		}
		return game.getActions(state).stream()
				.mapToDouble(action -> maxValue(game.getResult(state, action), player, depth+1))
				.min().orElse(Double.POSITIVE_INFINITY);
	}

	@Override
	public Metrics getMetrics() {
		return metrics;
	}

}