package net.haesleinhuepf.clijx.apoc;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Utilities {

    public static final String FEATURE_SPECIFICATION_KEY = "feature_specification = ";
    public static final String CLASSIFIER_CLASS_NAME_KEY = "classifier_class_name = ";
    public static final String POSITIVE_CLASS_IDENTIFIER_KEY = "positive_class_identifier = ";

    public static boolean checkModelApplicability(String model_filename, String klassName) {
        if (!new File(model_filename).exists()) {
            System.out.println("Model " + model_filename + " not found. Cancelling PixelClassifier.");
            return false;
        }

        String file_classifier = readSomethingFromOpenCLFile(model_filename, CLASSIFIER_CLASS_NAME_KEY, "Unknown");
        if (klassName.compareTo(file_classifier) != 0) {
            System.out.println("A classifier of type " + file_classifier + " cannot be applied using " + klassName);
            return false;
        }
        return true;
    }

    public static ArrayList<ClearCLBuffer> generateFeatureStack(CLIJ2 clij2, ClearCLBuffer input, String featureDefinitions) {
        String[] definitionsArray = preparseFeatures(featureDefinitions);

        HashMap<String, ClearCLBuffer> generatedFeatures = new HashMap<String, ClearCLBuffer>();

        // generate features
        for (String featureDefinition : definitionsArray) {
            generateFeature(clij2, input, generatedFeatures, featureDefinition);
        }

        // collect them in a list
        ArrayList<ClearCLBuffer> feature_buffers = new ArrayList<ClearCLBuffer>();
        for (String featureDefinition : definitionsArray) {
            feature_buffers.add(generatedFeatures.get(featureDefinition));
        }

        // release memory
        for (ClearCLBuffer buffer : generatedFeatures.values()) {
            if (!feature_buffers.contains(buffer)) {
                buffer.close();
            }
        }

        return feature_buffers;
    }

    public static String[] preparseFeatures(String featureDefinitions) {
        featureDefinitions = featureDefinitions.toLowerCase();
        featureDefinitions = featureDefinitions.trim();
        featureDefinitions = featureDefinitions.replace("\r", " ");
        featureDefinitions = featureDefinitions.replace("\n", " ");
        featureDefinitions = featureDefinitions.replace(",", " ");
        while (featureDefinitions.contains("  ")) {
            featureDefinitions = featureDefinitions.replace("  ", " ");
        }
        System.out.println("F:" + featureDefinitions);
        return featureDefinitions.split(" ");
    }

    public static ClearCLBuffer generateFeature(CLIJ2 clij2, ClearCLBuffer input, HashMap<String, ClearCLBuffer> generatedFeatures, String featureDefinition) {
        if (generatedFeatures.containsKey(featureDefinition)) {
            return generatedFeatures.get(featureDefinition);
        }
        String[] temp = featureDefinition.split("=");
        String featureName = temp[0];
        String parameter = temp.length>1?temp[1]:"0";
        double numericParameter = Double.parseDouble(parameter);
        int dimensionality = (int) input.getDimension();

        ClearCLBuffer output = clij2.create(input.getDimensions(), clij2.Float);
        if (featureName.compareTo("original") == 0) {
            clij2.copy(input, output);
        } else if (featureName.compareTo("gaussianblur") == 0 || featureName.compareTo("gaussian_blur") == 0) {
            System.out.println("Generating gaussian blur image" + numericParameter + " as " + featureDefinition);
            if (dimensionality == 2) {
                clij2.gaussianBlur2D(input, output, numericParameter, numericParameter);
            } else {
                clij2.gaussianBlur3D(input, output, numericParameter, numericParameter, numericParameter);
            }
        } else if (featureName.compareTo("gradientx") == 0) {
            clij2.gradientX(input, output);
        } else if (featureName.compareTo("gradienty") == 0) {
            clij2.gradientY(input, output);
        } else if (featureName.compareTo("minimum") == 0) {
            if (dimensionality == 2) {
                clij2.minimum2DBox(input, output, numericParameter, numericParameter);
            } else {
                clij2.minimum3DBox(input, output, numericParameter, numericParameter, numericParameter);
            }
        } else if (featureName.compareTo("maximum") == 0) {
            if (dimensionality == 2) {
                clij2.maximum2DBox(input, output, numericParameter, numericParameter);
            } else {
                clij2.maximum3DBox(input, output, numericParameter, numericParameter, numericParameter);
            }
        } else if (featureName.compareTo("mean") == 0) {
            if (dimensionality == 2) {
                clij2.mean2DBox(input, output, numericParameter, numericParameter);
            } else {
                clij2.mean3DBox(input, output, numericParameter, numericParameter, numericParameter);
            }
        } else if (featureName.compareTo("entropy") == 0) {
            if (dimensionality == 2) {
                clij2.entropyBox(input, output, numericParameter, numericParameter, 0);
            } else {
                clij2.entropyBox(input, output, numericParameter, numericParameter, numericParameter);
            }
        } else if (featureName.compareTo("laplacianofgaussian") == 0 || featureName.compareTo("laplace_box_of_gaussian_blur") == 0) {
            String midfix = "";
            if (featureName.contains("_")) {
                midfix = "_";
            }
            ClearCLBuffer gaussianBlurred = generateFeature(clij2, input, generatedFeatures, "gaussian" + midfix + "blur=" + parameter);
            clij2.laplaceBox(gaussianBlurred, output);
        } else if (featureName.compareTo("differenceofgaussian") == 0 || featureName.compareTo("difference_of_gaussian") == 0) {
            if (dimensionality == 2) {
                clij2.differenceOfGaussian2D(input, output, numericParameter * 0.9, numericParameter * 0.9, numericParameter * 1.1, numericParameter * 1.1);
            } else {
                clij2.differenceOfGaussian3D(input, output, numericParameter * 0.9, numericParameter * 0.9, numericParameter * 0.9, numericParameter * 1.1, numericParameter * 1.1, numericParameter * 1.1);
            }
        } else if (featureName.compareTo("sobelofgaussian") == 0 || featureName.compareTo"sobel_of_gaussian_blur") == 0) {
            String midfix = "";
            if (featureName.contains("_")) {
                midfix = "_";
            }
            ClearCLBuffer gaussianBlurred = generateFeature(clij2, input, generatedFeatures, "gaussian" + midfix + "blur=" + parameter);
            clij2.sobel(gaussianBlurred, output);
        } else {
            throw new IllegalArgumentException("Feature " + featureDefinition + "(" + featureName + ") is not supported.");
        }

        generatedFeatures.put(featureDefinition, output);
        return output;
    }

    public static String readSomethingFromOpenCLFile(String model_filename, String some_key, String default_value) {
        // Translated from https://github.com/haesleinhuepf/apoc/blob/409476ee34a6a03f5f9911d06ee7e1c41eb4d8b9/apoc/_utils.py#L103
        try {
            List<String> lines = Files.readAllLines(Paths.get(model_filename));

            boolean consider = false;
            int count = 0;
            for (String line : lines) {
                if (line.contains("/*")) {
                    consider = true;
                    continue;
                }
                if (consider) {
                    if (line.startsWith(some_key)) {
                        return line.replace(some_key, "").replace("\n","");
                    }
                }
                if (line.contains("*/")) {
                    break;
                }
                if (count > 25) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return default_value;
    }

}
