package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SimulationClockTest {
    SimulationConfig config=SimulationConfig.getInstance();
    SimulationClock clock=SimulationClock.getInstance();

    @Test
    void testKeepAnHourWithinTheRange(){
        config.getWorldConfig().setCycling(true);
        for(int i=0; i<24;i++){
            clock.updateClock();
            assertTrue(clock.getHour()>=0&&clock.getHour()<24);
        }
    }
}
