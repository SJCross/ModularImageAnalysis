package wbif.sjx.MIA.Module.ObjectProcessing.Refinement;

import wbif.sjx.MIA.Module.ModuleTest;

import static org.junit.Assert.*;

public class ExpandShrinkObjectsTest extends ModuleTest {

    @Override
    public void testGetHelp() {
        assertNotNull(new ExpandShrinkObjects(null).getHelp());
    }
}