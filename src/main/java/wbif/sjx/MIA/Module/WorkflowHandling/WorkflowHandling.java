package wbif.sjx.MIA.Module.WorkflowHandling;

import java.io.File;
import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.formats.FormatException;
import wbif.sjx.MIA.MIA;
import wbif.sjx.MIA.GUI.Colours;
import wbif.sjx.MIA.Module.Categories;
import wbif.sjx.MIA.Module.Category;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Module.InputOutput.ImageLoader;
import wbif.sjx.MIA.Module.ObjectProcessing.Refinement.FilterObjects.AbstractNumericObjectFilter;
import wbif.sjx.MIA.Object.Image;
import wbif.sjx.MIA.Object.Measurement;
import wbif.sjx.MIA.Object.ObjCollection;
import wbif.sjx.MIA.Object.Status;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.Parameters.BooleanP;
import wbif.sjx.MIA.Object.Parameters.ChoiceP;
import wbif.sjx.MIA.Object.Parameters.ImageMeasurementP;
import wbif.sjx.MIA.Object.Parameters.InputImageP;
import wbif.sjx.MIA.Object.Parameters.InputObjectsP;
import wbif.sjx.MIA.Object.Parameters.MetadataItemP;
import wbif.sjx.MIA.Object.Parameters.ModuleP;
import wbif.sjx.MIA.Object.Parameters.ParameterCollection;
import wbif.sjx.MIA.Object.Parameters.SeparatorP;
import wbif.sjx.MIA.Object.Parameters.Text.DoubleP;
import wbif.sjx.MIA.Object.Parameters.Text.MessageP;
import wbif.sjx.MIA.Object.Parameters.Text.StringP;
import wbif.sjx.MIA.Object.References.Collections.ImageMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.MetadataRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ObjMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ParentChildRefCollection;
import wbif.sjx.MIA.Object.References.Collections.PartnerRefCollection;
import wbif.sjx.common.MetadataExtractors.Metadata;

/**
 * Created by Stephen Cross on 23/11/2018.
 */
public class WorkflowHandling extends Module {
    public static final String CONDITION_SEPARATOR = "Condition";
    public static final String TEST_MODE = "Test mode";
    public static final String INPUT_IMAGE = "Input image";
    public static final String INPUT_OBJECTS = "Input objects";
    public static final String NUMERIC_FILTER_MODE = "Numeric filter mode";
    public static final String TEXT_FILTER_MODE = "Text filter mode";
    public static final String REFERENCE_IMAGE_MEASUREMENT = "Reference image measurement";
    public static final String REFERENCE_OBJECT_COUNT_MODE = "Reference object count mode";
    public static final String REFERENCE_METADATA_VALUE = "Reference metadata value";
    public static final String REFERENCE_NUMERIC_VALUE = "Reference numeric value";
    public static final String REFERENCE_TEXT_VALUE = "Reference text value";
    public static final String FIXED_VALUE = "Fixed value";
    public static final String GENERIC_FORMAT = "Generic format";
    public static final String AVAILABLE_METADATA_FIELDS = "Available metadata fields";

    public static final String RESULT_SEPARATOR = "Result";
    public static final String CONTINUATION_MODE = "Continuation mode";
    public static final String REDIRECT_MODULE = "Redirect module";
    public static final String SHOW_REDIRECT_MESSAGE = "Show redirect message";
    public static final String REDIRECT_MESSAGE = "Redirect message";
    public static final String EXPORT_WORKSPACE = "Export terminated workspaces";
    public static final String REMOVE_OBJECTS = "Remove objects from workspace";
    public static final String REMOVE_IMAGES = "Remove images from workspace";

    public WorkflowHandling(ModuleCollection modules) {
        super("Workflow handling", modules);
    }

    public interface TestModes {
        String IMAGE_MEASUREMENT = "Image measurement";
        String METADATA_NUMERIC_VALUE = "Metadata numeric value";
        String METADATA_TEXT_VALUE = "Metadata text value";
        String FILE_EXISTS = "File exists";
        String FILE_DOES_NOT_EXIST = "File doesn't exist";
        String FIXED_VALUE = "Fixed value";
        String OBJECT_COUNT = "Object count";

        String[] ALL = new String[] { FILE_DOES_NOT_EXIST, FILE_EXISTS, FIXED_VALUE, IMAGE_MEASUREMENT,
                METADATA_NUMERIC_VALUE, METADATA_TEXT_VALUE, OBJECT_COUNT };

    }

    public interface NumericFilterModes extends AbstractNumericObjectFilter.FilterMethods {
    }

    public interface TextFilterModes {
        String EQUAL_TO = "Equal to";
        String NOT_EQUAL_TO = "Not equal to";

        String[] ALL = new String[] { EQUAL_TO, NOT_EQUAL_TO };

    }

    public interface ContinuationModes {
        String REDIRECT_TO_MODULE = "Redirect to module";
        String TERMINATE = "Terminate";

        String[] ALL = new String[] { REDIRECT_TO_MODULE, TERMINATE };

    }

    Status processTermination(ParameterCollection params) {
        return processTermination(params, null, false);

    }

    Status processTermination(ParameterCollection parameters, Workspace workspace, boolean showRedirectMessage) {
        String continuationMode = parameters.getValue(CONTINUATION_MODE);
        String redirectMessage = parameters.getValue(REDIRECT_MESSAGE);
        boolean exportWorkspace = parameters.getValue(EXPORT_WORKSPACE);
        boolean removeImages = parameters.getValue(REMOVE_IMAGES);
        boolean removeObjects = parameters.getValue(REMOVE_OBJECTS);

        // If terminate, remove necessary images and objects
        switch (continuationMode) {
            case ContinuationModes.REDIRECT_TO_MODULE:
                redirectModule = parameters.getValue(REDIRECT_MODULE);
                if (showRedirectMessage)
                    MIA.log.writeMessage(workspace.getMetadata().insertMetadataValues(redirectMessage));
                return Status.REDIRECT;
            case ContinuationModes.TERMINATE:
                workspace.setExportWorkspace(exportWorkspace);
                if (removeImages)
                    workspace.clearAllImages(false);
                if (removeObjects)
                    workspace.clearAllObjects(false);
                return Status.TERMINATE;
        }

        return Status.PASS;

    }

    public static boolean testFileExists(Metadata metadata, String genericFormat) {
        String name = "";
        try {
            name = ImageLoader.getGenericName(metadata, genericFormat);
        } catch (ServiceException | DependencyException | FormatException | IOException e) {
            return false;
        }

        // If no name matching the format was found
        if (name == null)
            return false;

        return (new File(name)).exists();

    }

    public static boolean testImageMeasurement(Image inputImage, String measurementName, String referenceMode,
            double referenceValue) {
        // Getting the measurement
        Measurement measurement = inputImage.getMeasurement(measurementName);

        // If this measurement doesn't exist, fail the test
        if (measurement == null)
            return false;

        // Testing the value
        double measurementValue = measurement.getValue();
        return AbstractNumericObjectFilter.testFilter(measurementValue, referenceValue, referenceMode);

    }

    public static boolean testNumericMetadata(String metadataValue, String referenceMode, double referenceValue) {
        try {
            double testValue = Double.parseDouble(metadataValue);
            return AbstractNumericObjectFilter.testFilter(testValue, referenceValue, referenceMode);

        } catch (NumberFormatException e) {
            // This will be thrown if the value specified wasn't a number
            MIA.log.writeWarning("Specified metadata value for filtering (" + metadataValue
                    + ") wasn't a number.  Terminating this run.");
            return false;

        }
    }

    public static boolean testTextMetadata(String metadataValue, String referenceMode, String referenceValue) {
        switch (referenceMode) {
            case TextFilterModes.EQUAL_TO:
                return metadataValue.equals(referenceValue);
            case TextFilterModes.NOT_EQUAL_TO:
                return !metadataValue.equals(referenceValue);
        }

        return false;

    }

    public static boolean testObjectCount(ObjCollection inputObjects, String referenceMode, double referenceValue) {
        int testValue = 0;
        if (inputObjects != null)
            testValue = inputObjects.size();

        return AbstractNumericObjectFilter.testFilter(testValue, referenceValue, referenceMode);

    }


    @Override
    public Category getCategory() {
        return Categories.WORKFLOW_HANDLING;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    protected Status process(Workspace workspace) {
        // Getting parameters
        String testMode = parameters.getValue(TEST_MODE);
        String inputImageName = parameters.getValue(INPUT_IMAGE);
        String inputObjectsName = parameters.getValue(INPUT_OBJECTS);
        String numericFilterMode = parameters.getValue(NUMERIC_FILTER_MODE);
        String textFilterMode = parameters.getValue(TEXT_FILTER_MODE);
        String referenceImageMeasurement = parameters.getValue(REFERENCE_IMAGE_MEASUREMENT);
        String referenceMetadataValue = parameters.getValue(REFERENCE_METADATA_VALUE);
        double referenceValueNumber = parameters.getValue(REFERENCE_NUMERIC_VALUE);
        String referenceValueText = parameters.getValue(REFERENCE_TEXT_VALUE);
        double fixedValueNumber = parameters.getValue(FIXED_VALUE);
        String genericFormat = parameters.getValue(GENERIC_FORMAT);
        boolean showRedirectMessage = parameters.getValue(SHOW_REDIRECT_MESSAGE);

        // Running relevant tests
        boolean terminate = false;
        switch (testMode) {
            case TestModes.FILE_DOES_NOT_EXIST:
                terminate = !testFileExists(workspace.getMetadata(), genericFormat);
                break;
            case TestModes.FILE_EXISTS:
                terminate = testFileExists(workspace.getMetadata(), genericFormat);
                break;
            case TestModes.FIXED_VALUE:
                terminate = fixedValueNumber == referenceValueNumber;
                break;
            case TestModes.IMAGE_MEASUREMENT:
                Image inputImage = workspace.getImage(inputImageName);
                terminate = testImageMeasurement(inputImage, referenceImageMeasurement, numericFilterMode,
                        referenceValueNumber);
                break;
            case TestModes.METADATA_NUMERIC_VALUE:
                String metadataValue = workspace.getMetadata().get(referenceMetadataValue).toString();
                terminate = testNumericMetadata(metadataValue, numericFilterMode, referenceValueNumber);
                break;
            case TestModes.METADATA_TEXT_VALUE:
                metadataValue = workspace.getMetadata().get(referenceMetadataValue).toString();
                terminate = testTextMetadata(metadataValue, textFilterMode, referenceValueText);
                break;
            case TestModes.OBJECT_COUNT:
                ObjCollection inputObjects = workspace.getObjectSet(inputObjectsName);
                terminate = testObjectCount(inputObjects, numericFilterMode, referenceValueNumber);
                break;
        }

        if (terminate)
            return processTermination(parameters, workspace, showRedirectMessage);

        return Status.PASS;

    }

    @Override
    protected void initialiseParameters() {
        parameters.add(new SeparatorP(CONDITION_SEPARATOR, this));
        parameters.add(new ChoiceP(TEST_MODE, this, TestModes.IMAGE_MEASUREMENT, TestModes.ALL));

        parameters.add(new InputImageP(INPUT_IMAGE, this));
        parameters.add(new InputObjectsP(INPUT_OBJECTS, this));
        parameters.add(new ChoiceP(NUMERIC_FILTER_MODE, this, NumericFilterModes.LESS_THAN, NumericFilterModes.ALL));
        parameters.add(new ChoiceP(TEXT_FILTER_MODE, this, TextFilterModes.EQUAL_TO, TextFilterModes.ALL));
        parameters.add(new ImageMeasurementP(REFERENCE_IMAGE_MEASUREMENT, this));
        parameters.add(new MetadataItemP(REFERENCE_METADATA_VALUE, this));
        parameters.add(new DoubleP(REFERENCE_NUMERIC_VALUE, this, 0d));
        parameters.add(new StringP(REFERENCE_TEXT_VALUE, this));
        parameters.add(new DoubleP(FIXED_VALUE, this, 0d));
        parameters.add(new StringP(GENERIC_FORMAT, this));
        parameters.add(new MessageP(AVAILABLE_METADATA_FIELDS, this, Colours.ORANGE, 170));

        parameters.add(new SeparatorP(RESULT_SEPARATOR, this));
        parameters.add(new ChoiceP(CONTINUATION_MODE, this, ContinuationModes.TERMINATE, ContinuationModes.ALL));
        parameters.add(new ModuleP(REDIRECT_MODULE, this, true));
        parameters.add(new BooleanP(SHOW_REDIRECT_MESSAGE, this, false));
        parameters.add(new StringP(REDIRECT_MESSAGE, this, ""));
        parameters.add(new BooleanP(EXPORT_WORKSPACE, this, true));
        parameters.add(new BooleanP(REMOVE_IMAGES, this, false));
        parameters.add(new BooleanP(REMOVE_OBJECTS, this, false));

        addParameterDescriptions();

    }

    @Override
    public ParameterCollection updateAndGetParameters() {
        ParameterCollection returnedParameters = new ParameterCollection();

        returnedParameters.add(parameters.getParameter(CONDITION_SEPARATOR));
        returnedParameters.add(parameters.getParameter(TEST_MODE));
        switch ((String) parameters.getValue(TEST_MODE)) {
            case TestModes.FILE_EXISTS:
            case TestModes.FILE_DOES_NOT_EXIST:
                returnedParameters.add(parameters.getParameter(GENERIC_FORMAT));
                returnedParameters.add(parameters.getParameter(AVAILABLE_METADATA_FIELDS));
                MetadataRefCollection metadataRefs = modules.getMetadataRefs(this);
                parameters.getParameter(AVAILABLE_METADATA_FIELDS).setValue(metadataRefs.getMetadataValues());
                break;

            case TestModes.FIXED_VALUE:
                returnedParameters.add(parameters.getParameter(FIXED_VALUE));
                returnedParameters.add(parameters.getParameter(REFERENCE_NUMERIC_VALUE));
                break;

            case TestModes.IMAGE_MEASUREMENT:
                returnedParameters.add(parameters.getParameter(INPUT_IMAGE));
                returnedParameters.add(parameters.getParameter(NUMERIC_FILTER_MODE));
                returnedParameters.add(parameters.getParameter(REFERENCE_IMAGE_MEASUREMENT));
                returnedParameters.add(parameters.getParameter(REFERENCE_NUMERIC_VALUE));

                String inputImageName = parameters.getValue(INPUT_IMAGE);
                ImageMeasurementP parameter = parameters.getParameter(REFERENCE_IMAGE_MEASUREMENT);
                parameter.setImageName(inputImageName);
                break;

            case TestModes.METADATA_NUMERIC_VALUE:
                returnedParameters.add(parameters.getParameter(NUMERIC_FILTER_MODE));
                returnedParameters.add(parameters.getParameter(REFERENCE_METADATA_VALUE));
                returnedParameters.add(parameters.getParameter(REFERENCE_NUMERIC_VALUE));
                break;

            case TestModes.METADATA_TEXT_VALUE:
                returnedParameters.add(parameters.getParameter(TEXT_FILTER_MODE));
                returnedParameters.add(parameters.getParameter(REFERENCE_METADATA_VALUE));
                returnedParameters.add(parameters.getParameter(REFERENCE_TEXT_VALUE));
                break;

            case TestModes.OBJECT_COUNT:
                returnedParameters.add(parameters.getParameter(INPUT_OBJECTS));
                returnedParameters.add(parameters.getParameter(NUMERIC_FILTER_MODE));
                returnedParameters.add(parameters.getParameter(REFERENCE_NUMERIC_VALUE));
                break;
        }

        returnedParameters.add(parameters.getParameter(RESULT_SEPARATOR));
        returnedParameters.add(parameters.getParameter(CONTINUATION_MODE));
        switch ((String) parameters.getValue(CONTINUATION_MODE)) {
            case ContinuationModes.REDIRECT_TO_MODULE:
                returnedParameters.add(parameters.getParameter(REDIRECT_MODULE));
                redirectModule = parameters.getValue(REDIRECT_MODULE);
                returnedParameters.add(parameters.getParameter(SHOW_REDIRECT_MESSAGE));
                if ((boolean) parameters.getValue(SHOW_REDIRECT_MESSAGE)) {
                    returnedParameters.add(parameters.getParameter(REDIRECT_MESSAGE));
                }
                break;
            case ContinuationModes.TERMINATE:
                returnedParameters.add(parameters.getParameter(EXPORT_WORKSPACE));
                returnedParameters.add(parameters.getParameter(REMOVE_IMAGES));
                returnedParameters.add(parameters.getParameter(REMOVE_OBJECTS));
                redirectModule = null;
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

        parameters.get(TEST_MODE).setDescription("Controls what condition is being tested:<br><ul>"

                + "<li>\"" + TestModes.IMAGE_MEASUREMENT + "\" Numeric filter against a measurement (specified by \""
                + REFERENCE_IMAGE_MEASUREMENT + "\") associated with an image from the workspace (specified by \""
                + INPUT_IMAGE + "\").</li>"

                + "<li>\"" + TestModes.METADATA_NUMERIC_VALUE
                + "\" Numeric filter against a metadata value (specified by \"" + REFERENCE_METADATA_VALUE
                + "\") associated with the workspace.  Metadata values are stored as text, but this filter will attempt to parse any numeric values as numbers.  Text comparison can be done using \""
                + TestModes.METADATA_TEXT_VALUE + "\" mode.</li>"

                + "<li>\"" + TestModes.METADATA_TEXT_VALUE + "\" Text filter against a metadata value (specified by \""
                + REFERENCE_METADATA_VALUE
                + "\") associated with the workspace.  This filter compares for exact text matches to a reference, specified by \""
                + REFERENCE_TEXT_VALUE + "\"</li>"

                + "<li>\"" + TestModes.FILE_EXISTS
                + "\" Checks if a specified file exists on the accessible computer filesystem.</li>"

                + "<li>\"" + TestModes.FILE_DOES_NOT_EXIST
                + "\" Checks if a specified file doesn't exist on the accessible computer filesystem.</li>"

                + "<li>\"" + TestModes.FIXED_VALUE + "\" Numeric filter against a fixed value.</li>"

                + "<li>\"" + TestModes.OBJECT_COUNT
                + "\" Numeric filter against the number of objects contained in an object collection stored in the workspace (specified by \""
                + INPUT_OBJECTS + "\").</li></ul>");

        parameters.get(INPUT_IMAGE).setDescription("");

        parameters.get(INPUT_OBJECTS).setDescription("");

        parameters.get(NUMERIC_FILTER_MODE).setDescription("");

        parameters.get(REFERENCE_IMAGE_MEASUREMENT).setDescription("");

        parameters.get(REFERENCE_METADATA_VALUE).setDescription("");

        parameters.get(REFERENCE_NUMERIC_VALUE).setDescription("");

        parameters.get(REFERENCE_TEXT_VALUE).setDescription("");

        parameters.get(FIXED_VALUE).setDescription("");

        parameters.get(GENERIC_FORMAT).setDescription(
                "Format for a generic filename.  Plain text can be mixed with global variables or metadata values currently stored in the workspace.  Global variables are specified using the \"V{name}\" notation, where \"name\" is the name of the variable to insert.  Similarly, metadata values are specified with the \"M{name}\" notation.");

                parameters.get(AVAILABLE_METADATA_FIELDS).setDescription(
                        "List of the currently-available metadata values for this workspace.  These can be used when compiling a generic filename.");
                
        parameters.get(CONTINUATION_MODE)
                .setDescription("Controls what happens if the termination/redirection condition is met:<br><ul>"

                        + "<li>\"" + ContinuationModes.REDIRECT_TO_MODULE
                        + "\" The analysis workflow will skip to the module specified by the \"" + REDIRECT_MODULE
                        + "\" parameter.  Any modules between the present module and the target module will not be evaluated.</li>"

                        + "<li>\"" + ContinuationModes.TERMINATE
                        + "\"The analysis will stop evaluating any further modules.</li></ul>");

        parameters.get(REDIRECT_MODULE).setDescription(
                "If the condition is met, the workflow will redirect to this module.  In doing so, it will skip evaluation of any modules between the present module and this module.");

        parameters.get(SHOW_REDIRECT_MESSAGE)
                .setDescription("Controls if a message should be displayed in the log if redirection occurs.");

        parameters.get(REDIRECT_MESSAGE).setDescription("Message to display if redirection occurs.");

        parameters.get(EXPORT_WORKSPACE).setDescription(
                "Controls if the workspace should still be exported to the output Excel spreadsheet if termination occurs.");

        parameters.get(REMOVE_IMAGES).setDescription(
                "Controls if images should be completely removed from the workspace along with any associated measurements if termination occurs.");

        parameters.get(REMOVE_OBJECTS).setDescription(
                "Controls if objects should be completely removed from the workspace along with any associated measurements if termination occurs.");

    }
}
