package wbif.sjx.MIA.Module.ObjectMeasurements.Spatial;

import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Module.Category;
import wbif.sjx.MIA.Module.Categories;
import wbif.sjx.MIA.Object.Status;
import wbif.sjx.MIA.Object.Measurement;
import wbif.sjx.MIA.Object.Obj;
import wbif.sjx.MIA.Object.ObjCollection;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.Parameters.BooleanP;
import wbif.sjx.MIA.Object.Parameters.InputObjectsP;
import wbif.sjx.MIA.Object.Parameters.ParameterCollection;
import wbif.sjx.MIA.Object.Parameters.SeparatorP;
import wbif.sjx.MIA.Object.References.ObjMeasurementRef;
import wbif.sjx.MIA.Object.References.Collections.ImageMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.MetadataRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ObjMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ParentChildRefCollection;
import wbif.sjx.MIA.Object.References.Collections.PartnerRefCollection;
import wbif.sjx.common.Object.Volume.Volume;

/**
 * Created by sc13967 on 07/02/2018.
 */
public class MeasureObjectOverlap extends Module {
    public final static String INPUT_SEPARATOR = "Object input";
    public final static String OBJECT_SET_1 = "Object set 1";
    public final static String OBJECT_SET_2 = "Object set 2";
    public final static String LINK_IN_SAME_FRAME = "Only link objects in same frame";

    public MeasureObjectOverlap(ModuleCollection modules) {
        super("Measure object overlap",modules);
    }


    public interface Measurements {
        String OVERLAP_VOX_1 = "OVERLAP_VOXELS_1";
        String OVERLAP_VOL_PX_1 = "OVERLAP_VOLUME_(PX³)_1";
        String OVERLAP_VOL_CAL_1 = "OVERLAP_VOLUME_(${SCAL}³)_1";
        String OVERLAP_PERCENT_1 = "OVERLAP_PERCENT_1";
        String OVERLAP_VOX_2 = "OVERLAP_VOXELS_2";
        String OVERLAP_VOL_PX_2 = "OVERLAP_VOLUME_(PX³)_2";
        String OVERLAP_VOL_CAL_2 = "OVERLAP_VOLUME_(${SCAL}³)_2";
        String OVERLAP_PERCENT_2 = "OVERLAP_PERCENT_2";

    }

    public static String getFullName(String objectsName, String measurement) {
        return "OBJ_OVERLAP // "+objectsName+"_"+measurement.substring(0,measurement.length()-2);

    }

    public static int getNOverlappingPoints(Obj inputObject1, ObjCollection inputObjects2, boolean linkInSameFrame) {
        Volume overlap = new Volume(inputObject1.getVolumeType(),inputObject1.getSpatialCalibration());

        // Running through each object, getting a list of overlapping pixels
        for (Obj obj2:inputObjects2.values()) {
            // If only linking objects in the same frame, we may just skip this object
            if (linkInSameFrame && inputObject1.getT() != obj2.getT()) continue;

            Volume currentOverlap = inputObject1.getOverlappingPoints(obj2);

            overlap.getCoordinateSet().addAll(currentOverlap.getCoordinateSet());

        }

        return overlap.size();

    }



    @Override
    public Category getCategory() {
        return Categories.OBJECT_MEASUREMENTS_SPATIAL;
    }

    @Override
    public String getDescription() {
        return "Calculates the overlap of each object in an object collection with any object from another collection.  Overlaps are calculated for both specified object collections and are stored as measurements associated with the relevant object.  Overlap can occur for multiple objects; however, doubly-overlapped regions will only be counted once (i.e. an object can have no more than 100% overlap).  For example, an object in the first collection with 20% overlap with one object and 12% overlap with another would receive an overlap measurement of 32% (assuming the two overlapping objects weren't themselves overlapped in the overlapping region).";
    }

    @Override
    public Status process(Workspace workspace) {
        // Getting objects
        String inputObjects1Name = parameters.getValue(OBJECT_SET_1);
        ObjCollection inputObjects1 = workspace.getObjectSet(inputObjects1Name);
        String inputObjects2Name = parameters.getValue(OBJECT_SET_2);
        ObjCollection inputObjects2 = workspace.getObjectSet(inputObjects2Name);

        // Getting parameters
        boolean linkInSameFrame = parameters.getValue(LINK_IN_SAME_FRAME);

        int totalObjects = inputObjects1.size()+inputObjects2.size();
        int count = 0;

        // Iterating over all object pairs, adding overlapping pixels to a HashSet based on their index
        for (Obj obj1:inputObjects1.values()) {
            double dppXY = obj1.getDppXY();
            double dppZ = obj1.getDppZ();

            // Calculating volume
            double objVolume = (double) obj1.size();
            double overlap = (double) getNOverlappingPoints(obj1,inputObjects2,linkInSameFrame);
            double overlapVolPx = overlap*dppZ/dppXY;
            double overlapVolCal = overlap*dppXY*dppXY*dppZ;
            double overlapPC = 100*overlap/objVolume;

            // Adding the measurements
            obj1.addMeasurement(new Measurement(getFullName(inputObjects2Name,Measurements.OVERLAP_VOX_1),overlap));
            obj1.addMeasurement(new Measurement(getFullName(inputObjects2Name,Measurements.OVERLAP_VOL_PX_1),overlapVolPx));
            obj1.addMeasurement(new Measurement(getFullName(inputObjects2Name,Measurements.OVERLAP_VOL_CAL_1),overlapVolCal));
            obj1.addMeasurement(new Measurement(getFullName(inputObjects2Name,Measurements.OVERLAP_PERCENT_1),overlapPC));

            writeStatus("Processed "+(++count)+" objects of "+totalObjects);

        }

        // Iterating over all object pairs, adding overlapping pixels to a HashSet based on their index
        for (Obj obj2:inputObjects2.values()) {
            double dppXY = obj2.getDppXY();
            double dppZ = obj2.getDppZ();

            // Calculating volume
            double objVolume = (double) obj2.size();
            double overlap = (double) getNOverlappingPoints(obj2,inputObjects1,linkInSameFrame);
            double overlapVolPx = overlap*dppZ/dppXY;
            double overlapVolCal = overlap*dppXY*dppXY*dppZ;
            double overlapPC = 100*overlap/objVolume;

            // Adding the measurements
            obj2.addMeasurement(new Measurement(getFullName(inputObjects1Name,Measurements.OVERLAP_VOX_2),overlap));
            obj2.addMeasurement(new Measurement(getFullName(inputObjects1Name,Measurements.OVERLAP_VOL_PX_2),overlapVolPx));
            obj2.addMeasurement(new Measurement(getFullName(inputObjects1Name,Measurements.OVERLAP_VOL_CAL_2),overlapVolCal));
            obj2.addMeasurement(new Measurement(getFullName(inputObjects1Name,Measurements.OVERLAP_PERCENT_2),overlapPC));

            writeStatus("Processed "+(++count)+" objects of "+totalObjects);

        }

        if (showOutput) inputObjects1.showMeasurements(this,modules);
        if (showOutput) inputObjects2.showMeasurements(this,modules);

        return Status.PASS;

    }

    @Override
    protected void initialiseParameters() {
        parameters.add(new SeparatorP(INPUT_SEPARATOR, this));
        parameters.add(new InputObjectsP(OBJECT_SET_1,this));
        parameters.add(new InputObjectsP(OBJECT_SET_2,this));
        parameters.add(new BooleanP(LINK_IN_SAME_FRAME, this, true));

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
        ObjMeasurementRefCollection returnedRefs = new ObjMeasurementRefCollection();
        String objects1Name = parameters.getValue(OBJECT_SET_1);
        String objects2Name = parameters.getValue(OBJECT_SET_2);

        String name = getFullName(objects2Name, Measurements.OVERLAP_VOX_1);
        ObjMeasurementRef reference = objectMeasurementRefs.getOrPut(name);
        reference.setObjectsName(objects1Name);
        returnedRefs.add(reference);

        name = getFullName(objects2Name, Measurements.OVERLAP_VOL_PX_1);
        reference = objectMeasurementRefs.getOrPut(name);
        reference.setObjectsName(objects1Name);
        returnedRefs.add(reference);

        name = getFullName(objects2Name, Measurements.OVERLAP_VOL_CAL_1);
        reference = objectMeasurementRefs.getOrPut(name);
        reference.setObjectsName(objects1Name);
        returnedRefs.add(reference);

        name = getFullName(objects2Name, Measurements.OVERLAP_PERCENT_1);
        reference = objectMeasurementRefs.getOrPut(name);
        reference.setObjectsName(objects1Name);
        returnedRefs.add(reference);

        name = getFullName(objects1Name, Measurements.OVERLAP_VOX_2);
        reference = objectMeasurementRefs.getOrPut(name);
        reference.setObjectsName(objects2Name);
        returnedRefs.add(reference);

        name = getFullName(objects1Name, Measurements.OVERLAP_VOL_PX_2);
        reference = objectMeasurementRefs.getOrPut(name);
        reference.setObjectsName(objects2Name);
        returnedRefs.add(reference);

        name = getFullName(objects1Name, Measurements.OVERLAP_VOL_CAL_2);
        reference = objectMeasurementRefs.getOrPut(name);
        reference.setObjectsName(objects2Name);
        returnedRefs.add(reference);

        name = getFullName(objects1Name, Measurements.OVERLAP_PERCENT_2);
        reference = objectMeasurementRefs.getOrPut(name);
        reference.setObjectsName(objects2Name);
        returnedRefs.add(reference);

        return returnedRefs;

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
        parameters.get(OBJECT_SET_1).setDescription("Object collection for which, the overlap of each object with any object from a separate object collection (specified by the \""+OBJECT_SET_2+"\" parameter) will be calculated.");

        parameters.get(OBJECT_SET_2).setDescription("Object collection for which, the overlap of each object with any object from a separate object collection (specified by the \""+OBJECT_SET_1+"\" parameter) will be calculated.");

        parameters.get(LINK_IN_SAME_FRAME).setDescription("When selected, objects will only be considered to have any overlap if they're present in the same frame (timepoint).");
    }
}
