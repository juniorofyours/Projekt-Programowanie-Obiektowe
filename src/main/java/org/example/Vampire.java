package org.example;

import java.util.List;

public class Vampire extends Agent{
    private Boolean hidden;
    public Vampire(Simulation simulation, Board board, int x, int y, int energyBoost, int energyLoss){
        super(simulation,board, x, y, energyBoost, energyLoss);
        hidden=false;

    }
    public void updateCurrentState(){
        if(!clock.isNight()) hide();
        else showUp();
    }

    private void hide(){
        if(hidden) return;
        hidden=true;
        board.removeFromBoard(this);
        movement=new WaitingMovingStrategy();

    }
    private void showUp(){
        if(!hidden) return;
        hidden=false;
        board.addToBoard(this);
        movement=new RandomMovingStrategy();
    }

    public Boolean isHidden(){
        return hidden;
    }

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

    private void attack(Human human){
        if(human.isSafe()) return;
        boostEnergy(energyBoost);
        human.loseEnergy(human.getEnergyLoss());
        ConsoleColors.printlnYellow("<<Atak wampira na czlowieka>>");
        stats.addInteractionOfType(InteractionType.ATTACK);
    }
    private void getAttacked(Garlic garlic){
        simulation.removeGarlic(garlic);
        loseEnergy(energyLoss);
        ConsoleColors.printlnYellow("<<Zaatakowanie wampira przez czosnek>>");
        stats.addInteractionOfType(InteractionType.GARLIC_ATTACK);
    }
}
