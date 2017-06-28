package wbif.sjx.ModularImageAnalysis.Module.ObjectMeasurements;

import ij.ImagePlus;
import wbif.sjx.ModularImageAnalysis.Module.HCModule;
import wbif.sjx.ModularImageAnalysis.Object.*;
import wbif.sjx.common.MathFunc.CumStat;

import java.util.ArrayList;

/**
 * Created by sc13967 on 05/05/2017.
 */
public class MeasureObjectIntensity extends HCModule {
    public static final String INPUT_OBJECTS = "Input objects";
    public static final String INPUT_IMAGE = "Input image";


    @Override
    public String getTitle() {
        return "Measure object intensity";

    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public void execute(HCWorkspace workspace, boolean verbose) {
        String moduleName = this.getClass().getSimpleName();
        if (verbose) System.out.println("["+moduleName+"] Initialising");

        // Getting input objects
        String objectName = parameters.getValue(INPUT_OBJECTS);
        HCObjectSet objects = workspace.getObjects().get(objectName);

        // Getting input image
        String imageName = parameters.getValue(INPUT_IMAGE);
        HCImage image = workspace.getImages().get(imageName);
        ImagePlus ipl = image.getImagePlus();

        // Measuring intensity for each object and adding the measurement to that object
        for (HCObject object:objects.values()) {
            // Initialising the cumulative statistics object to store pixel intensities
            CumStat cs = new CumStat();

            // Getting pixel coordinates
            ArrayList<Integer> x = object.getCoordinates(HCObject.X);
            ArrayList<Integer> y = object.getCoordinates(HCObject.Y);
            ArrayList<Integer> c = object.getCoordinates(HCObject.C);
            ArrayList<Integer> z = object.getCoordinates(HCObject.Z);
            ArrayList<Integer> t = object.getCoordinates(HCObject.T);

            // Running through all pixels in this object and adding the intensity to the MultiCumStat object
            for (int i=0;i<x.size();i++) {
                int cPos = c==null ? 0 : c.get(i);
                int zPos = z==null ? 0 : z.get(i);
                int tPos = t==null ? 0 : t.get(i);

                ipl.setPosition(cPos+1,zPos+1,tPos+1);
                cs.addMeasure(ipl.getProcessor().getPixelValue(x.get(i),y.get(i)));

            }

            // Calculating mean, std, min and max intensity
            HCMeasurement meanIntensity = new HCMeasurement(imageName+"_MEAN", cs.getMean());
            meanIntensity.setSource(this);
            object.addMeasurement(meanIntensity);

            HCMeasurement stdIntensity = new HCMeasurement(imageName+"_STD", cs.getStd(CumStat.SAMPLE));
            stdIntensity.setSource(this);
            object.addMeasurement(stdIntensity);

            HCMeasurement minIntensity = new HCMeasurement(imageName+"_MIN", cs.getMin());
            minIntensity.setSource(this);
            object.addMeasurement(minIntensity);

            HCMeasurement maxIntensity = new HCMeasurement(imageName+"_MAX", cs.getMax());
            maxIntensity.setSource(this);
            object.addMeasurement(maxIntensity);

        }

        if (verbose) System.out.println("["+moduleName+"] Complete");

    }

    @Override
    public void initialiseParameters() {
        parameters.addParameter(new HCParameter(INPUT_OBJECTS, HCParameter.INPUT_OBJECTS,null));
        parameters.addParameter(new HCParameter(INPUT_IMAGE, HCParameter.INPUT_IMAGE,null));

    }

    @Override
    public HCParameterCollection getActiveParameters() {
        HCParameterCollection returnedParameters = new HCParameterCollection();
        returnedParameters.addParameter(parameters.getParameter(INPUT_IMAGE));
        returnedParameters.addParameter(parameters.getParameter(INPUT_OBJECTS));

        return returnedParameters;

    }

    @Override
    public void addMeasurements(HCMeasurementCollection measurements) {
        String inputImageName = parameters.getValue(INPUT_IMAGE);
        measurements.addMeasurement(parameters.getValue(INPUT_OBJECTS),inputImageName+"_MEAN");
        measurements.addMeasurement(parameters.getValue(INPUT_OBJECTS),inputImageName+"_STD");
        measurements.addMeasurement(parameters.getValue(INPUT_OBJECTS),inputImageName+"_MIN");
        measurements.addMeasurement(parameters.getValue(INPUT_OBJECTS),inputImageName+"_MAX");

    }

    @Override
    public void addRelationships(HCRelationshipCollection relationships) {

    }
}
