package wbif.sjx.MIA.Module.ObjectProcessing.Identification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URLDecoder;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import wbif.sjx.MIA.ExpectedObjects.ExpectedObjects;
import wbif.sjx.MIA.ExpectedObjects.Objects2D;
import wbif.sjx.MIA.ExpectedObjects.Objects3D;
import wbif.sjx.MIA.ExpectedObjects.Objects4D;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleTest;
import wbif.sjx.MIA.Object.Image;
import wbif.sjx.MIA.Object.Obj;
import wbif.sjx.MIA.Object.ObjCollection;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.WorkspaceCollection;
import wbif.sjx.common.Object.Volume.VolumeType;

/**
 * Created by Stephen Cross on 29/08/2017.
 */
public class IdentifyObjectsTest extends ModuleTest {
    private static int nThreads = Prefs.getThreads();

    @BeforeAll
    public static void setVerbose() {
        Module.setVerbose(false);
    }

    @BeforeAll
    public static void setThreadsBefore() {
        Prefs.setThreads(Math.min(3, nThreads));
    }

    @AfterAll
    public static void setThreadsAfter() {
        Prefs.setThreads(nThreads);
    }

    @Override
    public void testGetHelp() {
        assertNotNull(new IdentifyObjects(null).getDescription());
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit2DPointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects2D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects2D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit2DQuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects2D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects2D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit2DOcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects2D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects2D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DPointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DQuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DOcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit4DPointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects4D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects4D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(33, actualObjects.size());

        int count = 0;
        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit4DQuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects4D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects4D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(33, actualObjects.size());

        int count = 0;
        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit4DOcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects4D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects4D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(33, actualObjects.size());

        int count = 0;
        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @Test
    @Disabled
    public void testRunBlackBackground8bit5D() throws Exception {

    }

    /**
     * This tests that the system still works when presented with a labelled (rather
     * than binary) image.
     * 
     * @throws Exception
     */
    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DLabelledPointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DLabelledQuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DLabelledOcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunWhiteBackground8Bit3DPointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_whiteBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, true);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunWhiteBackground8Bit3DQuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_whiteBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, true);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunWhiteBackground8Bit3DOcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_whiteBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, true);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DSingleObjectPointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DSingleObjectQuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DSingleObjectOcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DLabelledSingleObjectPointListWithoutMT(VolumeType volumeType)
            throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DLabelledSingleObjectQuadTreeWithoutMT(VolumeType volumeType)
            throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DLabelledSingleObjectOcTreeWithoutMT(VolumeType volumeType)
            throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DPointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected",
                ExpectedObjects.Mode.SIXTEEN_BIT, dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DQuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected",
                ExpectedObjects.Mode.SIXTEEN_BIT, dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DOcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected",
                ExpectedObjects.Mode.SIXTEEN_BIT, dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DPointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DQuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DOcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DNot255PointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG_204.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DNot255QuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG_204.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DNot255OcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG_204.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DNot65535PointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG_15073.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DNot65535QuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG_15073.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DNot65535OcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG_15073.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DNot1PointListWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG_-0p54.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DNot1QuadTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG_-0p54.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DNot1OcTreeWithoutMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG_-0p54.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, false);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit2DPointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects2D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects2D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit2DQuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects2D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects2D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit2DOcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects2D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects2D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DPointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DQuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DOcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit4DPointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects4D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects4D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(33, actualObjects.size());

        int count = 0;
        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit4DQuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects4D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects4D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(33, actualObjects.size());

        int count = 0;
        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit4DOcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects4D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects4D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(33, actualObjects.size());

        int count = 0;
        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    /**
     * This tests that the system still works when presented with a labelled (rather
     * than binary) image.
     * 
     * @throws Exception
     */
    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DLabelledPointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DLabelledQuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DLabelledOcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunWhiteBackground8Bit3DPointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_whiteBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, true);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunWhiteBackground8Bit3DQuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_whiteBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, true);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunWhiteBackground8Bit3DOcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_whiteBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, true);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DSingleObjectPointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DSingleObjectQuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DSingleObjectOcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DLabelledSingleObjectPointListWithMT(VolumeType volumeType)
            throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DLabelledSingleObjectQuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8bit3DLabelledSingleObjectOcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/LabelledObjects/LabelledObjects3D_8bit.tif").getPath(), "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.SINGLE_OBJECT, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.BINARY,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(1, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DPointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected",
                ExpectedObjects.Mode.SIXTEEN_BIT, dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DQuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected",
                ExpectedObjects.Mode.SIXTEEN_BIT, dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DOcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected",
                ExpectedObjects.Mode.SIXTEEN_BIT, dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DPointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DQuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DOcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DNot255PointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG_204.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DNot255QuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG_204.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground8Bit3DNot255OcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_blackBG_204.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DNot65535PointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG_15073.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DNot65535QuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG_15073.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground16Bit3DNot65535OcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_16bit_blackBG_15073.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DNot1PointListWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG_-0p54.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.POINTLIST);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DNot1QuadTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG_-0p54.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.QUADTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

    @ParameterizedTest
    @EnumSource(VolumeType.class)
    public void testRunBlackBackground32Bit3DNot1OcTreeWithMT(VolumeType volumeType) throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null, 1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(
                this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_32bit_blackBG_-0p54.tif").getPath(),
                "UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image", ipl);
        workspace.addImage(image);

        // Initialising IdentifyObjects
        IdentifyObjects identifyObjects = new IdentifyObjects(null);
        identifyObjects.initialiseParameters();
        identifyObjects.updateParameterValue(IdentifyObjects.INPUT_IMAGE, "Test_image");
        identifyObjects.updateParameterValue(IdentifyObjects.OUTPUT_OBJECTS, "Test_output_objects");
        identifyObjects.updateParameterValue(IdentifyObjects.WHITE_BACKGROUND, false);
        identifyObjects.updateParameterValue(IdentifyObjects.ENABLE_MULTITHREADING, true);
        identifyObjects.updateParameterValue(IdentifyObjects.VOLUME_TYPE, IdentifyObjects.VolumeTypes.OCTREE);

        // Running IdentifyObjects
        identifyObjects.execute(workspace);

        // Checking there is only one set of objects in the workspace
        assertEquals(1, workspace.getObjects().size());

        // Getting the object set
        ObjCollection actualObjects = workspace.getObjectSet("Test_output_objects");

        // Checking the expected object set is present
        assertEquals("Test_output_objects", actualObjects.getName());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";

        // Bit depth here doesn't matter - we only want to check the module can
        // interpret 32-bit images
        ObjCollection expectedObjects = new Objects3D(volumeType).getObjects("Expected", ExpectedObjects.Mode.EIGHT_BIT,
                dppXY, dppZ, calibratedUnits, true);

        // Checking the number of detected objects
        assertEquals(8, actualObjects.size());

        for (Obj object : actualObjects.values()) {
            // Identifying the matching object. If this is null, one isn't found
            Obj expectedObject = expectedObjects.getByEquals(object);
            assertNotNull(expectedObject);

        }
    }

}