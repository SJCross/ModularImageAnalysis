package wbif.sjx.MIA.Module.ImageProcessing.Pixel;

import emblcmci.BleachCorrection_ExpoFit;
import emblcmci.BleachCorrection_MH;
import emblcmci.BleachCorrection_SimpleRatio;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.Duplicator;
import wbif.sjx.MIA.MIA;
import wbif.sjx.MIA.Module.Categories;
import wbif.sjx.MIA.Module.Category;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Object.Image;
import wbif.sjx.MIA.Object.Obj;
import wbif.sjx.MIA.Object.ObjCollection;
import wbif.sjx.MIA.Object.Status;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.Parameters.BooleanP;
import wbif.sjx.MIA.Object.Parameters.ChoiceP;
import wbif.sjx.MIA.Object.Parameters.InputImageP;
import wbif.sjx.MIA.Object.Parameters.InputObjectsP;
import wbif.sjx.MIA.Object.Parameters.OutputImageP;
import wbif.sjx.MIA.Object.Parameters.ParameterCollection;
import wbif.sjx.MIA.Object.Parameters.SeparatorP;
import wbif.sjx.MIA.Object.References.Collections.ImageMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.MetadataRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ObjMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ParentChildRefCollection;
import wbif.sjx.MIA.Object.References.Collections.PartnerRefCollection;

/**
 * Created by sc13967 on 30/11/2017.
 */
public class BleachingCorrection extends Module {
    public static final String INPUT_SEPARATOR = "Image input/output";
    public static final String INPUT_IMAGE = "Input image";
    public static final String APPLY_TO_INPUT = "Apply to input image";
    public static final String OUTPUT_IMAGE = "Output image";

    public static final String CORRECTION_SEPARATOR = "Correction controls";
    public static final String CORRECTION_MODE = "Correction mode";
    public static final String USE_ROI_OBJECTS = "Use ROI objects";
    public static final String ROI_OBJECTS = "ROI objects";

    public interface CorrectionModes {
        public String EXPONENTIAL_FIT = "Exponential fit";
        public String HISTOGRAM_MATCHING = "Histogram matching";
        public String SIMPLE_RATIO = "Simple ratio";

        public String[] ALL = new String[] { EXPONENTIAL_FIT, HISTOGRAM_MATCHING, SIMPLE_RATIO };

    }

    public BleachingCorrection(ModuleCollection modules) {
        super("Bleaching correction", modules);
    }

    @Override
    public Category getCategory() {
        return Categories.IMAGE_PROCESSING_PIXEL;
    }

    @Override
    public String getDescription() {
        return "Apply bleaching correction to a specified image.  This adjusts intensities in all frames (after the first) to match the histogram distribution of the first frame.  It is intended to account for any fluorophore bleaching that occurs during acquisition of a timecourse.<br><br>This macro runs the Fiji bleaching correction plugin, \"<a href=\"https://imagej.net/Bleach_Correction\">Bleach Correction</a>\".";
    }

    @Override
    public Status process(Workspace workspace) {
        // Getting input image
        String inputImageName = parameters.getValue(INPUT_IMAGE);
        Image inputImage = workspace.getImages().get(inputImageName);
        ImagePlus inputImagePlus = inputImage.getImagePlus();

        // Getting parameters
        boolean applyToInput = parameters.getValue(APPLY_TO_INPUT);
        String outputImageName = parameters.getValue(OUTPUT_IMAGE);
        String correctionMode = parameters.getValue(CORRECTION_MODE);
        boolean useRoiObjects = parameters.getValue(USE_ROI_OBJECTS);
        String roiObjectsName = parameters.getValue(ROI_OBJECTS);

        // If applying to a new image, the input image is duplicated
        if (!applyToInput)
            inputImagePlus = new Duplicator().run(inputImagePlus);

        // Although histogram matching has a ROI-compatible constructor the ROI is
        // unused
        if (correctionMode.equals(CorrectionModes.HISTOGRAM_MATCHING))
            useRoiObjects = false;

        Roi roi = null;
        if (useRoiObjects) {
            ObjCollection roiObjects = workspace.getObjectSet(roiObjectsName);
            Obj roiObject = roiObjects.getAsSingleObject();
            roiObject.setCoordinateSet(roiObject.getProjected().getCoordinateSet());
            roi = roiObject.getRoi(0);
        }

        switch (correctionMode) {
            case CorrectionModes.EXPONENTIAL_FIT:
                try {
                    new BleachCorrection_ExpoFit(inputImagePlus, roi).core();
                } catch (NullPointerException e) {
                    MIA.log.writeWarning("Bleach correction failed (possible lack of exponential decay in signal)");
                }
                break;
            case CorrectionModes.HISTOGRAM_MATCHING:
                new BleachCorrection_MH(inputImagePlus).doCorrection();
                break;
            case CorrectionModes.SIMPLE_RATIO:
                inputImagePlus = new BleachCorrection_SimpleRatio(inputImagePlus, roi).correctBleach();
                break;
        }

        // If the image is being saved as a new image, adding it to the workspace
        if (!applyToInput) {
            Image outputImage = new Image(outputImageName, inputImagePlus);
            workspace.addImage(outputImage);
            if (showOutput)
                outputImage.showImage();

        } else {
            if (showOutput)
                inputImage.showImage();

        }

        return Status.PASS;

    }

    @Override
    protected void initialiseParameters() {
        parameters.add(new SeparatorP(INPUT_SEPARATOR, this));
        parameters.add(new InputImageP(INPUT_IMAGE, this));
        parameters.add(new BooleanP(APPLY_TO_INPUT, this, true));
        parameters.add(new OutputImageP(OUTPUT_IMAGE, this));

        parameters.add(new SeparatorP(CORRECTION_SEPARATOR, this));
        parameters.add(new ChoiceP(CORRECTION_MODE, this, CorrectionModes.HISTOGRAM_MATCHING, CorrectionModes.ALL));
        parameters.add(new BooleanP(USE_ROI_OBJECTS, this, false));
        parameters.add(new InputObjectsP(ROI_OBJECTS, this));

        addParameterDescriptions();

    }

    @Override
    public ParameterCollection updateAndGetParameters() {
        ParameterCollection returnedParameters = new ParameterCollection();

        returnedParameters.add(parameters.getParameter(INPUT_SEPARATOR));
        returnedParameters.add(parameters.getParameter(INPUT_IMAGE));
        returnedParameters.add(parameters.getParameter(APPLY_TO_INPUT));
        if (!(boolean) parameters.getValue(APPLY_TO_INPUT)) {
            returnedParameters.add(parameters.getParameter(OUTPUT_IMAGE));
        }

        returnedParameters.add(parameters.getParameter(CORRECTION_SEPARATOR));
        returnedParameters.add(parameters.getParameter(CORRECTION_MODE));
        switch ((String) parameters.getValue(CORRECTION_MODE)) {
            case CorrectionModes.EXPONENTIAL_FIT:
            case CorrectionModes.SIMPLE_RATIO:
                returnedParameters.add(parameters.getParameter(USE_ROI_OBJECTS));
                if ((boolean) parameters.getValue(USE_ROI_OBJECTS))
                    returnedParameters.add(parameters.getParameter(ROI_OBJECTS));
                break;
        }

        return returnedParameters;

    }

    @Override
    public ImageMeasurementRefCollection updateAndGetImageMeasurementRefs() {
        return null;
    }

    @Override
    public ObjMeasurementRefCollection updateAndGetObjectMeasurementRefs() {
        return null;
    }

    @Override
    public MetadataRefCollection updateAndGetMetadataReferences() {
        return null;
    }

    @Override
    public ParentChildRefCollection updateAndGetParentChildRefs() {
        return null;
    }

    @Override
    public PartnerRefCollection updateAndGetPartnerRefs() {
        return null;
    }

    @Override
    public boolean verify() {
        return true;
    }

    void addParameterDescriptions() {
        parameters.get(INPUT_IMAGE).setDescription("Image from workspace to apply bleaching correction process to.");

        parameters.get(APPLY_TO_INPUT).setDescription(
                "When selected, the post-operation image will overwrite the input image in the workspace.  Otherwise, the image will be saved to the workspace with the name specified by the \""
                        + OUTPUT_IMAGE + "\" parameter.");

        parameters.get(OUTPUT_IMAGE).setDescription("If \"" + APPLY_TO_INPUT
                + "\" is not selected, the post-operation image will be saved to the workspace with this name.");

        parameters.get(CORRECTION_MODE).setDescription("Controls the bleach correction algorithm to use:<br><ul>" +

                "<li>\"" + CorrectionModes.EXPONENTIAL_FIT
                + "\" Assumes the bleaching process is controlled by a mono-exponential decay.  Will fail if the signal does not decay over time.  Calculation can be performed using a single ROI for all frames.</li>"
                +

                "<li>\"" + CorrectionModes.HISTOGRAM_MATCHING
                + "\" Adjusts image intensities so that the histograms match that from the first frame.</li>" +

                "<li>\"" + CorrectionModes.SIMPLE_RATIO
                + "\" Normalises images to have the same mean intensity.  Calculation can be performed using a single ROI for all frames.</li></ul>");

        parameters.get(USE_ROI_OBJECTS).setDescription(
                "When selected, the bleaching and associated intensity correction will be calculated based on the pixels within a region of interest (specified as the objects of collection \""
                        + ROI_OBJECTS
                        + "\").  A single ROI is used for all frames (i.e. the region can't be different from frame to frame).");
                
        parameters.get(ROI_OBJECTS).setDescription(
                "If \""+USE_ROI_OBJECTS+"\" is selected, this is the object collection which will act as the region of interest for calculating the bleaching.  Since only a single ROI can be used, all objects in this collection are reduced down into a single frame and timepoint.");
                        
    }
}
