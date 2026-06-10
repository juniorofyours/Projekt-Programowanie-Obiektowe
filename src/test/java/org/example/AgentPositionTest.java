package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AgentPositionTest {
    Board boardMock;
    Agent agentMock;
    AgentPosition position;
//    Random rand=new Random(42);
    @BeforeEach
    void setup(){
        boardMock=mock(Board.class);
        when(boardMock.getWidth()).thenReturn(20);
        when(boardMock.getHeight()).thenReturn(20);
//        when(boardMock.getCell(0,0)).thenReturn(cellMock);
        agentMock=mock(Vampire.class);
        position=new AgentPosition(agentMock, boardMock, 19, 19);
//        position.rand=rand;
    }

    @Test
    void testDoNotRandomizeWhenCellsArentUsable(){
        Cell cellMock=mock(Cell.class);
        when(cellMock.isUsable()).thenReturn(false);
        when(boardMock.getCell(anyInt(), anyInt())).thenAnswer(invocationOnMock -> cellMock);

        assertFalse(position.randomize(), "Pozycja agenta nie powinna byc wylosowana, gdy dostępne komórki są nieużywalne");
    }

    @Test
    void testRandomizeWhenCellsAreUsable(){
        Cell cellMock=mock(Cell.class);
        when(cellMock.isUsable()).thenReturn(true);
        when(boardMock.getCell(anyInt(), anyInt())).thenAnswer(invocationOnMock -> cellMock);

        assertTrue(position.randomize(), "Pozycja agenta powinna byc wylosowana, gdy dostępne komórki są używalne");
    }

    @Test
    void updateAgentPositionWhenMove(){
        position.move(1,1);

        assertEquals(0, position.x, "Agent powinien mieć nowy x w obrębie planszy");
        assertEquals(0, position.y, "Agent powinien mieć nowy y w obrębie planszy");

        verify(boardMock, description("Agent powinien być przeniesiony po planszy")).updateAgent(agentMock, 0, 0);

    }
}
