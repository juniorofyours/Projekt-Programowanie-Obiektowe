package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VampireTest {
    Simulation simulationMock;
    Board boardMock;
    Cell cellMock;
    SimulationClock clock=SimulationClock.getInstance();
    Vampire vampire;

    @BeforeEach
    void setup(){
        simulationMock=mock(Simulation.class);
        boardMock=mock(Board.class);
        cellMock=mock(Cell.class);
        when(boardMock.getCell(0,0)).thenReturn(cellMock);
        vampire=new Vampire(simulationMock, boardMock, 0, 0, 300, 300);
    }

    @Test
    void testShowUpAtNight(){
        vampire.setHidden(true);
        vampire.movement=new WaitingMovingStrategy();

        clock.setNight();
        vampire.updateCurrentState();

        assertFalse(vampire.isHidden(), "Wampir powinien się pokazać");
        assertTrue(vampire.movement instanceof RandomMovingStrategy, "Wampir powinien przyjąć strategię losowego poruszania się");
        verify(boardMock, description("Wampir powinien zostać dodany do planszy")).addToBoard(vampire);
    }
    @Test
    void testHideInTheDay(){
        vampire.setHidden(false);
        vampire.movement=new RandomMovingStrategy();

        clock.setDay();
        vampire.updateCurrentState();

        assertTrue(vampire.isHidden(), "Wampir powinien być ukryty");
        assertTrue(vampire.movement instanceof WaitingMovingStrategy, "Wampir powinien przyjąć strategię czekania");
        verify(boardMock, description("Wampir powinien zostać usunięty z planszy")).removeFromBoard(vampire);
    }

    @Test
    void testAttackWhenHumanIsNotSafe(){
        vampire.energyLevel=500;
        Human humanMock=mock(Human.class);
        when(humanMock.isSafe()).thenReturn(false);
        when(humanMock.getEnergyLoss()).thenReturn(300);
        List<Agent> agentList=new ArrayList<>();
        agentList.add(humanMock);
        when(cellMock.getAgents()).thenReturn(agentList);
        when(cellMock.getGarlics()).thenReturn(new ArrayList<>());

        vampire.interact();

        verify(humanMock, description("Człowiek powinien stracić energię")).loseEnergy(300);
        assertEquals(800, vampire.energyLevel, "Wampir powinien zyskać energię o wartość swojego energyBoost");

    }

    @Test
    void testAttackWhenHumanIsSafe(){
        vampire.energyLevel=500;
        Human humanMock=mock(Human.class);
        when(humanMock.isSafe()).thenReturn(true);
        List<Agent> agentList=new ArrayList<>();
        agentList.add(humanMock);
        when(cellMock.getAgents()).thenReturn(agentList);
        when(cellMock.getGarlics()).thenReturn(new ArrayList<>());

        vampire.interact();

        verify(humanMock, never().description("Człowiek nie powinien stracić energii")).loseEnergy(anyInt());
        assertEquals(500, vampire.energyLevel, "Energia wampira powinna pozostać na tym samym poziomie");
    }

    @Test
    void testGetAttackedWhenGarlic(){
        vampire.energyLevel=500;
        Garlic garlicMock=mock(Garlic.class);
        List<Garlic> garlicList=new ArrayList<>();
        garlicList.add(garlicMock);
        when(cellMock.getAgents()).thenReturn(new ArrayList<>());
        when(cellMock.getGarlics()).thenReturn(garlicList);
        doAnswer(invocationOnMock -> {
            garlicList.remove(garlicMock);
            return null;
        }).when(simulationMock).removeGarlic(garlicMock);

        vampire.interact();

        verify(simulationMock, description("Czosnek powinien być usunięty z symulacji")).removeGarlic(garlicMock);
        assertEquals(200, vampire.energyLevel, "Energia wampira powinna być zmniejszona o jego energyLoss");
    }

    @Test
    void testTryRemoveVampireWhenNoEnergy(){
        vampire.energyLevel=0;

        vampire.tryRemove();

        verify(simulationMock, description("Wampir powinien zostać usunięty z symulacji")).removeAgent(vampire);
    }

    @Test
    void testTryRemoveVampireWhenEnergyIsPositive(){
        vampire.energyLevel=1;

        vampire.tryRemove();

        verify(simulationMock, never().description("Wampir powinien zostać usunięty z symulacji")).removeAgent(vampire);
    }

}
