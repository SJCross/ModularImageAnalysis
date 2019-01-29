package wbif.sjx.ModularImageAnalysis.Module.ImageProcessing.Stack;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.SubHyperstackMaker;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import wbif.sjx.ModularImageAnalysis.Module.Module;
import wbif.sjx.ModularImageAnalysis.Module.PackageNames;
import wbif.sjx.ModularImageAnalysis.Object.*;
import wbif.sjx.ModularImageAnalysis.Object.Parameters.*;
import wbif.sjx.ModularImageAnalysis.ThirdParty.Stack_Focuser_;

import com.drew.lang.annotations.Nullable;

public class FocusStack extends Module {
    public static final String INPUT_IMAGE = "Input image";
    public static final String OUTPUT_FOCUSED_IMAGE = "Output focused image";
    public static final String USE_EXISTING_HEIGHT_IMAGE = "Use existing height image";
    public static final String INPUT_HEIGHT_IMAGE = "Input height image";
    public static final String RANGE = "Range";
    public static final String SMOOTH_HEIGHT_MAP = "Smooth height map";
    public static final String ADD_HEIGHT_MAP_TO_WORKSPACE = "Add height map image to workspace";
    public static final String OUTPUT_HEIGHT_IMAGE = "Output height image";
    public static final String SHOW_HEIGHT_IMAGE = "Show height image";


    public Image[] focusStack(Image inputImage, String outputImageName, int range, boolean smooth, @Nullable String outputHeightImageName, @Nullable Image inputHeightImage) {
        ImagePlus inputIpl = inputImage.getImagePlus();
        ImageProcessor ipr = inputIpl.getProcessor();

        // Creating array to hold [0] the focused image and [1] the height map
        Image[] images = new Image[2];
        ImagePlus outputIpl = createEmptyImage(inputIpl,outputImageName,inputIpl.getBitDepth());
        outputIpl.setCalibration(inputIpl.getCalibration());
        images[0] = new Image(outputImageName,outputIpl);

        // If necessary, creating the height image
        ImagePlus heightIpl = null;
        if (outputHeightImageName != null) {
            heightIpl = createEmptyImage(inputIpl,outputHeightImageName,16);
            heightIpl.setCalibration(inputIpl.getCalibration());
            images[1] = new Image(outputHeightImageName,heightIpl);
        }

        // Getting the image type
        int type = getImageType(ipr);

        // Initialising the stack focuser.  This requires an example stack.
        int nSlices = inputIpl.getNSlices();
        ImagePlus stack = SubHyperstackMaker.makeSubhyperstack(inputIpl,"1-1","1-"+nSlices,"1-1");
        Stack_Focuser_ focuser = new Stack_Focuser_();

        focuser.setup("ksize="+range+" hmap="+0+" rgbone="+1+" smooth="+smooth,stack);

        // Iterating over all timepoints and channels
        int nStacks = inputIpl.getNChannels()*inputIpl.getNFrames();
        int count = 0;
        for (int c=1;c<=inputIpl.getNChannels();c++) {
            for (int t=1;t<=inputIpl.getNFrames();t++) {
                stack = SubHyperstackMaker.makeSubhyperstack(inputIpl,c+"-"+c,1+"-"+nSlices,t+"-"+t);

                // If using an existing map, adding that now
                if (inputHeightImage != null) {
                    inputHeightImage.getImagePlus().setPosition(c,1,t);
                    focuser.setExistingHeightMap(inputHeightImage.getImagePlus().getProcessor());
                }

                // Adding the focused image to the output image stack
                ImageProcessor focusedIpr = focuser.focusGreyStack(stack.getStack(),type);
                outputIpl.setPosition(c,1,t);
                outputIpl.setProcessor(focusedIpr);

                // If necessary, adding the height image
                if (outputHeightImageName != null) {
                    ImageProcessor heightIpr = focuser.getHeightImage();
                    heightIpl.setPosition(c, 1, t);
                    heightIpl.setProcessor(heightIpr);
                }

                writeMessage("Processed "+(++count)+" of "+nStacks+" stacks");

            }
        }

        outputIpl.setPosition(1,1,1);
        outputIpl.updateChannelAndDraw();

        if (outputHeightImageName != null) {
            heightIpl.setPosition(1, 1, 1);
            heightIpl.updateChannelAndDraw();
        }

        return images;

    }

    public int getImageType(ImageProcessor ipr) {
        // Determining the type of image
        int type = Stack_Focuser_.RGB;
        if (ipr instanceof ByteProcessor) {
            type = Stack_Focuser_.BYTE;
        } else if (ipr instanceof ShortProcessor) {
            type = Stack_Focuser_.SHORT;
        } else if (ipr instanceof FloatProcessor) {
            type = Stack_Focuser_.FLOAT;
        }

        return type;

    }

    public ImagePlus createEmptyImage(ImagePlus inputImagePlus, String outputImageName, int bitDepth) {
        // Getting image dimensions
        int width = inputImagePlus.getWidth();
        int height = inputImagePlus.getHeight();
        int nChannels = inputImagePlus.getNChannels();
        int nSlices = inputImagePlus.getNSlices();
        int nFrames = inputImagePlus.getNFrames();

        return IJ.createHyperStack(outputImageName,width,height,nChannels,1,nFrames,bitDepth);

    }


    @Override
    public String getTitle() {
        return "Focus stack";
    }

    @Override
    public String getPackageName() {
        return PackageNames.IMAGE_PROCESSING_STACK;
    }

    @Override
    public String getHelp() {
        return "Channels will be focused individually.\n" +
                "Uses the StackFocuser plugin created by Mikhail Umorin.\n" +
                "Source downloaded from https://imagej.nih.gov/ij/plugins/download/Stack_Focuser_.java on 06-June-2018";
    }

    @Override
    protected boolean run(Workspace workspace) {
        // Getting input image
        String inputImageName = parameters.getValue(INPUT_IMAGE);
        Image inputImage = workspace.getImage(inputImageName);

        // Getting parameters
        String outputFocusedImageName = parameters.getValue(OUTPUT_FOCUSED_IMAGE);
        boolean useExisting = parameters.getValue(USE_EXISTING_HEIGHT_IMAGE);
        String inputHeightImageName = parameters.getValue(INPUT_HEIGHT_IMAGE);
        Image inputHeightImage = null;
        int range = parameters.getValue(RANGE);
        boolean smooth = parameters.getValue(SMOOTH_HEIGHT_MAP);
        boolean addHeightMap = parameters.getValue(ADD_HEIGHT_MAP_TO_WORKSPACE);
        String outputHeightImageName = parameters.getValue(OUTPUT_HEIGHT_IMAGE);

        // Updating parameters if an existing image was to be used
        if (useExisting) {
            inputHeightImage = workspace.getImage(inputHeightImageName);
            range = 0;
            addHeightMap = false;
        }

        if (!addHeightMap) outputHeightImageName = null;

        // Running stack focusing
        Image[] outputImages = focusStack(inputImage,outputFocusedImageName,range,smooth,outputHeightImageName,inputHeightImage);

        // Adding output image to Workspace
        workspace.addImage(outputImages[0]);
        if (showOutput) showImage(outputImages[0]);

        // If necessary, processing the height image
        if (addHeightMap) {
            if (parameters.getValue(SHOW_HEIGHT_IMAGE)) {
                showImage(outputImages[1]);
            }

            // Adding output image to Workspace
            workspace.addImage(outputImages[1]);

        }

        return true;

    }

    @Override
    protected void initialiseParameters() {
        parameters.add(new InputImageP(INPUT_IMAGE,this));
        parameters.add(new OutputImageP(OUTPUT_FOCUSED_IMAGE,this));
        parameters.add(new BooleanP(USE_EXISTING_HEIGHT_IMAGE,this,false));
        parameters.add(new InputImageP(INPUT_HEIGHT_IMAGE,this));
        parameters.add(new IntegerP(RANGE,this,11));
        parameters.add(new BooleanP(SMOOTH_HEIGHT_MAP,this,true));
        parameters.add(new BooleanP(ADD_HEIGHT_MAP_TO_WORKSPACE,this,false));
        parameters.add(new OutputImageP(OUTPUT_HEIGHT_IMAGE,this));
        parameters.add(new BooleanP(SHOW_HEIGHT_IMAGE,this,false));

    }

    @Override
    public ParameterCollection updateAndGetParameters() {
        ParameterCollection returnedParameters = new ParameterCollection();

        returnedParameters.add(parameters.getParameter(INPUT_IMAGE));
        returnedParameters.add(parameters.getParameter(OUTPUT_FOCUSED_IMAGE));

        returnedParameters.add(parameters.getParameter(USE_EXISTING_HEIGHT_IMAGE));
        if (parameters.getValue(USE_EXISTING_HEIGHT_IMAGE)) {
            returnedParameters.add(parameters.getParameter(INPUT_HEIGHT_IMAGE));

        } else {
            returnedParameters.add(parameters.getParameter(RANGE));
            returnedParameters.add(parameters.getParameter(SMOOTH_HEIGHT_MAP));

            returnedParameters.add(parameters.getParameter(ADD_HEIGHT_MAP_TO_WORKSPACE));
            if (parameters.getValue(ADD_HEIGHT_MAP_TO_WORKSPACE)) {
                returnedParameters.add(parameters.getParameter(OUTPUT_HEIGHT_IMAGE));
                returnedParameters.add(parameters.getParameter(SHOW_HEIGHT_IMAGE));
            }
        }

        return returnedParameters;

    }

    @Override
    public MeasurementRefCollection updateAndGetImageMeasurementRefs() {
        return null;
    }

    @Override
    public MeasurementRefCollection updateAndGetObjectMeasurementRefs() {
        return null;
    }

    @Override
    public MetadataRefCollection updateAndGetMetadataReferences() {
        return null;
    }

    @Override
    public void addRelationships(RelationshipCollection relationships) {

    }
}
