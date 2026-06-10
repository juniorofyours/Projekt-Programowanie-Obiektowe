package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HumanTest {
    Simulation simulationMock;
    Board boardMock;
    Cell cellMock;
    Human human;

    @BeforeEach
    void setup(){
        simulationMock=mock(Simulation.class);
        boardMock=mock(Board.class);
        cellMock=mock(Cell.class);
        when(boardMock.getCell(0, 0)).thenReturn(cellMock);
        human=new Human(simulationMock, boardMock, 0, 0, 0.1f, 0.1f, 300, 300);
    }

    @Test
    void testEatGarlic(){
        human.energyLevel=500;
        Garlic garlicMock=mock(Garlic.class);
        List<Garlic> garlicList=new ArrayList<>();
        garlicList.add(garlicMock);
        when(cellMock.getGarlics()).thenReturn(garlicList);
        doAnswer(invocationOnMock -> {
            garlicList.remove(garlicMock);
            return null;
        }).when(simulationMock).removeGarlic(garlicMock);

        human.interact();

        verify(simulationMock, description("Czosnek powinien być usunięty z symulacji")).removeGarlic(garlicMock);
        assertEquals(800, human.energyLevel, "Energia człowieka powinna być zwiększona o jego energyBoost");
    }
}
