package org.example;

import java.util.List;

public class Human extends Agent{
    /*zmienne instancyjne*/
    protected float transformationProb;
    protected float addProb;
    protected int stepsToReset;
    protected int lastGarlicStep;
    protected boolean safe;
    protected boolean trained;

    /*Parametry*/
    final float BASIC_PROB_OF_GIVING_BIRTH=0.01f;
    final float BASIC_PROB_OF_TRANSFORMING=50.0f;
    final int MAX_NUMB_OF_HUMANBEINGS = 200;
    final int MAX_NUMB_OF_VAMPIRES = 200;
    final float RANGE = 100;

    /*DODAC w przyszlosci do klasy z parametrami:*/
    final float rangeOfProbabilityOfAdd = 10.0f; //albo np. = addProb+1 //parametr zakresu z jakim prawdop. osoba moze rodzic
    final float rangeOfProbabilityToTransform = 10.0f; //zakres prawdop. z jakim osoba moze sie zamienic w wampira
    final int stepsToGainDefence = 4; //liczba krokow po zjedzeniu czosnku, kiedy po nastepnym zjedzeniu czosnku,
                                      //można zyskać znowu ochronę
    final int vampEnergyBoost=1000, vampEnergyLoss = 1000; //parametr z ktorym tworza sie wampiry
    final int finalOfReset=3; //ile krokow zajmuje reset ochrony
    final float addToAddProb=0.25f;
    final float addToTransformProb=0.04f; //dodaje sie z kazdym krokiem do transformationProb

    /*konstruktor*/
    public Human(Simulation simulation, Board board, int x, int y, float transformationProb, float addProb, int energyBoost, int energyLoss) {
        super(simulation, board, x, y, energyBoost, energyLoss); //tez ustawia np energyMax=1000 jak w konstruktorze klasy Agent
        this.energyMax=1000;
        this.energyLevel=1000;

        this.transformationProb=transformationProb;
        this.addProb=addProb;

        //wartosci poczatkowe
        this.lastGarlicStep=0;
        this.stepsToReset=0;
        this.safe=false;
        this.trained=false;
    }

    /*impelementacja metod, ktore w klasie Agent sa abstrakcyjne:*/
    public void updateCurrentState(){ //metoda aktualizujaca stan czlowieka
        //ochrona:
        if (this.stepsToReset <= 0) {
            this.safe=false;
        }
        else {
            this.stepsToReset-=1;
            this.safe=true;
        }
        //zwiekszenie sie szans zamiany sie w wampira:
        this.transformationProb+=addToTransformProb;
    }

    public void interact(){ //metoda: interakcja człowieka z otoczeniem
        Cell cell=board.getCell(this.position.getX(), this.position.getY());
        List<Garlic> garlics=cell.getGarlics();
        for(int i=0; i<garlics.size();){
            this.eat(garlics.get(i));
        }
    }

    /*pozostale metody*/

    protected boolean randomizer(float PROB, long NUMBER, int MAX) {
        float actualProb = PROB * (1.0f - NUMBER/MAX);

        if(actualProb<=0) return false;
        if(rand.nextFloat(RANGE) < actualProb) return true;
        return false;
    }


    //rodzi nową osobę:
    public void tryAdd(){
        if (randomizer(BASIC_PROB_OF_GIVING_BIRTH, simulation.getNumberOfHumanBeings(), MAX_NUMB_OF_HUMANBEINGS)) {
            simulation.addAgent(new Human(this.simulation, this.board, this.getX(), this.getY(),
                    this.transformationProb, this.addProb, this.energyBoost, this.energyLoss));
            ConsoleColors.printlnGreen("<<Dodanie nowego czlowieka>>");
        }
    }

    public void eat(Garlic garlic){

        //zwiekszenie energii u osoby
        this.boostEnergy(this.energyBoost);

        //usuniecie czosnku
        this.simulation.removeGarlic(garlic);

        //dodanie ochrony
        if(this.stepsToReset<=0) {
            this.safe=true;
            //ustawienie kroku, kiedy byl ostatnio wziety czosnek
            this.lastGarlicStep = clock.getStep();
            //ustawienie liczby krokow z ochroną
            this.stepsToReset = finalOfReset;
        }
        ConsoleColors.printlnYellow("<<Zjedzenie czosnku przez czlowieka>>");
        stats.addInteraction();
    }


    public boolean isSafe(){
        return this.safe;
    }
    public boolean isTrained(){
        return trained;
    }

    /*nadpisane metody:*/
    @Override
    public boolean tryRemove() { //metoda uswająca agenta, jesli ma energię=0, albo kiedy Human jest wyszkolony przez TrainedHuman
        if (energyLevel == 0) {
            if (randomizer(BASIC_PROB_OF_TRANSFORMING, this.simulation.getNumberOfVampires(), MAX_NUMB_OF_VAMPIRES)) {
                Agent newVampire = new Vampire(this.simulation, this.board, this.position.getX(), this.position.getY(), vampEnergyBoost, vampEnergyLoss);
                this.simulation.replaceAgent(this, newVampire);
                ConsoleColors.printlnRed("<<Zamiana czlowieka w wampira>>");
                return false;
            } else {
                //usuniecie osoby (Human)
                this.simulation.removeAgent(this);
                ConsoleColors.printlnRed("<<Smierc czlowieka>>");
                return true;
            }
        }
        //## przyszle korekty: zmienic impelementacje rekrutacji:
        /*Cell cell=board.getCell(position.getX(), position.getY());
        for(Agent agent : cell.getAgents()){
            if(agent instanceof TrainedHuman){
                ((TrainedHuman) agent).tryRecruit(this);
            }
        }*/
        return false;
    }

}
