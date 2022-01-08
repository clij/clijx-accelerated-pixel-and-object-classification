package net.haesleinhuepf.clijx.apoc;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_pixelClassifier_4")
public class PixelClassifier_4 extends PixelClassifier {
    public PixelClassifier_4() {
        super(4);
    }
}
