package wbif.sjx.MIA.Module.ImageProcessing.Pixel.Binary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URLDecoder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ij.IJ;
import ij.ImagePlus;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Module.ModuleTest;
import wbif.sjx.MIA.Object.Image;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.WorkspaceCollection;

public class FillHolesTest extends ModuleTest {

    @BeforeAll
    public static void setVerbose() {
        Module.setVerbose(false);
    }
    
    @Override
    public void testGetHelp() {
        assertNotNull(new FillHoles(null).getDescription());
    }

    @Test
    public void testRunWithFillHoles2DOperation2DStack() throws Exception {
        // Creating a new workspace
        WorkspaceCollection workspaces = new WorkspaceCollection();
        Workspace workspace = workspaces.getNewWorkspace(null,1);

        // Loading the test image and adding to workspace
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/BinaryObjects/BinaryObjects3D_8bit_whiteBG.tif").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image",ipl);
        workspace.addImage(image);

        pathToImage = URLDecoder.decode(this.getClass().getResource("/images/BinaryOperations/BinaryObjects3D_8bit_whiteBG_fillHoles3D.tif").getPath(),"UTF-8");
        Image expectedImage = new Image("Expected", IJ.openImage(pathToImage));

        // Initialising BinaryOperations
        FillHoles binaryOperations = new FillHoles(new ModuleCollection());
        binaryOperations.updateParameterValue(FillHoles.INPUT_IMAGE,"Test_image");
        binaryOperations.updateParameterValue(FillHoles.APPLY_TO_INPUT,false);
        binaryOperations.updateParameterValue(FillHoles.OUTPUT_IMAGE,"Test_output");

        // Running Module
        binaryOperations.execute(workspace);

        // Checking the images in the workspace
        assertEquals(2,workspace.getImages().size());
        assertNotNull(workspace.getImage("Test_image"));
        assertNotNull(workspace.getImage("Test_output"));

        // Checking the output image has the expected calibration
        Image outputImage = workspace.getImage("Test_output");
        assertEquals(expectedImage,outputImage);

    }
}