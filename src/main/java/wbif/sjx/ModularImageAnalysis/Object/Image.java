// TODO: Switch all image processing to ImgLib2.  This will involve re-writing all parts that use ImagePlus.  For now Img<T> is returned directly from the ImagePlus, but eventually the image data will be stored directly as Img<T>.

package wbif.sjx.ModularImageAnalysis.Object;

import ij.ImagePlus;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by steph on 30/04/2017.
 */
public class Image <T extends RealType<T> & NativeType<T>> {
    private String name;
    private ImagePlus imagePlus;
    private LinkedHashMap<String,Measurement> measurements = new LinkedHashMap<>();


    // CONSTRUCTORS

    public Image(String name, ImagePlus imagePlus) {
        this.name = name;
        this.imagePlus = imagePlus;

    }


    // PUBLIC METHODS

    public void addMeasurement(Measurement measurement) {
        measurements.put(measurement.getName(), measurement);

    }

    public Measurement getMeasurement(String name) {
        return measurements.get(name);

    }


    // GETTERS AND SETTERS

    public String getName() {
        return name;
    }

    @Deprecated
    public ImagePlus getImagePlus() {
        return imagePlus;
    }

    @Deprecated
    public void setImagePlus(ImagePlus imagePlus) {
        this.imagePlus = imagePlus;
    }

    public Img<T> getImg() {
        return ImagePlusAdapter.wrap(imagePlus);

    }

    public HashMap<String, Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(LinkedHashMap<String, Measurement> singleMeasurements) {
        this.measurements = singleMeasurements;
    }

}