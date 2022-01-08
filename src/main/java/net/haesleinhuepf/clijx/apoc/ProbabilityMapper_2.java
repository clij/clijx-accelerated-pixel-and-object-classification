package net.haesleinhuepf.clijx.apoc;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.CLIJ2;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.io.IOException;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_probabilityMapper_2")
public class ProbabilityMapper_2 extends ProbabilityMapper {
    public ProbabilityMapper_2() {
        super(2);
    }

    public static void main(String[] args) throws IOException {
        new ImageJ();

        ImagePlus imp = IJ.openImage("demo/blobs.tif");
        String model_filename = "demo/ProbabilityMapper2.cl";

        model_filename = new File(model_filename).getCanonicalPath();

        System.out.println(model_filename);

        CLIJ2 clij2 = CLIJ2.getInstance();

        ClearCLBuffer input = clij2.push(imp);
        ClearCLBuffer output = clij2.create(input.getDimensions(), clij2.Float);

        pixelClassifier(clij2, new ClearCLBuffer[]{input, input}, output, model_filename);

        clij2.show(output, "output");
    }
}
