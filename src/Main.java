import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        NhacNhacGame game = new NhacNhacGame();
        NhacNhacState state = game.getInitialState();


        String humanPlayer = NhacNhacState.X;
        String aiPlayer = NhacNhacState.O;
        System.out.println("Bem-vindo ao Nhac Nhac!");


        //cria a instancia com a heuristica
        int searchDepth = 3;
        MinimaxSearch<NhacNhacState, NhacNhacAction, String> minimax = MinimaxSearch.createFor(
                game,
                (s, p) -> game.getHeuristicValue(s, p), // Usando uma expressão lambda para a nossa heurística
                searchDepth
        );

        Scanner scanner = new Scanner(System.in);

        while (!game.isTerminal(state)) {
            System.out.println("--------------------");
            System.out.println(state);

            //NhacNhacAction action = minimax.makeDecision(state);
            //System.out.println("Minimax escolheu a ação: " + action);
            String currentPlayer = game.getPlayer(state);

            NhacNhacAction action;

            if (currentPlayer.equals(aiPlayer)) {
                // VEZ DA IA
                System.out.println("IA (" + aiPlayer + ") está pensando...");
                long startTime = System.currentTimeMillis();
                action = minimax.makeDecision(state);
                long endTime = System.currentTimeMillis();
                System.out.println("Pensou por " + (endTime - startTime) + " ms.");
                System.out.println("IA escolheu a ação: " + action);

            } else {
                // VEZ DO HUMANO
                System.out.println("É a sua vez (" + humanPlayer + ").");
                List<NhacNhacAction> validActions = game.getActions(state);
                action = getHumanAction(scanner, state, validActions, humanPlayer);
            }
            state = game.getResult(state, action);
        }

        System.out.println("------- FIM DE JOGO -------");
        System.out.println(state);

        double utility = state.getUtility();
        if (utility == 1.0) {
            System.out.println("Jogador X venceu!");
        } else if (utility == 0.0) {
            System.out.println("Jogador O venceu!");
        } else {
            System.out.println("O jogo terminou em empate!");
        }
    }
    private static NhacNhacAction getHumanAction(Scanner scanner, NhacNhacState state, List<NhacNhacAction> validActions, String humanPlayer) {
        while (true) {
            System.out.println("Digite sua jogada (ex: 'place S 1 1' ou 'move 0 0 2 1'):");
            String line = scanner.nextLine().toLowerCase().trim();
            String[] parts = line.split(" ");

            try {
                NhacNhacAction humanAction = null;
                if (parts.length > 0 && parts[0].equals("place")) {
                    // Formato: place <S|M|L> <col> <row>
                    if (parts.length != 4) throw new Exception("Formato inválido para 'place'.");
                    Piece.Size size = parseSize(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    int row = Integer.parseInt(parts[3]);
                    humanAction = new NhacNhacAction(new Piece(size, humanPlayer), new aima.core.util.datastructure.XYLocation(col, row));

                } else if (parts.length > 0 && parts[0].equals("move")) {
                    // Formato: move <fromCol> <fromRow> <toCol> <toRow>
                    if (parts.length != 5) throw new Exception("Formato inválido para 'move'.");
                    int fromCol = Integer.parseInt(parts[1]);
                    int fromRow = Integer.parseInt(parts[2]);
                    int toCol = Integer.parseInt(parts[3]);
                    int toRow = Integer.parseInt(parts[4]);
                    humanAction = new NhacNhacAction(new aima.core.util.datastructure.XYLocation(fromCol, fromRow), new aima.core.util.datastructure.XYLocation(toCol, toRow));
                } else {
                    System.out.println("Comando inválido. Use 'place' ou 'move'.");
                    continue;
                }

                // VALIDAÇÃO: Verifica se a ação criada pelo humano é uma das ações válidas
                if (validActions.contains(humanAction)) {
                    return humanAction;
                } else {
                    System.out.println("--- JOGADA ILEGAL! --- Tente novamente.");
                }

            } catch (Exception e) {
                System.out.println("Erro ao processar jogada: " + e.getMessage() + ". Tente novamente.");
            }
        }
    }

    private static Piece.Size parseSize(String s) throws Exception {
        return switch (s.toUpperCase()) {
            case "S" -> Piece.Size.SMALL;
            case "M" -> Piece.Size.MEDIUM;
            case "L" -> Piece.Size.LARGE;
            default -> throw new Exception("Tamanho de peça inválido. Use S, M ou L.");
        };
    }

}