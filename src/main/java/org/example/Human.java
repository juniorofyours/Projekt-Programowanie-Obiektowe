package org.example;

public class Human extends Agent{
    protected float transformationProb;
    protected float addProb;
    protected int stepsToReset;
    protected int lastGarlicStep;
    protected boolean safe;

    //DODAC w przyszlosci do klasy z parametrami:
    final float rangeToAdd= 10.0f; //albo np. = addProb+1
    final float rangeToTransform = 10.0f;
    final int stepsToGainDefence = 4; //liczba krokow po zjedzeniu czosnku, kiedy po nastepnym zjedzeniu czosnku,
                                      //można zyskać znowu ochronę
    final int vampEnergyBoost=1000, vampEnergyLoss = 1000;

    public Human(Simulation simulation, Board board, int x, int y, float transformationProb, float addProb, int energyBoost, int energyLoss) {
        super(simulation, board, x, y, energyBoost, energyLoss); //tez ustawia np energyMax=1000 jak w konstruktorze klasy Agent
        this.transformationProb=transformationProb;
        this.addProb=addProb;

        //dane wartoci od początku są zerowe:
        this.lastGarlicStep=0;
        this.stepsToReset=0;
    }

    //impelementacja metod, ktore w klasie Agent sa abstrakcyjne:
    public void updateCurrentState(){

    } //metoda, która zostanie zaimplementowana w klasach
    //    poszczególnych agentów aktualizująca ich stan
    public void interact(){

    } //metoda, która zostanie zaimplementowana w klasach
//    poszczególnych agentów przeprowadzająca ich interkacje z otoczeniem

    //metoda z zadanym prawdopodbienstwem (addProb: float) dodaje czlowieka do planszy
    public void tryAdd(){

        //czy osoba pojawi sie (czy zmienna losowa jest wieksza niz zadana wartosc)
        if (rand.nextFloat(rangeToAdd) > this.addProb) {

            //losowanie komórki, która jest dostępna (isUsable)
            boolean statusCheck = false;
            int randX, randY;
            {
                randX=rand.nextInt(this.board.getWidth()); //od 0 do boardWidth
                randY=rand.nextInt(this.board.getHeight()); // od 0 do boardHeight
                Cell checkCell = board.getCell(randX, randY);
                statusCheck = checkCell.isUsable();
            }while(statusCheck);

            //tworzenie nowego czlowieka
            Agent newOne = new Human(this.simulation, this.board, randX, randY, this.transformationProb, this.addProb, this.energyBoost, this.energyLoss);

            //dodanie nowego agenta na plansze
            this.board.addToBoard(newOne);
        }
    }

    public void eat(Garlic garlic){

        //zwiekszenie energii u osoby
        this.boostEnergy(this.energyBoost);

        //usuniecie czosnku
        Cell cellWithGarlic=board.getCell(this.getX(), this.getY());
        cellWithGarlic.removeGarlic(garlic); //dla przyszlych korekt: czy dodajemy dany obiekt czosnku
                                             // do kontenera czy zostawiamy gdzoes w pamieci?

        //dodanie ochrony
        if(this.stepsToReset<=0) {
            this.safe=true;
            //ustawienie kroku, kiedy byl ostatnio wziety czosnek
            this.lastGarlicStep = simulation.getCurrentStep();
            //ustawienie liczby krokow z ochroną
            this.stepsToReset = this.lastGarlicStep;
        }
    }

    public boolean isSafe(){
        //this.stepsToReset=
        //sprawdza czy liczba krokow do anulowania ochrony danej przez czosnek
        /*if (this.stepsToReset <= 0) {
            this.safe=false;
        }
        else {
            this.stepsToReset-=1;
            this.safe=true;
        }
        */ //chyba to bedzie w updateCurrentState
        return this.safe;
    }

    //nadpisane metody:
    @Override
    public boolean tryRemove(){ //metoda uswająca agenta, jesli ma energię=0, powinna być nadpisana dla
                                //ludzi by z danym prawdopodobienstwem zamienic ich w wampiry
        if(energyLevel==0) {
            if(rand.nextFloat(rangeToTransform) > this.transformationProb) {
                Agent newVampire = Vampire(this.simulation, this.board, this.getX(), this.getY(), vampEnergyBoost, vampEnergyLoss);
                this.board.addToBoard(newVampire);
            }
            simulation.removeAgent(this);
            return true;
        }
        return false;
    }

}
//notatki odnosnie Human:
//tryRemove() powinno byc nadpisane, poniewaz czlowiek moze zostac wampirem.
