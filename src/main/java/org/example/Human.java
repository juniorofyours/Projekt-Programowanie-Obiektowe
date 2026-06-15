package org.example;

import lombok.Getter;
import java.util.List;

/**
 * Klasa reprezentująca agenta typu Człowiek w symulacji.
 * <p>
 * Człowiek jest aktywny przez cały czas swojego istnienia, aż do chwili usunięcia z planszy
 * (za pośrednictwem metody {@code tryRemove()} oddziedziczonej z klasy {@link Agent}).
 * Polują na dany rodzaj agentów Wampiry (klasa {@link Vampire}),
 * przez co podczas ataku na nich tracą energię ({@code energyLevel}).
 * Jedzą czosnek (klasa {@link Garlic}) znajdujący się w tej samej komórce,
 * dzięki zdobywają energię i dostają ochronę tymczasową ({@code safe}) na 3 następne kroki ({@code FINAL_OF_RESET}).
 * </p>
 */
public class Human extends Agent{
    /*zmienne instancyjne*/
    protected float transformationProb;
    protected float addProb;
    protected int stepsToReset;
    protected int lastGarlicStep;

    @Getter
    protected boolean safe;
    @Getter
    protected boolean trained;

    /*stałe*/
    final int MAX_NUMB_OF_HUMANBEINGS = 200;
    final int MAX_NUMB_OF_VAMPIRES = 200;
    final float RANGE = 100f; //zakres użyty w metodzie randomizer
    final int FINAL_OF_RESET = 3; //ile krokow zajmuje reset ochrony

    /*inne*/
    protected SimulationConfig config; //do tworzenia wampirów


    /**
     * Konstruuje nową instancję człowieka z określonymi parametrami.
     * @param simulation            Instancja symulacji.
     * @param board                 Plansza dwuwymiarowa, na której osadzony jest agent.
     * @param x                     Początkowa współrzędna x.
     * @param y                     Początkowa współrzędna y.
     * @param transformationProb    Prawdopodobieństwo zamiany w wampira.
     * @param addProb               Prawdopodobieństwo urodzenia nowego człowieka.
     * @param energyBoost           Ilość energii zyskiwana po ataku na człowieka.
     * @param energyLoss            Ilość energii tracona przy ataku przez czosnek.
     */
    public Human(Simulation simulation, Board board, int x, int y, float transformationProb, float addProb, int energyBoost, int energyLoss) {
        super(simulation, board, x, y, energyBoost, energyLoss);
        this.energyMax=1000;
        this.energyLevel=1000;

        this.transformationProb=transformationProb;
        this.addProb=addProb;
        this.config=SimulationConfig.getInstance();

        //wartosci poczatkowe
        this.lastGarlicStep=0;
        this.stepsToReset=0;
        this.safe=false;
        this.trained=false;
    }


    /* METODY 1
     * (implementacja metod, które w klasie Agent są abstrakcyjne)
     */

    /**
     * Aktualizuje stan wewnętrzny człowieka.
     * Sprawdza warunek, czy człowiek nadal jest chroniony przez czosnek i aktualizuje liczbę kroków do końca ochrony.
     */
    @Override
    public void updateCurrentState(){
        //ochrona:
        if (this.stepsToReset <= 0) {
            this.safe=false;
        }
        else {
            this.stepsToReset-=1;
            this.safe=true;
        }
        //zwiekszenie sie szans zamiany sie w wampira:
        //this.transformationProb+=addToTransformProb;
    }

    /**
     * Realizuje fazę interakcji człowieka z obiektami znajdującymi się na tej samej komórce planszy.
     * Jedyna interakcja, która zależy od człowieka, jest to zjedzenie wszystkich czosnków
     * znajdujących się na danej komórce.
     */
    @Override
    public void interact(){
        Cell cell=board.getCell(this.position.getX(), this.position.getY());
        List<Garlic> garlics=cell.getGarlics();
        for(int i=0; i<garlics.size();){
            this.eat(garlics.get(i));
        }
    }


    /* METODY 2
     * (nadpisanie metod)
     */

    /**
     * Weryfikuje, czy człowiek zostanie usunięty z symulacji z powodu braku energii, albo czy się zamieni w wampira.
     * <p>
     * Jeśli poziom energii spadnie do zera, losuje się wartość prawdopodobieństwa i sprawdza się,
     * czy człowiek transformuje się w wampira.
     * </p>
     * @return {@code true} jeśli człowiek umarł i został usunięty; {@code false} jeśli nie został usunięty.
     */
    @Override
    public boolean tryRemove() {
        if (energyLevel == 0) {
            if (randomizer(this.transformationProb, this.simulation.getNumberOfVampires(), MAX_NUMB_OF_VAMPIRES)) {
                Agent newVampire = new Vampire(this.simulation, this.board, this.position.getX(), this.position.getY(), config.getVampireConfig().getEnergyBoost(),config.getVampireConfig().getEnergyLoss());
                this.simulation.replaceAgent(this, newVampire);
                ConsoleColors.printlnRed("<<Zamiana czlowieka w wampira>>");
                stats.addInteractionOfType(InteractionType.TRANSFORMATION);
                return false;
            } else {
                //usuniecie osoby (Human)
                this.simulation.removeAgent(this);
                ConsoleColors.printlnRed("<<Smierc czlowieka>>");
                return true;
            }
        }
        return false;
    }


    /* METODY 3
     * (pozostałe metody)
     */

    /**
     * Losuje wartości w zależności od teraźniejszej liczby i maksymalnej liczby agentów i zwraca
     * {@code true} lub {@code false}, jeżeli udało się wylosować większą wartość
     * niż zadane prawdopodobieństwo {@code PROB} lub nie.
     * Zaletą danej metody jest dynamiczna wartość prawdopodobieństwa dla danego kroku ({@code actualProb}),
     * która zmniejsza prawdopodobieństwo wraz ze wzrostem liczby agentów danej grupy
     * (m.in. agentów klas {@link Human} {@link TrainedHuman})
     * <p>
     *     Dana metoda została użyta w metodach z losowaniem możliwości urodzenia, transformacji
     *     lub wyszkolenia człowieka (np. w metodzie {@code tryAdd}).
     * </p>
     * @param PROB                  Prawdopodobieństwo podstawowe.
     * @param NUMBER                Aktualna liczba agentów danej grupy (człowiek/wyszkolony człowiek).
     * @param MAX                   Maksymalna liczba agentów danej grupy (człowiek/wyszkolony człowiek).
     * @return {@code true} jeśli wylosowano liczbę większą niż aktualne prawdopodobieństwo ({@code actualProb}),
     * {@code false} jeśli nie wylosowano liczby większej od aktualnego prawdopodobieństwa.
     */
    //losowanie w zależności od liczby danej grupy agentów i maksymalnej liczby możliwej do stworzenia:
    protected boolean randomizer(float PROB, long NUMBER, int MAX) {
        float actualProb = PROB * (1.0f - NUMBER/MAX);

        if(actualProb<=0) return false;
        if(rand.nextFloat(RANGE) < actualProb) return true;
        return false;
    }

    /**
     * Próba dodania nowego agenta klasy {@link TrainedHuman} na plansze w zależności od wylosowanej wartości.
     * <p>
     *     Człowiek rodzi nową osobę (tworząc nową instancję) i nadaje jej wartości początkowe takie same jak swoje.
     * </p>
     */
    public void tryAdd(){
        if (randomizer(this.addProb, simulation.getNumberOfHumanBeings(), MAX_NUMB_OF_HUMANBEINGS)) {
            simulation.addAgent(new Human(this.simulation, this.board, this.getX(), this.getY(),
                    this.transformationProb, this.addProb, this.energyBoost, this.energyLoss));
            ConsoleColors.printlnGreen("<<Dodanie nowego czlowieka>>");
        }
    }

    /**
     * Je czosnek podany jak parametr do metody.
     * <p>
     *     Jedząc czosnek, człowiek usuwa go z symulacji i dostaje ochronę na trzy ({@code FINAL_OF_RESET}) kolejne
     *     kroki w przypadku, jeżeli jeszcze nie ma ochrony ({@code safe}).
     * </p>
     * @param garlic                Instancja czosnku, która ma zostać zjedzony.
     */
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
            this.stepsToReset = FINAL_OF_RESET;
        }
        ConsoleColors.printlnYellow("<<Zjedzenie czosnku przez czlowieka>>");
        stats.addInteractionOfType(InteractionType.GARLIC_EAT);
    }

}
