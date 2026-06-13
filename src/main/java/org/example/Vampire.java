package org.example;

import java.util.List;

/**
 * Klasa reprezentująca agenta typu Wampir w symulacji.
 * <p>
 * Aktywność wampira zależy bezpośrednio od cyklu dobowego.
 * W dzień przechodzi w stan ukrycia (jest usuwany z planszy, a jego ruch zostaje
 * zamrożony). W nocy powraca do chodzenia,
 * poluje na ludzi w tej samej komórce i jest atakowany przez czosnek
 * </p>
 */
public class Vampire extends Agent{
    private Boolean hidden;
    private MovingStrategy waitingForMoving;
    private MovingStrategy randomMoving;

    /**
     * Konstruuje nową instancję wampira z określonymi parametrami.
     * * @param simulation  Instancja symulacji.
     * @param board       Plansza dwuwymiarowa, na której osadzony jest agent.
     * @param x           Początkowa współrzędna x.
     * @param y           Początkowa współrzędna y.
     * @param energyBoost Ilość energii zyskiwana po ataku na człowieka.
     * @param energyLoss  Ilość energii tracona przy ataku przez czosnek.
     */
    public Vampire(Simulation simulation, Board board, int x, int y, int energyBoost, int energyLoss){
        super(simulation,board, x, y, energyBoost, energyLoss);
        hidden=false;
        this.randomMoving = this.movement;
        this.waitingForMoving = new WaitingMovingStrategy();
    }

    /**
     * Aktualizuje stan wewnętrzny wampira na podstawie zegara globalnego.
     * Metoda decyduje o tym, czy wampir się ukrywa czy nie.
     */
    @Override
    public void updateCurrentState(){
        if(!clock.isNight()) hide();
        else showUp();
    }

    /**
     * Przełącza wampira w tryb dziennego ukrycia.
     * Usuwa agenta z planszy oraz nadaje mu strategię chodzenia {@link WaitingMovingStrategy}
     */
    private void hide(){
        if(hidden) return;
        hidden=true;
        board.removeFromBoard(this);
        movement=waitingForMoving;

    }

    /**
     * Przełącza wampira w tryb aktywności
     * Dodaje z powrotem agenta do planszy i przywraca standardową strategię poruszania się.
     */
    private void showUp(){
        if(!hidden) return;
        hidden=false;
        board.addToBoard(this);
        movement=randomMoving;
    }

    /**
     * Realizuje fazę interakcji wampira z obiektami znajdującymi się na tej samej komórce planszy.
     * <p>
     * Jeśli wampir nie jest ukryty, sprawdza bieżącą komórkę oraz atakuje ludzi i je czosnek (który zmniejsza jego energię).
     * </p>
     */
    @Override
    public void interact(){
        if(hidden) return;
        Cell cell=board.getCell(position.getX(), position.getY());
        for(Agent agent : cell.getAgents()){
            if(agent instanceof Human){
                attack((Human)agent);
            }
        }
        List<Garlic> garlics=cell.getGarlics();
        for(int i=0; i<garlics.size();){
            this.getAttacked(garlics.get(i));
        }
    }

    /**
     * Przeprowadza atak na człowieka
     * <p>
     * Atak zachodzi tylko wtedy, gdy człowiek nie jest chroniony.
     * Wampir podwyższa wtedy swój poziom energii i zmniejsza energię człowieka.
     * </p>
     * * @param human Atakowany człowiek.
     */
    private void attack(Human human){
        if(human.isSafe()) return;
        boostEnergy(energyBoost);
        human.loseEnergy(human.getEnergyLoss());
        ConsoleColors.printlnYellow("<<Atak wampira na czlowieka>>");
        stats.addInteractionOfType(InteractionType.ATTACK);
    }

    /**
     * Obsługuje interakcję wampira z czosnkiem.
     * <p>
     * Wampir je czosnek, usuwając go z całej symulacji.
     * W wyniku kontaktu traci energię.
     * </p>
     * * @param garlic Czosnek jedzony przez wampira i atakujący go.
     */
    private void getAttacked(Garlic garlic){
        simulation.removeGarlic(garlic);
        loseEnergy(energyLoss);
        ConsoleColors.printlnYellow("<<Zaatakowanie wampira przez czosnek>>");
        stats.addInteractionOfType(InteractionType.GARLIC_ATTACK);
    }

}
