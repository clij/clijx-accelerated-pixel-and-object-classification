package net.haesleinhuepf.clijx.apoc;


import com.android.dx.rop.cst.CstEnumRef;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.CLIJ;
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
import java.util.ArrayList;
import java.util.HashMap;

import static net.haesleinhuepf.clijx.apoc.Utilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_objectClassifier")
public class ObjectClassifier extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasClassifiedInputOutput {

    private String help_text;
    public ObjectClassifier() {
        help_text = "Image intensity_input, Image labels_input, ByRef Image destination, String model_filename";
    }

    @Override
    public String getParameterHelpText() {
        return help_text;
    }

    @Override
    public boolean executeCL() {
        ClearCLBuffer intensity_input = (ClearCLBuffer) args[0];
        ClearCLBuffer labels_input = (ClearCLBuffer) args[1];

        ClearCLBuffer output = (ClearCLBuffer) args[2];

        String model_filename = (String) args[3];

        checkModelApplicability(model_filename, getClass().getSimpleName().split("_")[0]);

        return objectClassifier(getCLIJ2(), intensity_input, labels_input, output, model_filename);
    }

    @Override
    public String getDescription() {
        return "Applies a pre-trained APOC model to an intensity  image and a label image to classify labeled objects. \n\n" +
                "Read more about how to train these models:\n" +
                "https://github.com/haesleinhuepf/napari-accelerated-pixel-and-object-classification";
    }

    public String getDefaultClassifierFilename() {
        return "ObjectClassifier.cl";
    }

    public static void main(String[] args) throws IOException {
        new ImageJ();

        ImagePlus imp = IJ.openImage("demo/blobs.tif");
        String model_filename = "demo/ObjectClassifier.cl";

        model_filename = new File(model_filename).getCanonicalPath();

        System.out.println(model_filename);

        CLIJ2 clij2 = CLIJ2.getInstance();

        ClearCLBuffer intensity_input = clij2.push(imp);
        ClearCLBuffer binary = clij2.create(intensity_input.getDimensions(), clij2.UnsignedByte);
        ClearCLBuffer labels_input = clij2.create(intensity_input.getDimensions(), clij2.UnsignedShort);
        clij2.thresholdOtsu(intensity_input, binary);
        clij2.connectedComponentsLabelingDiamond(binary, labels_input);
        ClearCLBuffer output = clij2.create(intensity_input.getDimensions(), clij2.UnsignedShort);

        objectClassifier(clij2, intensity_input, labels_input, output, model_filename);

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


    public static boolean objectClassifier(CLIJ2 clij2, ClearCLBuffer intensity_input, ClearCLBuffer labels_input, ClearCLBuffer output, String model_filename)
    {
        String features = readSomethingFromOpenCLFile(model_filename, FEATURE_SPECIFICATION_KEY, "Custom/unkown");

        LabelFeatureGenerator lfg = new LabelFeatureGenerator(clij2, intensity_input, labels_input, features);

        String[] feature_names = lfg.getFeatureList();
        ArrayList<ClearCLBuffer> feature_images = lfg.getResult();

        ClearCLBuffer classification_vector = clij2.create(feature_images.get(0).getDimensions(), clij2.UnsignedShort);

        HashMap<String, Object> parameters = new HashMap<>();

        int count = 0;
        for (ClearCLBuffer feature : feature_images) {
            parameters.put("in" + count, feature);

            if (CLIJ.debug) {
                System.out.print(feature_names[count] + ": ");
                clij2.print(feature);
            }
            count = count + 1;
        }
        parameters.put("out", classification_vector);

        clij2.execute(PixelClassifier.class, model_filename, "predict", classification_vector.getDimensions(), classification_vector.getDimensions(), parameters, null);

        for (ClearCLBuffer feature : feature_images) {
            feature.close();
        }

        // set background to 0
        clij2.setColumn(classification_vector, 0, 0);

        if (CLIJ.debug) {
            System.out.print("classification_vector: ");
            clij2.print(classification_vector);
        }
        clij2.replaceIntensities(labels_input, classification_vector, output);

        return true;
    }



    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Machine Learning, Segmentation, Classification";
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
