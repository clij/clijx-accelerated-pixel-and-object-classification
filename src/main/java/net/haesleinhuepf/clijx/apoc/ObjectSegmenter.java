package net.haesleinhuepf.clijx.apoc;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.CLIJ2;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.io.IOException;

import static net.haesleinhuepf.clijx.apoc.Utilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_objectSegmenter")
public class ObjectSegmenter extends PixelClassifier {

    public ObjectSegmenter() {
        this(1);
    }
    public ObjectSegmenter(int number_of_input_images) {
        super(number_of_input_images);
    }

    @Override
    public String getDescription() {
        return "Applies a pre-trained APOC model to " + number_of_input_images + " image(s) to segment objects and generate a label image. \n\n" +
                "Read more about how to train these models:\n" +
                "https://github.com/haesleinhuepf/napari-accelerated-pixel-and-object-classification";
    }

    public String getDefaultClassifierFilename() {
        return "ObjectSegmenter.cl";
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
            return objectSegmenter(getCLIJ2(), inputs, output, model_filename);
        }
        return false;
    }

    public static boolean objectSegmenter(CLIJ2 clij2, ClearCLBuffer[] inputs, ClearCLBuffer output, String model_filename) {
        ClearCLBuffer pixel_classification = clij2.create(output.getDimensions(), clij2.UnsignedShort);
        pixelClassifier(clij2, inputs, pixel_classification, model_filename);

        ClearCLBuffer binary = clij2.create(output.getDimensions(), clij2.UnsignedByte);
        int positive_class_identifier = Integer.parseInt(readSomethingFromOpenCLFile(model_filename, POSITIVE_CLASS_IDENTIFIER_KEY, "2"));

        clij2.equalConstant(pixel_classification, binary, positive_class_identifier);
        pixel_classification.close();

        clij2.connectedComponentsLabelingDiamond(binary, output);
        binary.close();

        return true;
    }

    public static void main(String[] args) throws IOException {
        new ImageJ();

        ImagePlus imp = IJ.openImage("docs/data/blobs.tif");
        String model_filename = "docs/data/ObjectSegmenter.cl";

        model_filename = new File(model_filename).getCanonicalPath();

        System.out.println(model_filename);

        CLIJ2 clij2 = CLIJ2.getInstance();

        ClearCLBuffer input = clij2.push(imp);
        ClearCLBuffer output = clij2.create(input.getDimensions(), clij2.UnsignedShort);

        objectSegmenter(clij2, new ClearCLBuffer[]{input}, output, model_filename);

        clij2.show(output, "output");
    }
}
