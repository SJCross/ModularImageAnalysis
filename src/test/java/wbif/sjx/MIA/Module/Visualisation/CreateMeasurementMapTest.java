package wbif.sjx.MIA.Module.Visualisation;

import wbif.sjx.MIA.Module.ModuleTest;

import static org.junit.jupiter.api.Assertions.*;

public class CreateMeasurementMapTest extends ModuleTest {

    @Override
    public void testGetHelp() {
        assertNotNull(new CreateMeasurementMap(null).getDescription());
    }
}