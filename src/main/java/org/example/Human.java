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
    //metoda z zadanym prawdopodbienstwem (addProb: float) dodaje czlowieka do planszy
    //prawdopodobienstwo dziala na zasadzie, czy losuje sie liczba z jakiegos zakresu (rangeOfProbabilityOfAdd) i porownuje sie do
    //zmiennej (addProb) danego obiektu, która się zwiększa z każdym krokiem (+=addToAddProb)
    public void tryAdd(){
        //czy osoba pojawi sie (czy zmienna losowa jest wieksza niz zadana wartosc)
//        Dałem 0 zamiast addProb, bo addProb robiło się nieprzewidywalne i szybko rosło i praktycznie
//        co krok rodziło się coraz więcej ludzi zmierzając do nieskonczonosci (co mi zawiesiło komputer)
        if (rand.nextFloat(rangeOfProbabilityOfAdd) < 0) {

            //losowanie komórki, która jest dostępna (isUsable)      //##dla przyszlych korekt: zostawiamy tak czy bez losowania?
//           skomentowałem twój kod z losowaniem miejsca dla nowej osoby, bo myślę, że randomowe pojawianie się nowej osoby
//            znikąd w losowym miejscu by nie wyglądało najlpiej + nielosowanie miejsca ułatwia znacząco kod. Można później
//            przywrócić losowanie miejsca, ale wtedy trzeba to zrobić z blokiem try catch
            boolean success;
//            int randX, randY;
            simulation.addAgent(new Human(this.simulation, this.board, this.getX(), this.getY(),
                    this.transformationProb, this.addProb, this.energyBoost, this.energyLoss));

//            do{
//                try{
//                    randX=rand.nextInt(this.board.getWidth()); //od 0 do boardWidth
//                    randY=rand.nextInt(this.board.getHeight()); // od 0 do boardHeight
//                    simulation.addAgent(new Human(this.simulation, this.board, randX, randY,
//                            this.transformationProb, this.addProb, this.energyBoost, this.energyLoss));
//                    success=true;
//                }catch(UnusableCellException error){
//                    success=false;
//                }
//            }while(!success);
            this.addProb=0;
            ConsoleColors.printlnGreen("<<Dodanie nowego czlowieka>>");
//
        }
        this.addProb+=addToAddProb;
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
            this.lastGarlicStep = simulation.getCurrentStep();
            //ustawienie liczby krokow z ochroną
            this.stepsToReset = finalOfReset;
        }
        ConsoleColors.printlnYellow("<<Zjedzenie czosnku przez czlowieka>>");
    }


    public boolean isSafe(){
        return this.safe;
    }
    public boolean isTrained(){return trained;}

    /*nadpisane metody:*/
    @Override
    public boolean tryRemove() { //metoda uswająca agenta, jesli ma energię=0, albo kiedy Human jest wyszkolony przez TrainedHuman
        if (energyLevel == 0) {
            if (rand.nextFloat(rangeOfProbabilityToTransform) < this.transformationProb) {
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
