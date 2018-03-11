package wbif.sjx.ModularImageAnalysis.Module.ImageProcessing.Pixel;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.plugin.Duplicator;
import ij.process.ImageProcessor;
import wbif.sjx.ModularImageAnalysis.Exceptions.GenericMIAException;
import wbif.sjx.ModularImageAnalysis.Module.Module;
import wbif.sjx.ModularImageAnalysis.Object.*;
import wbif.sjx.ModularImageAnalysis.Object.Image;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by sc13967 on 27/02/2018.
 */
public class ManuallyCreateBinaryImage extends Module implements ActionListener {
    private JFrame frame = new JFrame();
    private JTextField objectNumberField;

    private Workspace workspace;
    private ImagePlus displayImagePlus;
    private ImagePlus outputImagePlus;
    private String outputImageName;
    private Overlay overlay;

    private double dppXY;
    private double dppZ;
    private String calibrationUnits;


    private static final String ADD = "Add region";
    private static final String FINISH = "Finish";

    public static final String INPUT_IMAGE = "Input image";
    public static final String OUTPUT_IMAGE = "Output image";
    public static final String SHOW_IMAGE = "Show image";


    private void showOptionsPanel() {
        frame = new JFrame();
        GridLayout gridLayout = new GridLayout(0, 1);
        frame.setLayout(gridLayout);

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

        JLabel headerLabel = new JLabel("<html>Draw round a region, then select \"Add\"<br>(or click \"Finish adding regions\" at any time)</html>");
        headerPanel.add(headerLabel);

        frame.add(headerPanel);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        JButton newObjectButton = new JButton("Add region");
        newObjectButton.addActionListener(this);
        newObjectButton.setActionCommand(ADD);
        buttonsPanel.add(newObjectButton);

        JButton finishButton = new JButton("Finish adding regions");
        finishButton.addActionListener(this);
        finishButton.setActionCommand(FINISH);
        buttonsPanel.add(finishButton);

        frame.add(buttonsPanel);

        frame.pack();
        frame.setVisible(true);

    }

    @Override
    public String getTitle() {
        return "Manually create binary image";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    protected void run(Workspace workspace, boolean verbose) throws GenericMIAException {
        // Local access to this is required for the action listeners
        this.workspace = workspace;

        // Getting parameters
        String inputImageName = parameters.getValue(INPUT_IMAGE);
        outputImageName = parameters.getValue(OUTPUT_IMAGE);
        boolean showImage = parameters.getValue(SHOW_IMAGE);

        // Getting input image
        Image inputImage = workspace.getImage(inputImageName);
        ImagePlus inputImagePlus = inputImage.getImagePlus();
        displayImagePlus = new Duplicator().run(inputImagePlus);
        displayImagePlus.setCalibration(null);
        displayImagePlus.setTitle(getNickname());
        overlay = displayImagePlus.getOverlay();
        if (overlay == null) {
            overlay = new Overlay();
            displayImagePlus.setOverlay(overlay);
        }

        // Storing the image calibration
        Calibration calibration = inputImagePlus.getCalibration();
        dppXY = calibration.getX(1);
        dppZ = calibration.getZ(1);
        calibrationUnits = calibration.getUnits();

        // Creating an output image with dimensions matching the input image
        outputImagePlus = IJ.createHyperStack(outputImageName,inputImagePlus.getWidth(),inputImagePlus.getHeight(),inputImagePlus.getNChannels(),inputImagePlus.getNSlices(),inputImagePlus.getNFrames(),8);
        outputImagePlus.setCalibration(calibration);
        workspace.addImage(new Image(outputImageName,outputImagePlus));

        // Setting all pixels to white
        for (int z = 1; z <= outputImagePlus.getNSlices(); z++) {
            for (int c = 1; c <= outputImagePlus.getNChannels(); c++) {
                for (int t = 1; t <= outputImagePlus.getNFrames(); t++) {
                    outputImagePlus.setPosition(c, z, t);
                    ImageProcessor ipr = outputImagePlus.getProcessor();

                    for (int x = 0;x<outputImagePlus.getWidth();x++) {
                        for (int y = 0;y<outputImagePlus.getHeight();y++) {
                            ipr.putPixel(x,y,255);
                        }
                    }
                }
            }
        }
        outputImagePlus.setPosition(1,1,1);

        // Displaying the image and showing the control
        displayImagePlus.show();
        showOptionsPanel();

        while (frame != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (showImage) new Duplicator().run(outputImagePlus).show();

    }

    @Override
    protected void initialiseParameters() {
        parameters.add(new Parameter(INPUT_IMAGE, Parameter.INPUT_IMAGE, null));
        parameters.add(new Parameter(OUTPUT_IMAGE, Parameter.OUTPUT_IMAGE, null));
        parameters.add(new Parameter(SHOW_IMAGE,Parameter.BOOLEAN,false));
    }

    @Override
    protected void initialiseMeasurementReferences() {

    }

    @Override
    public ParameterCollection updateAndGetParameters() {
        return parameters;
    }

    @Override
    public MeasurementReferenceCollection updateAndGetImageMeasurementReferences() {
        return null;
    }

    @Override
    public MeasurementReferenceCollection updateAndGetObjectMeasurementReferences() {
        return null;
    }

    @Override
    public void addRelationships(RelationshipCollection relationships) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case (ADD):
                // Getting points
                Roi roi = displayImagePlus.getRoi();
                Point[] points = roi.getContainedPoints();

                // Setting all points inside the region to 0 (black objects)
                for (Point point:points) {
                    int x = (int) Math.round(point.getX());
                    int y = (int) Math.round(point.getY());
                    outputImagePlus.setPosition(displayImagePlus.getChannel(),displayImagePlus.getZ(),displayImagePlus.getT());
                    outputImagePlus.getProcessor().putPixel(x,y,0);
                }

                // Adding overlay showing ROI and its ID number
                overlay.add(roi);
                displayImagePlus.updateAndDraw();

                break;

            case (FINISH):
                frame.dispose();
                frame = null;

                break;
        }
    }
}