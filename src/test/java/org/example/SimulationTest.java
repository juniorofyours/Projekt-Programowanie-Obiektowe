package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulationTest {
    private Board boardMock;
    private Simulation simulation;
    private SimulationConfig config=SimulationConfig.getInstance();

    @BeforeEach
    void setUp() {
        boardMock = mock(Board.class);
        simulation = new Simulation();
        simulation.setBoard(boardMock);
    }

    @Test
    void testInitAddsAgentsAndGarlicContainerCorrectly(){
        config.getHumanConfig().setInitialNumber(10);
        config.getTrainedHumanConfig().setInitialNumber(10);
        config.getVampireConfig().setInitialNumber(10);

        simulation.init();

        assertEquals(30, simulation.getAgents().size(), "Lista agentów stworzonego środowiska symulacji powinna zawierać wszystkich agentów" +
                "określonych w konfiguracji");

        assertNotNull(simulation.getContainer(), "Kontener stworzonego środowiska symulacji nie powinien być null");
    }

    @Test
    void testReplaceAgentUpdatesAgentListAndBoard() {
        Human humanMock = mock(Human.class);
        Human humanMockTwo = mock(Human.class);
        Vampire vampireMock = mock(Vampire.class);

        simulation.addAgent(humanMock);
        simulation.addAgent(humanMockTwo);

        simulation.replaceAgent(humanMock, vampireMock);

        assertEquals(1, simulation.getNumberOfHumanBeings(), "Stary agent powinien być usunięty z listy agentów");
        assertEquals(1, simulation.getNumberOfVampires(), "Nowy agent powinien zostać dodany do listy agentów");

        assertSame(vampireMock, simulation.getAgents().get(0), "Nowy agent powinien zostać umieszczony w liście" +
                "w miejscu starego agenta");

        verify(boardMock, description("Symulacja powinna zastąpić starego agenta nowym agentem na planszy"))
                .replaceAgentInCell(humanMock, vampireMock);
    }

    @Test
    void testCountersIncrementAndDecrementCorrectly() {
        Human humanMock1 = mock(Human.class);
        Human humanMock2 = mock(Human.class);

        simulation.addAgent(humanMock1);
        simulation.addAgent(humanMock2);
        assertEquals(2, simulation.getNumberOfHumanBeings());

        simulation.removeAgent(humanMock1);

        assertEquals(1, simulation.getNumberOfHumanBeings(), "Licznik ludzi powinien zmaleć po usunięciu agenta");
    }

    @Test
    void testAddAndRemoveGarlicInteractsWithBoard() {
        Garlic garlicMock = mock(Garlic.class);

        simulation.addGarlic(garlicMock);

        verify(boardMock, description("Czosnek powinien zostać dodany do planszy")).addGarlicToBoard(garlicMock);

        simulation.removeGarlic(garlicMock);

        verify(boardMock, description("Czosnek powinien zostać usunięty planszy")).removeGarlicFromBoard(garlicMock);
    }

    @Test
    void testGetCoordinatesOfContainerReturnsCorrectArray() {
        GarlicContainer container = new GarlicContainer(2, 5, 2, 6);

        simulation.setContainer(container);

        int[] coords = simulation.getCoordinatesOfContainer();

        assertEquals(4, coords.length, "Tablica powinna zawierać dokładnie 4 elementy");

        assertAll(
                () -> assertEquals(2, coords[0], "minimalny x powinien wynosić 2"),
                () -> assertEquals(5, coords[1], "maksymalny x powinien wynosić 5"),
                () -> assertEquals(2, coords[2], "minimalny y powinien wynosić 2"),
                () -> assertEquals(6, coords[3], "maksymalny y powinien wynosić 6")
        );
    }
}
