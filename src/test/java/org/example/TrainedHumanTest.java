package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainedHumanTest {

    private Simulation simulation;
    private Board board;
    private Cell cell;
    private Garlic garlic;
    private MovingStrategy collectMoving;
    private MovingStrategy randomMoving;

    private TrainedHuman trainedHuman;

    @BeforeEach
    void setup(){
        simulation=mock(Simulation.class);
        board=mock(Board.class);
        cell=mock(Cell.class);
        when(board.getCell(0, 0)).thenReturn(cell);
        trainedHuman = new TrainedHuman(simulation, board, 0, 0, 0.1f, 0.1f, 0.1f, 1f, 3, 300, 300);
        trainedHuman.collectMoving = new CollectMovingStrategy(board);
        trainedHuman.randomMoving = new RandomMovingStrategy();
    }

    @Test
    void testUpdateCurrentStateWhenGarlicStockIsZero() {
        trainedHuman.garlicStock=0;
        trainedHuman.movement = randomMoving;

        trainedHuman.updateCurrentState();

        assertInstanceOf(CollectMovingStrategy.class, trainedHuman.movement,
                "Gdy skończy się czosnek, strategia ruchu ma się zmienić na CollectMovingStrategy");
    }

    @Test
    void testUpdateCurrentStateWhenGarlicStockIsNotZero() {
        trainedHuman.garlicStock=3;
        trainedHuman.movement = collectMoving;

        trainedHuman.updateCurrentState();

        assertInstanceOf(RandomMovingStrategy.class, trainedHuman.movement,
                "Gdy jest czosnek w zapasie, strategia ruchu powinna być RandomMovingStrategy");
    }

    @Test
    void testUpdateCurrentStateWhenStepsToResetIsZero() {
        trainedHuman.stepsToReset=0;
        trainedHuman.safe=false;

        trainedHuman.updateCurrentState();
        assertFalse(trainedHuman.isSafe(), "Człowiek nie powinien mieć ochrony, gdy stepsToReset <= 0");
    }

    @Test
    void testUpdateCurrentStateWhenStepsToResetIsPositive() {
        trainedHuman.stepsToReset = 2;
        trainedHuman.safe = true;

        trainedHuman.updateCurrentState();

        assertEquals(1, trainedHuman.stepsToReset);
        assertTrue(trainedHuman.isSafe());
    }

    @Test
    void testEatGarlicWhenWeakeningIsTrue(){
        trainedHuman.weakend=true;

        Garlic garlicMock=mock(Garlic.class);
        List<Garlic> garlicList = new ArrayList<>();
        garlicList.add(garlicMock);

        List<Garlic> spyGarlicList = spy(garlicList);
        when(cell.getGarlics()).thenReturn(spyGarlicList);


        trainedHuman.interact();

        //sprawdzenie, czy nie usuwa chociażby jednego elementu listy
        verify(spyGarlicList, never()).get(anyInt());
        verify(spyGarlicList, never()).clear();
        //sprawdzenie, czy nadal lista zawiera swój jeden element
        assertEquals(1, spyGarlicList.size(), "Lista czosnków nie powinna się zmienić, gdy człowiek jest osłabiony");

    }

    @Test
    void testEatGarlicWhenWeakeningIsFalse(){
        trainedHuman.weakend=false;

        trainedHuman.energyLevel=500;
        Garlic garlicMock=mock(Garlic.class);
        List<Garlic> garlicList=new ArrayList<>();
        garlicList.add(garlicMock);
        when(cell.getGarlics()).thenReturn(garlicList);
        doAnswer(invocationOnMock -> {
            garlicList.remove(garlicMock);
            return null;
        }).when(simulation).removeGarlic(garlicMock);

        trainedHuman.interact();

        verify(simulation, description("Czosnek powinien być usunięty z symulacji")).removeGarlic(garlicMock);
        assertEquals(800, trainedHuman.energyLevel, "Energia człowieka powinna być zwiększona o jego energyBoost");
        assertTrue(trainedHuman.isSafe(), "Po zjedzeniu czosnku człowiek powinien mieć ochronę");
        assertEquals(3, trainedHuman.stepsToReset, "Licznik ma wynosić FINAL_OF_RESET (3)");
    }

    @Test
    void testTryRemoveWhenEnergyIsAboveZero() {
        trainedHuman.energyLevel = 300;

        boolean result = trainedHuman.tryRemove();

        assertFalse(result, "tryRemove powinno zwrócić false, jeśli energia > 0");
        verify(simulation, never()).removeAgent(any());
    }

    @Test
    void testTryRemoveWhenEnergyEqualsZero() {
        trainedHuman.energyLevel = 0;

        boolean result = trainedHuman.tryRemove();

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


    @Test
    void testInteract_ShouldCollectGarlic_WhenStandingNextToContainer() {
        // 1. GIVEN: Człowiek ma pusty zapas czosnku i szuka kontenera
        trainedHuman.garlicStock = 0;
        trainedHuman.movement = trainedHuman.collectMoving; // strategia CollectMovingStrategy

        int[] containerCoords = {trainedHuman.getX()+1, trainedHuman.getX()+2, trainedHuman.getX()+1, trainedHuman.getX()+2};
        when(simulation.getCoordinatesOfContainer()).thenReturn(containerCoords);

        Cell containerCellMock = mock(Cell.class);
        when(containerCellMock.getX()).thenReturn(trainedHuman.getX()+1);
        when(containerCellMock.getY()).thenReturn(trainedHuman.getX()+1);

        when(board.getWidth()).thenReturn(10);
        when(board.getHeight()).thenReturn(10);
        when(board.getClosestCellContainingGarlicContainer(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(containerCellMock);


        when(board.getCell(anyInt(), anyInt())).thenReturn(cell);

        trainedHuman.interact();

        assertEquals(trainedHuman.garlicStockMax, trainedHuman.garlicStock,
                "Zapas czosnku powinien zostać uzupełniony do wartości maksymalnej");

    }
}
