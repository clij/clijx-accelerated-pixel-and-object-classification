package net.haesleinhuepf.clijx.apoc;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_pixelClassifier_5")
public class PixelClassifier_5 extends PixelClassifier {
    public PixelClassifier_5() {
        super(5);
    }
}
