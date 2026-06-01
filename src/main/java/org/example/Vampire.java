package org.example;

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
        for(Garlic garlic : cell.getGarlics()){
            getAttacked(garlic);
        }
    }

    private void attack(Human human){
        if(human.isSafe()) return;
        boostEnergy(energyBoost);
        human.loseEnergy(human.getEnergyLoss());
    }
    private void getAttacked(Garlic garlic){
        simulation.removeGarlic(garlic);
        loseEnergy(energyLoss);
    }
}
