import java.util.Objects;


// representação da peça, com os diferentes tamanhos
public class Piece {

    public enum Size {
        SMALL(1), MEDIUM(2), LARGE(3);

        private final int value;

        Size(int value) {
            this.value = value;
        }

        public boolean isLargerThan(Size other) {
            return this.value > other.value;
        }
    }

    private final Size size;
    private final String player;

    public Piece(Size size, String player) {
        this.size = size;
        this.player = player;
    }

    public Size getSize() {
        return size;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return player + size.name().charAt(0); // Ex: XS, OM, XL
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return size == piece.size && Objects.equals(player, piece.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, player);
    }
}