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

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_pixelClassifier_3")
public class PixelClassifier_3 extends PixelClassifier {
    public PixelClassifier_3() {
        super(3);
    }
}
