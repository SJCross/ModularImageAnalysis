package wbif.sjx.ModularImageAnalysis.Module.ObjectProcessing.Identification;

import wbif.sjx.ModularImageAnalysis.Module.ModuleTest;

import static org.junit.Assert.*;

public class ExtractObjectEdgesTest extends ModuleTest {

    @Override
    public void testGetTitle() {
        assertNotNull(new ExtractObjectEdges().getTitle());
    }

    @Override
    public void testGetHelp() {
        assertNotNull(new ExtractObjectEdges().getHelp());
    }
}