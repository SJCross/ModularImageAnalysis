package wbif.sjx.MIA.Module.Miscellaneous.Macros;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.plugin.Duplicator;
import ij.process.ImageProcessor;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Module.Category;
import wbif.sjx.MIA.Module.Categories;
import wbif.sjx.MIA.Object.Image;
import wbif.sjx.MIA.Object.Status;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.Parameters.BooleanP;
import wbif.sjx.MIA.Object.Parameters.InputImageP;
import wbif.sjx.MIA.Object.Parameters.OutputImageP;
import wbif.sjx.MIA.Object.Parameters.SeparatorP;
import wbif.sjx.MIA.Object.Parameters.ParameterCollection;
import wbif.sjx.MIA.Object.Parameters.Text.StringP;
import wbif.sjx.MIA.Object.References.Collections.ImageMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.MetadataRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ObjMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ParentChildRefCollection;
import wbif.sjx.MIA.Object.References.Collections.PartnerRefCollection;

/**
 * Created by sc13967 on 31/01/2018.
 */
public class RunSingleCommand extends Module {
    public static final String INPUT_SEPARATOR = "Image input/output";
    public static final String INPUT_IMAGE = "Input image";
    public static final String APPLY_TO_INPUT = "Apply to input image";
    public static final String OUTPUT_IMAGE = "Output image";
    public static final String COMMAND_SEPARATOR = "Command controls";
    public static final String COMMAND = "Command";
    public static final String ARGUMENTS = "Parameters";
    public static final String EXECUTION_SEPARATOR = "Execution controls";
    public static final String ENABLE_MULTITHREADING = "Enable multithreading";

    public RunSingleCommand(ModuleCollection modules) {
        super("Run single command", modules);
    }


    @Override
    public Category getCategory() {
        return Categories.MISCELLANEOUS_MACROS;
    }

    @Override
    public String getDescription() {
        return "Run a single command on an image from the workspace.   This module only runs commands of the format \"run([COMMAND], [ARGUMENTS])\".  For example, the command \"run(\"Subtract Background...\", \"rolling=50 stack\");\" would be specified with the \""+COMMAND+"\" parameter set to \"Subtract Background...\" and the \""+ARGUMENTS+"\" parameter set to \"rolling=50 stack\".  For more advanced macro processing please use the \""+new RunMacro(null).getName()+"\" module.";
    }

    public void runCommandMultithreaded(ImagePlus inputImagePlus, String commandTitle, String arguments) {
        // Setting up multithreading
        int nThreads = Prefs.getThreads();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());

        // Applying the command
        ImageStack ist = inputImagePlus.getStack();
        for (int i = 0; i < ist.size(); i++) {
            ImageProcessor ipr = ist.getProcessor(i + 1);
            ImagePlus ipl = new ImagePlus("Temp", ipr);
            Runnable task = () -> {
                IJ.run(ipl, commandTitle, arguments);
            };
            pool.submit(task);
        }

        pool.shutdown();
        try {
            pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inputImagePlus.updateChannelAndDraw();
        
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
        String commandTitle = parameters.getValue(COMMAND);
        String arguments = parameters.getValue(ARGUMENTS);
        boolean multithread = parameters.getValue(ENABLE_MULTITHREADING);

        // Only multithread the operation if it's being conducted on a single slice at a time.
        if (!arguments.contains("stack"))
            multithread = false;

        // If applying to a new image, the input image is duplicated
        if (!applyToInput) {
            inputImagePlus = new Duplicator().run(inputImagePlus);
        }

        if (multithread) {
            // If multithreading, remove the "stack" argument
            arguments = arguments.replace("stack","");
            runCommandMultithreaded(inputImagePlus, commandTitle, arguments);
        } else {
            IJ.run(inputImagePlus, commandTitle, arguments);
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
        parameters.add(new SeparatorP(COMMAND_SEPARATOR, this));
        parameters.add(new StringP(COMMAND, this));
        parameters.add(new StringP(ARGUMENTS, this));
        parameters.add(new SeparatorP(EXECUTION_SEPARATOR, this));
        parameters.add(new BooleanP(ENABLE_MULTITHREADING, this, true));

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

        returnedParameters.add(parameters.getParameter(COMMAND_SEPARATOR));
        returnedParameters.add(parameters.getParameter(COMMAND));
        returnedParameters.add(parameters.getParameter(ARGUMENTS));

            returnedParameters.add(parameters.getParameter(EXECUTION_SEPARATOR));
            returnedParameters.add(parameters.getParameter(ENABLE_MULTITHREADING));
        

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
      parameters.get(INPUT_IMAGE).setDescription("Image from workspace to apply command to.  This image is duplicated prior to application of the command, so won't be updated by default.  To store any changes back onto this image, select the \""+APPLY_TO_INPUT+"\" parameter.");

      parameters.get(APPLY_TO_INPUT).setDescription("When selected, the image returned by the command will be stored back into the MIA workspace at the same name as the input image.  This will update the input image.");

      parameters.get(OUTPUT_IMAGE).setDescription("When \""+APPLY_TO_INPUT+"\" is not selected this will store the command output image into the MIA workspace with the name specified by this parameter.");

      parameters.get(COMMAND).setDescription("The command command to run.  This must be the exact name as given by the ImageJ command recorder.  Note: Only commands of the format \"run([MACRO TITLE], [ARGUMENTS])\" can be run by this module.  For more advanced command processing please use the \""+new RunMacro(null).getName()+"\" module.");

      parameters.get(ARGUMENTS).setDescription("The options to pass to the command.");

      parameters.get(ENABLE_MULTITHREADING).setDescription("When running a command which operates on a single slice at a time, multithreading will create a new thread for each slice.  This can provide a speed improvement when working on a computer with a multi-core CPU.  Note: Multithreading is only available for commands containing the \"stack\" argument.");

    }
}
