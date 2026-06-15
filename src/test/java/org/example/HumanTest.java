package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HumanTest {

    private Simulation simulation;
    private Board board;
    private Cell cell;
    private Garlic garlic;

    private Human human;

    @BeforeEach
    void setup(){
        simulation=mock(Simulation.class);
        board=mock(Board.class);
        cell=mock(Cell.class);
        when(board.getCell(0, 0)).thenReturn(cell);
        human=new Human(simulation, board, 0, 0, 0.1f, 0.1f, 300, 300);
    }

    @Test
    void testUpdateCurrentStateWhenStepsToResetIsZero() {
        human.stepsToReset=0;
        human.safe=false;

        human.updateCurrentState();
        assertFalse(human.isSafe(), "Człowiek nie powinien mieć ochrony, gdy stepsToReset <= 0");
    }

    @Test
    void testUpdateCurrentStateWhenStepsToResetIsPositive() {
        human.stepsToReset = 2;
        human.safe = true;

        human.updateCurrentState();

        assertEquals(1, human.stepsToReset);
        assertTrue(human.isSafe());
    }

    @Test
    void testEatGarlic(){
        human.energyLevel=500;
        Garlic garlicMock=mock(Garlic.class);
        List<Garlic> garlicList=new ArrayList<>();
        garlicList.add(garlicMock);
        when(cell.getGarlics()).thenReturn(garlicList);
        doAnswer(invocationOnMock -> {
            garlicList.remove(garlicMock);
            return null;
        }).when(simulation).removeGarlic(garlicMock);

        human.interact();

        verify(simulation, description("Czosnek powinien być usunięty z symulacji")).removeGarlic(garlicMock);
        assertEquals(800, human.energyLevel, "Energia człowieka powinna być zwiększona o jego energyBoost");
        assertTrue(human.isSafe(), "Po zjedzeniu czosnku człowiek powinien mieć ochronę");
        assertEquals(3, human.stepsToReset, "Licznik ma wynosić FINAL_OF_RESET (3)");
    }


    @Test
    void testTryRemoveWhenEnergyIsAboveZero() {
        human.energyLevel = 300;

        boolean result = human.tryRemove();

        assertFalse(result, "tryRemove powinno zwrócić false, jeśli energia > 0");
        verify(simulation, never()).removeAgent(any());
    }

    @Test
    void testTryRemoveWhenEnergyEqualsZero() {
        human.energyLevel = 0;

        boolean result = human.tryRemove();

        assertTrue(result, "tryRemove powinno zwrócić true, jeśli energia = 0");
        verify(simulation, times(1)).removeAgent(any());
    }

    @Test
    void testInteractEatGarlicFromCell() {

        when(board.getCell(anyInt(), anyInt())).thenReturn(cell);

        List<Garlic> garlicList = new ArrayList<>();
        garlicList.add(garlic);

        // cell.getGarlics() zwróci listę z jednym czosnkiem
        when(cell.getGarlics()).thenReturn(garlicList);

    }

}
