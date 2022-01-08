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

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_probabilityMapper")
public class ProbabilityMapper extends PixelClassifier {

    @Override
    public String getDescription() {
        return "Applies a pre-trained APOC model to " + number_of_input_images + " image(s) to generate a probability image for a specific class. \n\n" +
                "Read more about how to train these models:\n" +
                "https://github.com/haesleinhuepf/napari-accelerated-pixel-and-object-classification";
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return getCLIJ2().create(input.getDimensions(), NativeTypeEnum.Float);
    }

    public static boolean probabilityMapper(CLIJ2 clij2, ClearCLBuffer[] inputs, ClearCLBuffer output, String model_filename) {
        return pixelClassifier(clij2, inputs, output, model_filename);
    }

    public static void main(String[] args) throws IOException {
        new ImageJ();

        ImagePlus imp = IJ.openImage("demo/blobs.tif");
        String model_filename = "demo/ProbabilityMapper.cl";

        model_filename = new File(model_filename).getCanonicalPath();

        System.out.println(model_filename);

        CLIJ2 clij2 = CLIJ2.getInstance();

        ClearCLBuffer input = clij2.push(imp);
        ClearCLBuffer output = clij2.create(input.getDimensions(), clij2.Float);

        probabilityMapper(clij2, new ClearCLBuffer[]{input}, output, model_filename);

        clij2.show(output, "output");
    }
}
