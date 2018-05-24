package wbif.sjx.ModularImageAnalysis.Object;

import ij.IJ;
import ij.ImagePlus;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import org.junit.Test;
import wbif.sjx.ModularImageAnalysis.ExpectedObjects3D;

import java.net.URLDecoder;

import static org.junit.Assert.*;

public class ImageTest < T extends RealType< T > & NativeType< T >> {
    @Test
    public void testConstructorImagePlus() throws Exception {
        // Loading the test image
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/LabelledObjects5D_8bit.tif").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image",ipl);

        // Checking the image has the right name
        assertEquals("Test_image",image.getName());

        // Checking the image doesn't have any Measurements to start with
        assertEquals(0,image.getMeasurements().size());

        // Checking the image is the right size
        assertNotNull(image.getImagePlus());
        ImagePlus iplOut = image.getImagePlus();
        assertEquals(64,iplOut.getWidth());
        assertEquals(76,iplOut.getHeight());
        assertEquals(2,iplOut.getNChannels());
        assertEquals(12,iplOut.getNSlices());
        assertEquals(4,iplOut.getNFrames());

    }

    @Test
    public void testConstructorImg() throws Exception {
        // Loading the test image
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/LabelledObjects5D_8bit.tif").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Img<T> img = ImageJFunctions.wrap(ipl);
        Image<T> image = new Image<>("Test_image",img);

        // Checking the image has the right name
        assertEquals("Test_image",image.getName());

        // Checking the image doesn't have any Measurements to start with
        assertEquals(0,image.getMeasurements().size());

        // Checking the image is the right size
        assertNotNull(image.getImagePlus());
        ImagePlus iplOut = image.getImagePlus();
        assertEquals(64,iplOut.getWidth());
        assertEquals(76,iplOut.getHeight());
        assertEquals(2,iplOut.getNChannels());
        assertEquals(12,iplOut.getNSlices());
        assertEquals(4,iplOut.getNFrames());

    }

    /**
     * Tests the ability to take an image containing labelled pixels and turn it into an ObjCollection.
     * @throws Exception
     */
    @Test
    public void testConvertImageToObjects8bit3D() throws Exception {
        // Loading the test image
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/LabelledObjects3D_8bit.tif").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image",ipl);

        // Setting other parameters
        String testObjectsName = "Test objects";

        // Running the method to be tested
        ObjCollection actualObjects = image.convertImageToObjects(testObjectsName);

        // Checking objects have been assigned
        assertNotNull("Testing converted objects are not null",actualObjects);

        // Checking there are the expected number of objects
        assertEquals("Testing the number of converted objects",8,actualObjects.size());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new ExpectedObjects3D().getObjects("Expected",true,dppXY,dppZ,calibratedUnits,true);

        for (Obj object:expectedObjects.values()) {
            // Identifying the matching object.  If this is null, one isn't found
            Obj actualObject = actualObjects.getByEquals(object);
            assertNotNull(actualObject);
        }
    }

    /**
     * Tests the ability to take an image containing labelled pixels and turn it into an ObjCollection.
     * @throws Exception
     */
    @Test
    public void testConvertImageToObjects16bit3D() throws Exception {
        // Loading the test image
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/LabelledObjects3D_16bit.tif").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image",ipl);

        // Setting other parameters
        String testObjectsName = "Test objects";

        // Running the method to be tested
        ObjCollection actualObjects = image.convertImageToObjects(testObjectsName);

        // Checking objects have been assigned
        assertNotNull("Testing converted objects are not null",actualObjects);

        // Checking there are the expected number of objects
        assertEquals("Testing the number of converted objects",8,actualObjects.size());

        // Getting the expected objects
        double dppXY = 0.02;
        double dppZ = 0.1;
        String calibratedUnits = "µm";
        ObjCollection expectedObjects = new ExpectedObjects3D().getObjects("Expected",true,dppXY,dppZ,calibratedUnits,true);

        for (Obj object:expectedObjects.values()) {
            // Identifying the matching object.  If this is null, one isn't found
            Obj actualObject = actualObjects.getByEquals(object);
            assertNotNull(actualObject);
        }
    }

    @Test
    public void testAddMeasurement() throws Exception {
        // Loading the test image
        String pathToImage = URLDecoder.decode(this.getClass().getResource("/images/LabelledObjects3D_8bit.tif").getPath(),"UTF-8");
        ImagePlus ipl = IJ.openImage(pathToImage);
        Image image = new Image("Test_image",ipl);

        // Adding a couple of measurements
        image.addMeasurement(new Measurement("Meas 1",1.2));
        image.addMeasurement(new Measurement("Second meas",-9));
        image.addMeasurement(new Measurement("Meas 3.0",3.0));

        // Checking the measurements are there
        assertEquals(3,image.getMeasurements().size());
        assertEquals(1.2, image.getMeasurement("Meas 1").getValue(),0);
        assertEquals(-9, image.getMeasurement("Second meas").getValue(),0);
        assertEquals(3.0, image.getMeasurement("Meas 3.0").getValue(),0);

    }
}