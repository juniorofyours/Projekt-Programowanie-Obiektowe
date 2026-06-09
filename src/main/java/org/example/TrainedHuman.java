package org.example;

import java.util.List;

//importowanie metody:
import static org.example.CollectMovingStrategy.vectorTorus;

public class TrainedHuman extends Human{
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

    /*konstruktor*/
    public TrainedHuman(Simulation simulation, Board board, int x, int y, float transformationProb, float addProb, float rectruitmentProb, float throwProb,int garlicStockMax, int energyBoost, int energyLoss) {
        super(simulation, board, x, y, transformationProb, addProb, energyBoost, energyLoss); //super() wywyoluje konsturktor
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

    /*implementacja metod, ktore klasa TrainedHuman dziedziczy po klasie Human*/
    //tryAdd() nie zmienia sie
    //isSafe() nie zmienia sie
    //eat() nie zmienia sie (poniewaz zmienna weakend sprawdza się w metodzie interact)

    @Override
    public void updateCurrentState() {
        //ochrona i prawdop. zamiany sie w wampira; //## dla przyszlych korekt: o ile jest taka sama dla wytrenowanej osoby
        super.updateCurrentState();

        //czy koniec trybu weakend:
        if(this.lastWeakeningStep <= clock.getStep()- STEPS_FOR_GAIN) {
            this.weakend=false;
        }
        else {
            this.weakend=true;
        }

        if(this.garlicStock<=0) {
            this.movement = collectMoving;
        }
        else {
            this.movement = randomMoving;
        }
    }

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

    /*pozostale metody*/

    private void tryRecruit(Human human) {
        //losowanie za pomocą metody randomizer:
        if(!human.isTrained() && (randomizer(this.rectruitmentProb, simulation.getNumberOfTrainedHumanBeings(), MAX_NUMB_OF_TRAINED_HUMANBEINGS))) {
            Agent newTrainedHuman = new TrainedHuman(this.simulation, this.board, this.position.getX(), this.position.getY(), human.transformationProb, human.addProb, this.rectruitmentProb, this.throwProb,this.garlicStockMax, human.energyBoost, human.energyLoss);
            simulation.replaceAgent(human, newTrainedHuman);

            ConsoleColors.printlnYellow("<<Wyrekrutowanie czlowieka>>");
            stats.addInteractionOfType(InteractionType.RECRUITMENT);
        }
    }

    //metoda: proba rozrzucenia czosnku
    private void tryThrow() {
        //losowanie z zakresu
        if(rand.nextFloat(RANGE_OF_PROBABILITY_OF_THROW) < this.throwProb) {

            Garlic garlic = new Garlic(this.simulation, this.board, this.position.getX(), this.position.getY());
            this.simulation.addGarlic(garlic);

            this.garlicStock-=1;
            this.weakend=true;
            this.lastWeakeningStep=clock.getStep();
            ConsoleColors.printlnYellow("<<Rozrzucenie nowego czosnku>>");
            stats.addInteractionOfType(InteractionType.GARLIC_THROW);
        }
        //this.throwProb+=addToThrowProb;
    }

    private void collectGarlic() {
        this.garlicStock=this.garlicStockMax;
    }

}
