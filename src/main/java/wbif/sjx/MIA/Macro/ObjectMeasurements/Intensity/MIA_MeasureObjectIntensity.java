package wbif.sjx.MIA.Macro.ObjectMeasurements.Intensity;

import ij.macro.MacroExtension;
import wbif.sjx.MIA.Macro.MacroOperation;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Module.ObjectMeasurements.Intensity.MeasureObjectIntensity;
import wbif.sjx.MIA.Object.Workspace;

public class MIA_MeasureObjectIntensity extends MacroOperation {
    public MIA_MeasureObjectIntensity(MacroExtension theHandler) {
        super(theHandler);
    }

    @Override
    public int[] getArgumentTypes() {
        return new int[]{ARG_STRING,ARG_STRING,ARG_NUMBER,ARG_NUMBER};

    }

    @Override
    public String action(Object[] objects, Workspace workspace, ModuleCollection modules) {
        MeasureObjectIntensity measureObjectIntensity = new MeasureObjectIntensity(modules);

        measureObjectIntensity.updateParameterValue(MeasureObjectIntensity.INPUT_OBJECTS,objects[0]);
        measureObjectIntensity.updateParameterValue(MeasureObjectIntensity.INPUT_IMAGE,objects[1]);
        measureObjectIntensity.updateParameterValue(MeasureObjectIntensity.MEASURE_WEIGHTED_CENTRE,(double) objects[2] == 1);
        measureObjectIntensity.setShowOutput((double) objects[3] ==1);

        measureObjectIntensity.process(workspace);

        return null;
    }

    @Override
    public String getArgumentsDescription() {
        return "String objectsName, String imageName, boolean measureWeightedCentre, boolean showResults";
    }

    @Override
    public String getDescription() {
        return "Measure intensity for specified objects.";
    }

}
