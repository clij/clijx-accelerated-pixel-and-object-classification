package net.haesleinhuepf.clijx.apoc;


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import net.haesleinhuepf.clij.clearcl.ClearCL;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasClassifiedInputOutput;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static net.haesleinhuepf.clijx.apoc.Utilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_pixelClassifier")
public class PixelClassifier extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasClassifiedInputOutput {

    private String help_text;
    protected final int number_of_input_images;
    public PixelClassifier() {
        this(1);
    }

    public PixelClassifier(int number_of_input_images) {
        this.number_of_input_images = number_of_input_images;
        help_text = "";
        for (int i = 1; i <= number_of_input_images; i ++) {
            help_text = help_text + "Image input" + i + ", ";
        }
        help_text = help_text  + "ByRef Image destination, String model_filename";
    }

    @Override
    public String getParameterHelpText() {
        return help_text;
    }

    @Override
    public boolean executeCL() {
        ClearCLBuffer[] inputs = new ClearCLBuffer[number_of_input_images];
        for (int i = 0; i < number_of_input_images; i ++) {
            inputs[i] = (ClearCLBuffer) args[i];
        }
        ClearCLBuffer output = (ClearCLBuffer) args[number_of_input_images];

        String model_filename = (String) args[number_of_input_images + 1];

        if (checkModelApplicability(model_filename, getClass().getSimpleName().split("_")[0])) {
            return pixelClassifier(getCLIJ2(), inputs, output, model_filename);
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Applies a pre-trained APOC model to " + number_of_input_images + " image(s) to classify pixels. \n\n" +
                "Read more about how to train these models:\n" +
                "https://github.com/haesleinhuepf/napari-accelerated-pixel-and-object-classification";
    }

    public String getDefaultClassifierFilename() {
        return "PixelClassifier.cl";
    }

    public static void main(String[] args) throws IOException {
        new ImageJ();

        ImagePlus imp = IJ.openImage("demo/blobs.tif");
        String model_filename = "demo/PixelClassifier.cl";

        model_filename = new File(model_filename).getCanonicalPath();

        System.out.println(model_filename);

        CLIJ2 clij2 = CLIJ2.getInstance();

        ClearCLBuffer input = clij2.push(imp);
        ClearCLBuffer output = clij2.create(input.getDimensions(), clij2.UnsignedShort);

        pixelClassifier(clij2, new ClearCLBuffer[]{input}, output, model_filename);

        clij2.show(output, "output");
    }

    @Override
    public Object[] getDefaultValues() {
        int num_parameters = getParameterHelpText().split(",").length;
        Object[] defaults = new Object[num_parameters];
        for (int i = 0; i < defaults.length - 1; i++) {
            defaults[i] = null;
        }
        defaults[defaults.length - 1] = getDefaultClassifierFilename();

        return defaults;
    }


    public static boolean pixelClassifier(CLIJ2 clij2, ClearCLBuffer[] inputs, ClearCLBuffer output, String model_filename)
    {

        String features = readSomethingFromOpenCLFile(model_filename, FEATURE_SPECIFICATION_KEY, "Custom/unkown");

        ArrayList<ClearCLBuffer> featureImages = new ArrayList<>();
        for (ClearCLBuffer input : inputs) {
            featureImages.addAll(generateFeatureStack(clij2, input, features));
        }

        HashMap<String, Object> parameters = new HashMap<>();

        int count = 0;
        for (ClearCLBuffer feature : featureImages) {
            parameters.put("in" + count, feature);
            clij2.show(feature, "in" + count);
            count = count + 1;
        }
        parameters.put("out", output);

        clij2.execute(PixelClassifier.class, model_filename, "predict", output.getDimensions(), output.getDimensions(), parameters, null);

        for (ClearCLBuffer feature : featureImages) {
            feature.close();
        }

        return true;
    }



    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Machine Learning, Segmentation";
    }

    @Override
    public String getInputType() {
        return "Image";
    }

    @Override
    public String getOutputType() {
        return "Label Image";
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return getCLIJ2().create(input.getDimensions(), NativeTypeEnum.UnsignedShort);
    }
}
