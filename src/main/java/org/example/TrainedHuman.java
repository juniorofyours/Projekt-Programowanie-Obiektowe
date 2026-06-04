package org.example;

import java.util.List;

public class TrainedHuman extends Human{
    private float rectruitmentProb;
    private float throwProb;
    private int garlicStock;
    private int garlicStockMax;
    private int lastWeakeningStep;
    private boolean weakend;

    /*DODAC w przyszlosci do klasy z parametrami:*/
    final int stepsForGain = 2; //np    //- ile krkokow zajmuje wylaczeni etrybu weakend
    final float rangeOfProbabilityOfRecruitment = 10.0f; //zakres prawdop., z jakim osoba moze zostac zrekrutowana
    final float addToRecruitmentProb=0.05f; //dodaje sie do recruitmentProb
    final float rangeOfProbabilityOfThrow = 10.0f;
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
        if(this.lastWeakeningStep <= this.simulation.getCurrentStep()-stepsForGain) {
            this.weakend=false;
        }
        else {
            this.weakend=true;
        }
    }

    @Override
    public void interact() {
        Cell cell=board.getCell(this.position.getX(), this.position.getY());
        if(!this.weakend) {
            //zjedzenie czosnku
            List<Garlic> garlics=cell.getGarlics();
            for(int i=0; i<garlics.size();){
                this.eat(garlics.get(i));
            }
            //rozrzucanie czosnku
            if(this.garlicStock>0) {
                this.tryThrow();
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

    //metoda: prawdopodobienstwo rekrutacji osoby
    //prawdop. dziala na zasadzie, ze zwieksza sie zmienna instancyjna recruitmentProb (dzieki stalej addToRecrutitmentProb)
    //i jezeli zmienna jest wieksza niz zmienna losowa wylosowana z zakresu rangeOfProbabilityRecrutiment, to zachodzi rekrutacja
    private void tryRecruit(Human human) {
            if(!human.isTrained() && (rand.nextFloat(rangeOfProbabilityOfRecruitment) < this.rectruitmentProb)) {
                //throwProb i recruitmentProb = 0 dla nowej wyszkolonej osoby:
                Agent newTrainedHuman = new TrainedHuman(this.simulation, this.board, this.position.getX(), this.position.getY(), human.transformationProb, human.addProb, 0, 0,this.garlicStockMax, human.energyBoost, human.vampEnergyLoss);

                    simulation.replaceAgent(human, newTrainedHuman);

                ConsoleColors.printlnYellow("<<Wyrekrutowanie czlowieka>>");

                this.rectruitmentProb=0;
            }
            this.rectruitmentProb+=addToRecruitmentProb;
    }

    //metoda: proba rozrzucenia czosnku
    private void tryThrow() { //##do zmiany: prawdopodbienstwo
        //warunek weakend i garlickStock!=0 jest sprawdzany wewnatrz interact()
        if(rand.nextFloat(rangeOfProbabilityOfThrow) < this.throwProb) {

            Garlic garlic = new Garlic(this.simulation, this.board, this.position.getX(), this.position.getY());
            this.simulation.addGarlic(garlic);

            this.garlicStock-=1;
            this.weakend=true;
            this.lastWeakeningStep=simulation.getCurrentStep();
            this.throwProb=0;
            ConsoleColors.printlnYellow("<<Rozrzucenie nowego czosnku>>");
        }
        this.throwProb+=addToThrowProb;
    }

    private void collectGarlic() {
        this.garlicStock=this.garlicStockMax;
    }

}
