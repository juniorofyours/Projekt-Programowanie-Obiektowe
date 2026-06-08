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

    /*Parametry*/
    final float BASIC_PROB_OF_TRAINING=50.0f;
    final int MAX_NUMB_OF_TRAINED_HUMANBEINGS = 200;

    final int stepsForGain = 2; //np    //- ile krkokow zajmuje wylaczeni etrybu weakend
    final float rangeOfProbabilityOfRecruitment = 10.0f; //zakres prawdop., z jakim osoba moze zostac zrekrutowana
    final float addToRecruitmentProb=0.05f; //dodaje sie do recruitmentProb
    final float rangeOfProbabilityOfThrow = 50.0f;
    final float addToThrowProb=0.5f;

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
    //eat() nie zmienia sie (poniewaz zmienna weakend sprawdza sie w metodzie interact)

    @Override
    public void updateCurrentState() {
        //ochrona i prawdop. zamiany sie w wampira; //## dla przyszlych korekt: o ile jest taka sama dla wytrenowanej osoby
        super.updateCurrentState();

        //czy koniec trybu weakend:
        if(this.lastWeakeningStep <= clock.getStep()-stepsForGain) {
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
        if(!human.isTrained() && (randomizer(this.rectruitmentProb, simulation.getNumberOfHumanBeings(), MAX_NUMB_OF_TRAINED_HUMANBEINGS))) {
            Agent newTrainedHuman = new TrainedHuman(this.simulation, this.board, this.position.getX(), this.position.getY(), human.transformationProb, human.addProb, this.rectruitmentProb, this.throwProb,this.garlicStockMax, human.energyBoost, human.vampEnergyLoss);
            simulation.replaceAgent(human, newTrainedHuman);

            ConsoleColors.printlnYellow("<<Wyrekrutowanie czlowieka>>");
            stats.addInteractionOfType(InteractionType.RECRUITMENT);
        }
    }

    //metoda: proba rozrzucenia czosnku
    private void tryThrow() { //##do zmiany: prawdopodbienstwo
        //warunek weakend i garlickStock!=0 jest sprawdzany wewnatrz interact()
        if(rand.nextFloat(rangeOfProbabilityOfThrow) < this.throwProb) {

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
