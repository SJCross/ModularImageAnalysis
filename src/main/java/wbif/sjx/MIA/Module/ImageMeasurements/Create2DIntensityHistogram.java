package wbif.sjx.MIA.Module.ImageMeasurements;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.histogram.HistogramNd;
import net.imglib2.histogram.Real1dBinMapper;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import wbif.sjx.MIA.MIA;
import wbif.sjx.MIA.Module.Categories;
import wbif.sjx.MIA.Module.Category;
import wbif.sjx.MIA.Module.Module;
import wbif.sjx.MIA.Module.ModuleCollection;
import wbif.sjx.MIA.Object.Image;
import wbif.sjx.MIA.Object.Status;
import wbif.sjx.MIA.Object.Workspace;
import wbif.sjx.MIA.Object.Parameters.BooleanP;
import wbif.sjx.MIA.Object.Parameters.InputImageP;
import wbif.sjx.MIA.Object.Parameters.OutputImageP;
import wbif.sjx.MIA.Object.Parameters.ParameterCollection;
import wbif.sjx.MIA.Object.Parameters.SeparatorP;
import wbif.sjx.MIA.Object.Parameters.Text.DoubleP;
import wbif.sjx.MIA.Object.Parameters.Text.IntegerP;
import wbif.sjx.MIA.Object.References.Collections.ImageMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.MetadataRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ObjMeasurementRefCollection;
import wbif.sjx.MIA.Object.References.Collections.ParentChildRefCollection;
import wbif.sjx.MIA.Object.References.Collections.PartnerRefCollection;

public class Create2DIntensityHistogram<T extends RealType<T> & NativeType<T>> extends Module {
    public static final String INPUT_SEPARATOR = "Image input/output";
    public final static String INPUT_IMAGE1 = "Input image 1 (x-axis)";
    public final static String INPUT_IMAGE2 = "Input image 2 (y-axis)";
    public final static String OUTPUT_IMAGE = "Output image";

    public static final String HISTOGRAM_SEPARATOR_1 = "Histogram controls (image 1)";
    public static final String MIN_BIN_1 = "Minimum bin (image 1)";
    public static final String MAX_BIN_1 = "Maximum bin (image 1)";
    public static final String N_BINS_1 = "Number of bins (image 1)";
    public static final String INCLUDE_TAIL_BIN_1 = "Include tail bin (image 1)";

    public static final String HISTOGRAM_SEPARATOR_2 = "Histogram controls (image 2)";
    public static final String MIN_BIN_2 = "Minimum bin (image 2)";
    public static final String MAX_BIN_2 = "Maximum bin (image 2)";
    public static final String N_BINS_2 = "Number of bins (image 2)";
    public static final String INCLUDE_TAIL_BIN_2 = "Include tail bin (image 2)";

    public Create2DIntensityHistogram(ModuleCollection modules) {
        super("Create 2D intensity histogram", modules);
    }

    @Override
    public Category getCategory() {
        return Categories.IMAGE_MEASUREMENTS;
    }

    @Override
    public String getDescription() {
        return "Creates a 2D intensity histogram for a pair of specified images.  Intensities along the x-axis correspond to the first input image and those along the y-axis to the second input image.  Output histogram is saved to the workspace as an image.  Works for N-dimensional image stacks (must have the same dimensions).  Uses the ImgLib2 implementation ND intensity histograms.";
    }

    @Override
    public Status process(Workspace workspace) {
        String inputImageName1 = parameters.getValue(INPUT_IMAGE1);
        Image inputImage1 = workspace.getImage(inputImageName1);
        String inputImageName2 = parameters.getValue(INPUT_IMAGE2);
        Image inputImage2 = workspace.getImage(inputImageName2);

        String outputImageName = parameters.getValue(OUTPUT_IMAGE);
        double minBin1 = parameters.getValue(MIN_BIN_1);
        double maxBin1 = parameters.getValue(MAX_BIN_1);
        int nBins1 = parameters.getValue(N_BINS_1);
        boolean includeTailBin1 = parameters.getValue(INCLUDE_TAIL_BIN_1);
        double minBin2 = parameters.getValue(MIN_BIN_2);
        double maxBin2 = parameters.getValue(MAX_BIN_2);
        int nBins2 = parameters.getValue(N_BINS_2);
        boolean includeTailBin2 = parameters.getValue(INCLUDE_TAIL_BIN_2);

        double[] minVals = new double[] { minBin1, minBin2 };
        double[] maxVals = new double[] { maxBin1, maxBin2 };
        long[] numBins = new long[] { nBins1, nBins2 };
        boolean[] tailBins = new boolean[] { includeTailBin1, includeTailBin2 };
        HistogramNd<FloatType> hist = Real1dBinMapper.histogramNd(minVals, maxVals, numBins, tailBins);

        List<Iterable<FloatType>> intervals = new ArrayList<>();
        intervals.add(inputImage1.getImgPlus());
        intervals.add(inputImage2.getImgPlus());

        hist.addData(intervals);
        
        Image outputImage = new Image(outputImageName, ImageJFunctions.wrapFloat(hist, outputImageName));
        workspace.addImage(outputImage);

        if (showOutput)
            outputImage.showImage();

        return Status.PASS;

    }

    @Override
    protected void initialiseParameters() {
        parameters.add(new SeparatorP(INPUT_SEPARATOR, this));
        parameters.add(new InputImageP(INPUT_IMAGE1, this));
        parameters.add(new InputImageP(INPUT_IMAGE2, this));
        parameters.add(new OutputImageP(OUTPUT_IMAGE, this));

        parameters.add(new SeparatorP(HISTOGRAM_SEPARATOR_1, this));
        parameters.add(new DoubleP(MIN_BIN_1, this, 0d));
        parameters.add(new DoubleP(MAX_BIN_1, this, 255d));
        parameters.add(new IntegerP(N_BINS_1, this, 256));
        parameters.add(new BooleanP(INCLUDE_TAIL_BIN_1, this, false));

        parameters.add(new SeparatorP(HISTOGRAM_SEPARATOR_2, this));
        parameters.add(new DoubleP(MIN_BIN_2, this, 0d));
        parameters.add(new DoubleP(MAX_BIN_2, this, 255d));
        parameters.add(new IntegerP(N_BINS_2, this, 256));
        parameters.add(new BooleanP(INCLUDE_TAIL_BIN_2, this, false));

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

    public void addParameterDescriptions() {
        parameters.get(INPUT_IMAGE1).setDescription("First image from the workspace used in 2D intensity histogram plotting.  Intensities from this image are represented along te output histogram x-axis (columns).  For multidimensional stacks, all pixel values are compiled into a single intensity histogram.");

        parameters.get(INPUT_IMAGE2).setDescription("Second image from the workspace used in 2D intensity histogram plotting.  Intensities from this image are represented along te output histogram y-axis (rows).  For multidimensional stacks, all pixel values are compiled into a single intensity histogram.");

        parameters.get(OUTPUT_IMAGE).setDescription("Output 2D intensity histogram, which will be saved to the workspace.");

        parameters.get(MIN_BIN_1).setDescription("Minimum intenisty bin for the image specified by \""+INPUT_IMAGE1+"\".");

        parameters.get(MAX_BIN_1).setDescription("Maximum intenisty bin for the image specified by \""+INPUT_IMAGE1+"\".");

        parameters.get(N_BINS_1).setDescription("Number of intensity bins to use for the image specified by \""+INPUT_IMAGE1+"\".");

        parameters.get(INCLUDE_TAIL_BIN_1).setDescription("When selected, additional bins at the extremes of the histogram x-axis will be used for intensity values that fall outside the specified intensity range.");

        parameters.get(MIN_BIN_2).setDescription("Minimum intenisty bin for the image specified by \""+INPUT_IMAGE2+"\".");

        parameters.get(MAX_BIN_2).setDescription("Maximum intenisty bin for the image specified by \""+INPUT_IMAGE2+"\".");

        parameters.get(N_BINS_2).setDescription("Number of intensity bins to use for the image specified by \""+INPUT_IMAGE2+"\".");

        parameters.get(INCLUDE_TAIL_BIN_2).setDescription("When selected, additional bins at the extremes of the histogram y-axis will be used for intensity values that fall outside the specified intensity range.");

    }
}