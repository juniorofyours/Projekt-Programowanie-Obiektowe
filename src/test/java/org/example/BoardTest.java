package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardTest {
    private Board board;
    @BeforeEach
    void setUp() {
        board = new Board(10, 10);
    }

    @Test
    void testBoardFillsGridWithCorrectCells() {
        Cell cell = board.getCell(5, 5);
        assertNotNull(cell, "Komórka planszy nie powinna być null");
        assertAll(
                ()->assertEquals(5, cell.getX()),
                ()->assertEquals(5, cell.getY())
        );
    }

    @Test
    void testBoardManagesAgentsProperly(){
//        Dodawanie agenta do planszy
        Agent vampireMock=mock(Vampire.class);
        when(vampireMock.getX()).thenReturn(2);
        when(vampireMock.getY()).thenReturn(3);
        board.addToBoard(vampireMock);
        Cell cellOne=board.getCell(2,3);
        assertTrue(cellOne.getAgents().contains(vampireMock), "Komórka powinna zawierać dodanego do planszy agenta");

//        Aktualizowanie agenta na planszy
        board.updateAgent(vampireMock, 3, 4);
        when(vampireMock.getX()).thenReturn(3);
        when(vampireMock.getY()).thenReturn(4);
        Cell cellTwo=board.getCell(3,4);
        assertFalse(cellOne.getAgents().contains(vampireMock), "Stara komórka nie powinna zawierać zaktualizowanego agenta");
        assertTrue(cellTwo.getAgents().contains(vampireMock), "Nowa komórka powinna zawierać zaktualizowanego agenta");

//        Zastępowanie agenta innym agentem
        Agent humanMock=mock(Human.class);
        when(humanMock.getX()).thenReturn(3);
        when(humanMock.getY()).thenReturn(4);
        board.replaceAgentInCell(vampireMock,humanMock);
        assertFalse(cellTwo.getAgents().contains(vampireMock), "Komórka nie powinna zwierać starego agenta");
        assertTrue(cellTwo.getAgents().contains(humanMock), "Komórka powinna zwierać nowego agenta");

//        usuwanie agenta z planszy
        board.removeFromBoard(humanMock);
        assertFalse(cellTwo.getAgents().contains(humanMock), "Komórka nie powinna zawierać agenta usuniętego z planszy");
    }

    @Test
    void testDistanceTorusCalculatesShortestDistance() {
        assertEquals(3, Board.distanceTorus(2, 5, 10), "Najkrótszy dystans powinien wynieść 3 (przez planszę");

        assertEquals(2, Board.distanceTorus(1, 9, 10), "Najkrótszy dystans powinien wynieść 2 (przez krawędź");
    }

    @Test
    void testGetClosestCellContainingGarlicContainer() {
//        Współrzędne min i max kontenera
        int minX = 0, maxX = 2;
        int minY = 0, maxY = 2;

//        Współrzędne agenta
        int agentX = 9;
        int agentY = 1;

        Cell closestCell = board.getClosestCellContainingGarlicContainer(agentX, agentY, minX, maxX, minY, maxY);

//        Sprawdzenie czy wybrano najbliższą komórkę kontenera uwzględniając torus
        assertAll(
                () -> assertEquals(0, closestCell.getX(), "Najbliższy x kontenera przez krawędź to 0"),
                () -> assertEquals(1, closestCell.getY(), "Najbliższy y kontenera to 1")
        );
    }
}