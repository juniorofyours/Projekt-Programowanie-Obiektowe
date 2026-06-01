package org.example;

public class TrainedHuman extends Human{
    private float rectruitmentProb;
    private float throwProb;
    private int garlicStock;
    private int garlicStockMax;
    private int lastWeakeningStep;
    private int stepsUntilGainingPower; //ile krokow zostalo do weakend=false
    private boolean weakend;

    /*DODAC w przyszlosci do klasy z parametrami:*/
    final int stepsForGain = 2; //np    //dla stepsUntilGainPower
    final float rangeOfProbabilityOfRecruitment = 10.0f; //zakres prawdop., z jakim osoba moze zostac zrekrutowana
    final float addToRecruitmentProb=0.05f; //dodaje sie do recruitmentProb
    final float rangeOfProbabilityOfThrow = 10.0f;
    final float addToThrowProb=0.5f;

    /*konstruktor*/
    public TrainedHuman(Simulation simulation, Board board, int x, int y, float transformationProb, float addProb, float rectruitmentProb, float throwProb, int energyBoost, int energyLoss) {
        super(simulation, board, x, y, transformationProb, addProb, energyBoost, energyLoss); //super() wywyoluje konsturktor
                                         // klasy nadrzednej, wiec zmienne np energyMax lub np stepsToReset sa takie same (1000 i 0)
        this.rectruitmentProb=rectruitmentProb;
        this.throwProb=throwProb;

        //wartosci poczatkowe
        this.lastWeakeningStep=0;
        this.stepsUntilGainingPower=0;
        this.weakend=false;
        this.garlicStock=0;
        this.garlicStockMax=3; //np.

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
        if(this.stepsUntilGainingPower <= 0) {
            this.weakend=false;
        }
        else {
            stepsUntilGainingPower-=1;
            this.weakend=true;
        }
    }

    @Override
    public void interact() {
        Cell cell=board.getCell(this.position.getX(), this.position.getY());
        if(!this.weakend) {
            //zjedzenie czosnku
            for (Garlic garlic : cell.getGarlics()) {
                this.eat(garlic);
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
            if(rand.nextFloat(rangeOfProbabilityOfRecruitment) < this.rectruitmentProb) {
                //throwProb i recruitmentProb = 0 dla nowej wyszkolonej osoby:
                Agent newTrainedHuman = new TrainedHuman(this.simulation, this.board, this.position.getX(), this.position.getY(), human.transformationProb, human.addProb, 0, 0, human.energyBoost, human.vampEnergyLoss);

                //dodanie osoby wyszkolonej
                this.board.addToBoard(newTrainedHuman);

                //usuniecie zwyklej osoby
                this.board.removeFromBoard(human);
                this.simulation.removeAgent(human);

                this.rectruitmentProb=0;
            }
            this.rectruitmentProb+=addToRecruitmentProb;
    }

    //metoda: proba rozrzucenia czosnku
    private void tryThrow() {
        //warunek weakend i garlickStock!=0 jest sprawdzany wewnatrz interact()
        if(rand.nextFloat(rangeOfProbabilityOfThrow) < this.throwProb) {
            Cell cell = board.getCell(this.position.getX(), this.position.getY());

            Garlic garlic = new Garlic(this.simulation, this.board, this.position.getX(), this.position.getY());
            this.board.addGarlicToBoard(garlic);

            this.garlicStock-=1;
            this.weakend=true;
            this.lastWeakeningStep=simulation.getCurrentStep();
            this.throwProb=0;
        }
        this.throwProb+=addToThrowProb;
    }

    /*do zrobienia:*/
    private void seekContiainer() {

    }

    private void collectGarlic() {

    }

}
