package wbif.sjx.MIA.Module.ObjectMeasurements.Miscellaneous;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import wbif.sjx.MIA.Module.Categories;
import wbif.sjx.MIA.Module.Category;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Object.Measurement;
import wbif.sjx.MIA.Object.Obj;
import wbif.sjx.MIA.Object.ObjCollection;
import wbif.sjx.MIA.Object.Status;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.Parameters.BooleanP;
import wbif.sjx.MIA.Object.Parameters.FilePathP;
import wbif.sjx.MIA.Object.Parameters.InputObjectsP;
import wbif.sjx.MIA.Object.Parameters.ParameterCollection;
import wbif.sjx.MIA.Object.Parameters.SeparatorP;
import wbif.sjx.MIA.Object.References.ObjMeasurementRef;
import wbif.sjx.MIA.Object.References.Collections.ImageMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.MetadataRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ObjMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ParentChildRefCollection;
import wbif.sjx.MIA.Object.References.Collections.PartnerRefCollection;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

public class ApplyWekaObjectClassification extends Module {
    public static final String INPUT_SEPARATOR = "Objects input";
    public static final String INPUT_OBJECTS = "Input objects";

    public static final String CLASSIFIER_SEPARATOR = "Classifier controls";
    public static final String CLASSIFIER_PATH = "Classifier path";
    public static final String APPLY_NORMALISATION = "Apply normalisation";

    public ApplyWekaObjectClassification(ModuleCollection modules) {
        super("Apply Weka object classification", modules);
    }

    @Override
    public Category getCategory() {
        return Categories.OBJECT_MEASUREMENTS_MISCELLANEOUS;
    }

    @Override
    public String getDescription() {
        return "Apply a previously-prepared WEKA object classifier to a specified object collection from the workspace.  Classification can be based on a range of measurements associated with the input objects.  All measurements used to create this model should be present in the input objects and have the same names (i.e. measurement names shouldn't be changed during preparation of training data).<br><br>" +
        
        "The probability of each input object belonging to each class is output as a measurement associated with that object.  Each object also has a class index (based on the order the classes are listed in the .model file) indicating the most probable class that object belongs to.";
    }

    public static String getProbabilityMeasurementName(String className) {
        return "CLASSIFIER // " + className + "_PROB";
    }

    public static String getClassMeasurementName(Instances instances) {
        StringBuilder sb = new StringBuilder("CLASSIFIER // CLASS (");
        for (int i = 0; i < instances.numClasses(); i++) {
            if (i != 0)
                sb.append(",");
            sb.append(instances.classAttribute().value(i));
        }

        sb.append(")");
 
        return sb.toString();

    }

    public static void addProbabilityMeasurements(Obj inputObject, Instances instances, double[] classification) {
        for (int i = 0; i < instances.numClasses(); i++) {
            String measName = getProbabilityMeasurementName(instances.classAttribute().value(i));
            Measurement measurement = new Measurement(measName, classification[i]);
            inputObject.addMeasurement(measurement);
        }
    }

    @Override
    public Status process(Workspace workspace) {
        // Getting input objects
        String inputObjectsName = parameters.getValue(INPUT_OBJECTS);
        ObjCollection inputObjects = workspace.getObjects().get(inputObjectsName);

        // Getting other parameters
        String classifierPath = parameters.getValue(CLASSIFIER_PATH);
        boolean applyNormalisation = parameters.getValue(APPLY_NORMALISATION);

        AbstractClassifier abstractClassifier = null;
        Instances instances = null;

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(classifierPath));
            abstractClassifier = (AbstractClassifier) objectInputStream.readObject();
            instances = (Instances) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Status.FAIL;
        }

        // Getting list of measurement names
        ArrayList<String> measurementNames = new ArrayList<>();
        for (int i = 0; i < instances.numAttributes(); i++) {
            String measName = instances.attribute(i).name();
            measName = measName.replace("ï»¿", "");
            measurementNames.add(measName);
        }

        // Adding object instances
        ArrayList<Obj> processedObjects = new ArrayList<>();
        for (Obj inputObject : inputObjects.values()) {
            double[] objAttr = new double[measurementNames.size() - 1];
            for (int i = 0; i < measurementNames.size() - 1; i++) {
                Measurement measurement = inputObject.getMeasurement(measurementNames.get(i));

                // Objects with missing measurements will cause problems for normalisation
                if (measurement == null || Double.isNaN(measurement.getValue()))
                    continue;

                objAttr[i] = measurement.getValue();
            }
        
            processedObjects.add(inputObject);
            instances.add(new SparseInstance(1, objAttr));
        }

        try {
            // Applying normalisation
            if (applyNormalisation) {
                Normalize normalize = new Normalize();
                normalize.setInputFormat(instances);
                instances = Filter.useFilter(instances, normalize);
            }

            // Applying classifications
            double[][] classifications = abstractClassifier.distributionsForInstances(instances);
            
            String classMeasName = getClassMeasurementName(instances);
            for (int i = 0; i < processedObjects.size(); i++) {
                Obj inputObject = processedObjects.get(i);
                double[] classification = classifications[i];
                addProbabilityMeasurements(inputObject, instances, classification);

                int classIndex = (int) abstractClassifier.classifyInstance(instances.get(i));
                inputObject.addMeasurement(new Measurement(classMeasName, classIndex));
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Status.FAIL;
        }

        if (showOutput)
            inputObjects.showMeasurements(this, modules);

        return Status.PASS;

    }

    @Override
    protected void initialiseParameters() {
        parameters.add(new SeparatorP(INPUT_SEPARATOR, this));
        parameters.add(new InputObjectsP(INPUT_OBJECTS, this));

        parameters.add(new SeparatorP(CLASSIFIER_SEPARATOR, this));
        parameters.add(new FilePathP(CLASSIFIER_PATH, this));
        parameters.add(new BooleanP(APPLY_NORMALISATION, this, true));

        addParameterDescriptions();
        
    }

    @Override
    public ParameterCollection updateAndGetParameters() {
        return parameters;
    }

    @Override
    public ImageMeasurementRefCollection updateAndGetImageMeasurementRefs() {
        return null;
    }

    @Override
    public ObjMeasurementRefCollection updateAndGetObjectMeasurementRefs() {
        String inputObjectsName = parameters.getValue(INPUT_OBJECTS);

        try {
            // Getting class names
            String classifierPath = parameters.getValue(CLASSIFIER_PATH);
            if (!new File(classifierPath).exists())
                return null;
                
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(classifierPath));
            AbstractClassifier abstractClassifier = (AbstractClassifier) objectInputStream.readObject();
            Instances instances = (Instances) objectInputStream.readObject();
            objectInputStream.close();

            ObjMeasurementRefCollection returnedRefs = new ObjMeasurementRefCollection();

            for (int i = 0; i < instances.numClasses(); i++) {
                String className = instances.classAttribute().value(i);                
                ObjMeasurementRef ref = objectMeasurementRefs.getOrPut(getProbabilityMeasurementName(className));
                ref.setObjectsName(inputObjectsName);
                returnedRefs.add(ref);
            }

            ObjMeasurementRef ref = objectMeasurementRefs.getOrPut(getClassMeasurementName(instances));
            ref.setObjectsName(inputObjectsName);
            returnedRefs.add(ref);

            return returnedRefs;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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
        parameters.get(INPUT_OBJECTS).setDescription("Input objects from workspace which will be classified based on model specified by \""+CLASSIFIER_PATH+"\" parameter.");

        parameters.get(CLASSIFIER_PATH).setDescription("WEKA model (.model extension) that will be used to classify input objects based on a variety of measurements.  This model must be created in the <a href=\"https://www.cs.waikato.ac.nz/ml/index.html\">WEKA software</a>.  All measurements used to create this model should be present in the input objects and have the same names (i.e. measurement names shouldn't be changed during preparation of training data).");

        parameters.get(APPLY_NORMALISATION).setDescription("When selected, measurements will be normalised (set to the range 0-1) within their respective classes.");

    }
}
