package org.example;

import java.util.List;

public class Vampire extends Agent{
    private Boolean hidden;
    public Vampire(Simulation simulation, Board board, int x, int y, int energyBoost, int energyLoss){
        super(simulation,board, x, y, energyBoost, energyLoss);
        hidden=false;

    }
    public void updateCurrentState(){
        if(simulation.getCurrentHour()==simulation.getSunriseHour()) hide();
        else if(simulation.getCurrentHour()==simulation.getSunsetHour()) showUp();
    }

    private void hide(){
        hidden=true;
        board.removeFromBoard(this);

    }
    private void showUp(){
        hidden=false;
        board.addToBoard(this);
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
    }
    private void getAttacked(Garlic garlic){
        simulation.removeGarlic(garlic);
        loseEnergy(energyLoss);
        ConsoleColors.printlnYellow("<<Zaatakowanie wampira przez czosnek>>");
    }
}
