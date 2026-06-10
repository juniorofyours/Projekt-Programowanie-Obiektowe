package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CellTest {

    Cell cell;

    @BeforeEach
    void setUp() {
        cell = new Cell(3,4);
    }

    @Test
    void testObjectCannotStepOnCellWithGarlicContainer(){
        GarlicContainerCell garlicContainerCellMock=mock(GarlicContainerCell.class);
        cell.addGarlicContainerCell(garlicContainerCellMock);

        Agent humanMock=mock(Human.class);
        Garlic garlicMock=mock(Garlic.class);

        assertThrows(UnusableCellException.class,()->{ cell.addAgent(humanMock);}, "Metoda addAgent powinna wyrzucić wyjątek," +
                "gdy jest nieużywalna");
        assertThrows(UnusableCellException.class,()->{ cell.addGarlic(garlicMock);}, "Metoda addGarlic powinna wyrzucić wyjątek," +
                "gdy jest nieużywalna");
    }

    @Test
    void testGetFirstObject(){
        assertNull(cell.getFirstObject(), "Metoda powinna zwracać null, gdy komórka nie ma żadnego obiektu");

        GarlicContainerCell garlicContainerCellMock=mock(GarlicContainerCell.class);
        cell.addGarlicContainerCell(garlicContainerCellMock);

        assertSame(garlicContainerCellMock, cell.getFirstObject(), "Metoda powinna zwracać garlicContainerCell");
        cell.removeGarlicContainerCell();

        Garlic garlicMock=mock(Garlic.class);
        cell.addGarlic(garlicMock);

        assertSame(garlicMock, cell.getFirstObject(), "Metoda powinna zwracać czosnek, gdy komórka posiada czosnek, ale nie posiada agentów");

        Agent humanMockOne=mock(Agent.class);
        Agent humanMockTwo=mock(Agent.class);
        cell.addAgent(humanMockOne);
        cell.addAgent(humanMockTwo);

        assertSame(humanMockOne, cell.getFirstObject(), "Metoda powinna zwracać pierwszego agenta, gdy komórka posiada agentów");
    }

}
