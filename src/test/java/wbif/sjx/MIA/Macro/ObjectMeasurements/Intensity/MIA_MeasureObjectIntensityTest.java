package wbif.sjx.MIA.Macro.ObjectMeasurements.Intensity;

import wbif.sjx.MIA.Macro.MacroOperationTest;

import static org.junit.jupiter.api.Assertions.*;

public class MIA_MeasureObjectIntensityTest extends MacroOperationTest {
    @Override
    public void testGetName() {
        assertNotNull(new MIA_MeasureObjectIntensity(null).getName());
    }

    @Override
    public void testGetArgumentsDescription() {
        assertNotNull(new MIA_MeasureObjectIntensity(null).getArgumentsDescription());
    }

    @Override
    public void testGetDescription() {
        assertNotNull(new MIA_MeasureObjectIntensity(null).getDescription());
    }
}