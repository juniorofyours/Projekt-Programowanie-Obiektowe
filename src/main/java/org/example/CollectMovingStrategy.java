package org.example;

/**
 * Strategia poruszania się wyszkolonego człowieka realizująca ruch docelowy w stronę kontenera na planszy.
 * <p>
 *      Klasa implementuje interfejs {@link MovingStrategy}.
 *      W każdym kroku generuje wektor ruchu [x, y] (gdzie x, y przejmują wartości całkowite od -1 do 1) w zależności od
 *      najbliższej drogi do kontenera. Wyznacza go dzięki metodzie {@code vectorTorus()}, uwzględniając możliwość
 *      przejścia przez krawędź planszy, która w metodzie {@code move()} za pomocą metody {@code signum()}
 *      zamienia się w wektor skłądający się z wartości całkowitych w przedziale od -1 do 1..
 *      Wyszkolony człowiek nie może wejść na komórkę kontenera i jest hamowany przed najbliższą.
 * </p>
 */
public class CollectMovingStrategy implements MovingStrategy{
    private final Board board;
    public CollectMovingStrategy(Board board){
        this.board=board;
    }

    /**
     * Wyznacza wektor torusowy dla ruchu wyszkolonej osoby w stronę kontenera dla zadanej osi OX lub OY.
     * @param coord                 Współrzędna, na której znajduje się wyszkolony człowiek.
     * @param goal                  Współrzędna komórki kontenera, do któej porusza się agent.
     * @param sizeOfBoard           Rozmiar planszy dla danej osi.
     * @return delta                Droga do pokonania na danej osi.
     */
    public static int vectorTorus (int coord, int goal, int sizeOfBoard) {
        //delta - odpowiednik standardowej drogi
        int delta = goal - coord;

        //sprawdzamy, czy zwykla droga jest wieksza niz polowa rozmiaru planszy
        if(Math.abs(delta) > sizeOfBoard /2.0) { //jezeli tak, to trzeba zawinąć
            if(delta > 0) { // skrót przez lewa / gorna (w zaleznosci od osi) krawedz
                return delta - sizeOfBoard;
            }
            else { // skrót przez prawa / dolna (w zaleznosci od osi) krawedz
                return delta + sizeOfBoard;
            }
        }
        return delta; //droga standardowa
    }

    /**
     * Rusza agentem w stronę wyznaczoną za pomocą wyznaczonych wektorów {@code vectorTorus} dla osi OX i OY.
     * @param position                  Pozycja agenta w chwili wywołania metody.
     * @param simulation                Instancja symulacji.
     */
    @Override
    public void move(AgentPosition position, Simulation simulation){
        int[] targetCoords = simulation.getCoordinatesOfContainer(); //0 - minX, 1 - maxX, 2 - minY, 3 - maxY
        int x = position.getX(), y = position.getY();
        Cell targetCell=board.getClosestCellContainingGarlicContainer(x, y, targetCoords[0],  targetCoords[1],  targetCoords[2],  targetCoords[3]);

        int width = board.getWidth();
        int height = board.getHeight();
        int dx = vectorTorus(x, targetCell.getX(), width);
        int dy = vectorTorus(y, targetCell.getY(), height);

        if(Math.abs(dx)<=1) {
            dx = 0;
        }
        if(Math.abs(dy)<=1) {
            dy = 0;
        }
        position.move(Integer.signum(dx), Integer.signum(dy));
    }
}
