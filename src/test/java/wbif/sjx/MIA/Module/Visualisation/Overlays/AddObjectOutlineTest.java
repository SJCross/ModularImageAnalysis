package wbif.sjx.MIA.Module.Visualisation.Overlays;

import wbif.sjx.MIA.Module.ModuleTest;

import static org.junit.jupiter.api.Assertions.*;

public class AddObjectOutlineTest extends ModuleTest {

    @Override
    public void testGetHelp() {
        assertNotNull(new AddObjectOutline(null).getDescription());
    }
}