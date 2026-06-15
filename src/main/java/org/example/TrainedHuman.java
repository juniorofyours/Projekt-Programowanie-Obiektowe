package org.example;

import java.util.List;
//importowanie metody dla ruchu po torusie:
import static org.example.CollectMovingStrategy.vectorTorus;

/**
 * Klasa reprezentująca agenta typu Wyszkolony Człowiek w symulacji.
 * <p>
 * Wyszkolony człowiek pojawia się na nplanszy ({@link Board}) na dwa różne sposoby:
 * 1. przy tworzeniu symulacji ({@link Simulation}) bądź
 * 2. przy wyszkoleniu zwykłego człowieka w wytrenowanego.
 * W identyczny sposób do zwykłych ludzi jedzą czosnek i zostają atakowani przez wampirów, lecz wyróżniają się swoimi
 * dodatkowymi umiejętnościami: rozrzucaniem czosnku (które powoduje włączenie się trybu weakening ({@code weakening})
 * przez kolejne dwa kroki ({@code STEPS_FOR_GAIN}, kiedy nie mogą jeść i rozrzucać czosnkiem), szkoleniem zwykłych
 * osób oraz pobieraniem czosnku z kontenera, kiedy im się skończy w zapasie (zmieniają strategie ruchu w zależności od
 * potrzeb między strategiami {@link RandomMovingStrategy} a {@link CollectMovingStrategy}).
 * </p>
 */
public class TrainedHuman extends Human{
    /*zmienne instancyjne*/
    private float rectruitmentProb;
    private float throwProb;
    private int garlicStock;
    private int garlicStockMax;
    private int lastWeakeningStep;
    private boolean weakend;
    private MovingStrategy collectMoving;
    private MovingStrategy randomMoving;

    /*stałe*/
    final int MAX_NUMB_OF_TRAINED_HUMANBEINGS = 100;
    final int STEPS_FOR_GAIN = 2; // stała decydująca o tym, jak długo trwa tryb weakening
    final float RANGE_OF_PROBABILITY_OF_THROW = 50.0f;

    /**
     * Konstruuje nową instancję człowieka z określonymi parametrami.
     * @param simulation            Instancja symulacji.
     * @param board                 Plansza dwuwymiarowa, na której osadzony jest agent.
     * @param x                     Początkowa współrzędna x.
     * @param y                     Początkowa współrzędna y.
     * @param transformationProb    Prawdopodobieństwo zamiany w wampira.
     * @param addProb               Prawdopodobieństwo urodzenia nowego człowieka.
     * @param rectruitmentProb      Prawdopodobieństwo wyszkolenia zwykłego człowieka.
     * @param throwProb             Prawdopodobieństwo rzucenia czosnku.
     * @param garlicStockMax        Maksymalna liczba czosnku w zasobach wyszkolonego człowieka.
     * @param energyBoost           Ilość energii zyskiwana po ataku na człowieka.
     * @param energyLoss            Ilość energii tracona przy ataku przez czosnek.
     */
    public TrainedHuman(Simulation simulation, Board board, int x, int y, float transformationProb, float addProb, float rectruitmentProb, float throwProb,int garlicStockMax, int energyBoost, int energyLoss) {
        super(simulation, board, x, y, transformationProb, addProb, energyBoost, energyLoss); //super() wywołuje konstruktor
                                         // klasy nadrzednej, wiec zmienne np energyMax lub np stepsToReset sa takie same (1000 i 0)
        this.rectruitmentProb=rectruitmentProb;
        this.throwProb=throwProb;

        //wartosci poczatkowe
        this.lastWeakeningStep=0;
        this.weakend=false;
        this.garlicStockMax=garlicStockMax; //np.
        this.garlicStock=garlicStockMax;
        this.trained=true;
        this.randomMoving = this.movement;
        this.collectMoving = new CollectMovingStrategy(this.board);
    }

    /*implementacja metod, które klasa TrainedHuman dziedziczy po klasie Human*/
    //tryAdd() nie zmienia się
    //getSafe() nie zmienia się
    //eat() nie zmienia sie (ponieważ zmienna weakend sprawdza się w metodzie interact)

    /* METODY 1
     * (nadpisanie metod)
     */

    /**
     * Aktualizuje stan wewnętrzny wyszkolonego człowieka.
     * Dziedziczy: sprawdzenie warunku, czy człowiek nadal jest chroniony przez czosnek i
     * aktualizuje liczbę kroków do końca ochrony.
     * <p>
     *     W dodatku do zawartości metody {@code updateCurrentState} z klasy {@link Human} jeszcze sprawdza, czy tryb
     *     weakening się skończył, a w przeciwnym przypadku zmniejsza czas na dany tryb o jeden krok.
     *     Też zmienia strategię ruchu w zależności od liczby czosnku w zapasie.
     * </p>
     */
    @Override
    public void updateCurrentState() {
        super.updateCurrentState();

        //czy koniec trybu weakend:
        if(this.lastWeakeningStep <= clock.getStep()- STEPS_FOR_GAIN) {
            this.weakend=false;
        } else {
            this.weakend=true;
        }
        //zamiana strategii ruchu
        if(this.garlicStock<=0) {
            this.movement = collectMoving;
        } else {
            this.movement = randomMoving;
        }
    }

    /**
     * Realizuje fazę interakcji wyszkolonego człowieka z obiektami znajdującymi się na tej samej komórce planszy.
     * <p>
     *      Najpierw próbuje zjeść czosnek w zależności od tego, czy jest w trybie weakening
     *      (zależny od zmiennej {@code weakened}).
     *      Dalej sprawdza, czy potrzebuje czosnku (warunek {@code if(movement == collectMoving)}) i jeżeli jest
     *      przy kontenerze - pobiera go.
     *      W przypadku znajdowania się na tej samej komórce w wyszkolonym człowiekiem próbuje go wyszkolić.
     * </p>
     */
    @Override
    public void interact() {
        Cell cell=board.getCell(this.position.getX(), this.position.getY());
        if(!this.weakend) {
            //zjedzenie czosnku
            List<Garlic> garlics=cell.getGarlics();
            while(!garlics.isEmpty() ){
                this.eat(garlics.get(0));
            }
            cell.getGarlics().clear();
            //rozrzucanie czosnku
            if(this.garlicStock>0) {
                this.tryThrow();
            }
        }
        if(movement == collectMoving) {
            int[] targetCoords = simulation.getCoordinatesOfContainer();
            Cell closestGarlicContainerCell = board.getClosestCellContainingGarlicContainer(position.getX(), position.getY(), targetCoords[0],  targetCoords[1],  targetCoords[2],  targetCoords[3]);

            int width = board.getWidth();
            int height = board.getHeight();
            if((Math.abs(vectorTorus(position.getX(), closestGarlicContainerCell.getX(), board.getWidth())) <=1) && (Math.abs(vectorTorus(position.getY(), closestGarlicContainerCell.getY(), board.getHeight())) <=1)) {
                this.collectGarlic();
            }
        }
        //rekrutowanie osob
        for(Agent agent : cell.getAgents()){
            if(agent instanceof Human){
                this.tryRecruit((Human)agent);
            }
        }
    }

    /* METODY 2
     * (pozostałe metody)
     */

    /**
     * Próbuje rekrutować zwykłą osobę, czyja instancja została podana jako parametr ({@code human}) w zależności
     * od wartości wylosowanej za pośrednictwem metody {@code radnomizer}.
     * <p>
     *     Sprawdza warunek, czy człowiek jest niewyszkolony oraz czy wartość losowa jest większa niż prawdopodobieństwo
     *     aktualne.
     * </p>
     * @param human                 Instancja człowieka, którego próbuje wyszkolić.
     */
    private void tryRecruit(Human human) {
        //losowanie za pomocą metody randomizer:
        if(!human.isTrained() && (randomizer(this.rectruitmentProb, simulation.getNumberOfTrainedHumanBeings(), MAX_NUMB_OF_TRAINED_HUMANBEINGS))) {
            Agent newTrainedHuman = new TrainedHuman(this.simulation, this.board, this.position.getX(), this.position.getY(), human.transformationProb, human.addProb, this.rectruitmentProb, this.throwProb,this.garlicStockMax, human.energyBoost, human.energyLoss);
            simulation.replaceAgent(human, newTrainedHuman);

            ConsoleColors.printlnYellow("<<Wyrekrutowanie czlowieka>>");
            stats.addInteractionOfType(InteractionType.RECRUITMENT);
        }
    }

    /**
     * Próbuje rzucić czosnek w zależności od wartości wylosowanej.
     * <p>
     *     Sprawdza warunek, czy wartość wylosowana z przedziału [0, 50] jest mniejsza niż prawdopodobieństwo
     *     rozrzucenia czosnku {@code throwProb}.
     *     Jeżeli uda się wyrzucić czosnek, to włącza tryb weakening (zależny od {@code weakend}).
     * </p>
     */
    private void tryThrow() {
        //losowanie z zakresu
        if(rand.nextFloat(RANGE_OF_PROBABILITY_OF_THROW) < this.throwProb) {

            Garlic garlic = new Garlic(this.position.getX(), this.position.getY());
            this.simulation.addGarlic(garlic);

            this.garlicStock-=1;
            this.weakend=true;
            this.lastWeakeningStep=clock.getStep();
            ConsoleColors.printlnYellow("<<Rozrzucenie nowego czosnku>>");
            stats.addInteractionOfType(InteractionType.GARLIC_THROW);
        }
    }

    /**
     * Zwiększa liczbę czosnku w zapasie wyszkolonej osoby o maksymalną wartość {@code garlicStockMax}.
     * Jest wywoływana w metodzie {@code interact()}.
     */
    private void collectGarlic() {
        this.garlicStock=this.garlicStockMax;
    }

}
