package net.haesleinhuepf.clijx.apoc;

import ij.measure.ResultsTable;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.plugins.StatisticsOfLabelledPixels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static net.haesleinhuepf.clijx.apoc.Utilities.preparseFeatures;

class LabelFeatureGenerator {

    private interface Computer {
        void compute();
    }

    private CLIJ2 clij2;
    private ClearCLBuffer input;
    private ClearCLBuffer label_map;
    private int number_of_labels = -1;
    private ClearCLBuffer touch_matrix = null;
    private ClearCLBuffer distance_matrix = null;
    private ClearCLBuffer pointlist = null;
    private ResultsTable statistics_of_labels = null;

    private ClearCLBuffer measurement_vector = null;
    private ClearCLBuffer temp_vector = null;
    private double numericParameter = 0;


    private final static HashMap<String, LabelFeatureGenerator.Computer> computers = new HashMap<>();

    private String[] feature_definition_keys = null;
    private ArrayList<ClearCLBuffer> result = null;

    LabelFeatureGenerator(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer label_map, String featureDefinitions) {

        computers.put("area", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.PIXEL_COUNT.toString());
        });

        computers.put("min_intensity", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.MINIMUM_INTENSITY.toString());
        });
        computers.put("mean_intensity", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.MEAN_INTENSITY.toString());
        });
        computers.put("max_intensity", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.MAXIMUM_INTENSITY.toString());
        });
        computers.put("sum_intensity", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.SUM_INTENSITY.toString());
        });
        computers.put("standard_deviation_intensity", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.STANDARD_DEVIATION_INTENSITY.toString());
        });
        computers.put("mean_max_distance_to_centroid_ratio", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.MAX_MEAN_DISTANCE_TO_CENTROID_RATIO.toString());
        });
        computers.put("centroid_x", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.CENTROID_X.toString());
        });
        computers.put("centroid_y", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.CENTROID_Y.toString());
        });
        computers.put("centroid_z", () -> {
            clij2.pushResultsTableColumn(measurement_vector, getStatistics(), StatisticsOfLabelledPixels.STATISTICS_ENTRY.CENTROID_Z.toString());
        });
        computers.put("touching_neighbor_count", () -> {
            clij2.countTouchingNeighbors(touch_matrix, measurement_vector);
        });
        computers.put("average_distance_of_touching_neighbors", () -> {
            clij2.averageDistanceOfTouchingNeighbors(distance_matrix, touch_matrix, measurement_vector);
        });
        computers.put("average_distance_of_n_nearest_neighbors", () -> {
            clij2.averageDistanceOfNClosestPoints(distance_matrix, measurement_vector, numericParameter);
        });

        /*
        computers.put("average_touch_pixel_count", () -> {
            ClearCLBuffer touch_count_matrix2 = clij2.create(distance_matrix);
            clij2.generateTouchCountMatrix(label_map, touch_count_matrix2);

            ClearCLBuffer touch_count_matrix = clij2.create(distance_matrix.getWidth() - 1, distance_matrix.getHeight() - 1);
            ClearCLBuffer sum_vector = clij2.create(touch_count_matrix.getWidth(), 1, 1);
            clij2.sumYProjection(touch_count_matrix, sum_vector);
            ClearCLBuffer count_vector = clij2.create(touch_count_matrix.getWidth(), 1, 1);
            clij2.countTouchingNeighbors(touch_matrix, count_vector);
            clij2.divideImages(count_vector, sum_vector, measurement_vector);
            touch_count_matrix.close();
            touch_count_matrix2.close();
            sum_vector.close();
            count_vector.close();
        });
        */

        // ---------------------------------------------------------------------------------------------------------
        /*
        computers.put("local_maximum_average_distance_n_closest_neighbors=2", () -> {
            clij2.averageDistanceOfNClosestPoints(distance_matrix, temp_vector, numericParameter);
            clij2.maximumOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        computers.put("local_maximum_average_distance_of_touching_neighbors", () -> {
            clij2.averageDistanceOfTouchingNeighbors(distance_matrix, touch_matrix, temp_vector);
            clij2.maximumOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        computers.put("local_maximum_count_touching_neighbors", () -> {
            clij2.countTouchingNeighbors(touch_matrix, temp_vector);
            clij2.maximumOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        // ---------------------------------------------------------------------------------------------------------
        computers.put("local_minimum_average_distance_n_closest_neighbors=2", () -> {
            clij2.averageDistanceOfNClosestPoints(distance_matrix, temp_vector, numericParameter);
            clij2.minimumOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        computers.put("local_minimum_average_distance_of_touching_neighbors", () -> {
            clij2.averageDistanceOfTouchingNeighbors(distance_matrix, touch_matrix, temp_vector);
            clij2.minimumOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        computers.put("local_minimum_count_touching_neighbors", () -> {
            clij2.countTouchingNeighbors(touch_matrix, temp_vector);
            clij2.minimumOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        // ---------------------------------------------------------------------------------------------------------
        computers.put("local_mean_average_distance_n_closest_neighbors=2", () -> {
            clij2.averageDistanceOfNClosestPoints(distance_matrix, temp_vector, numericParameter);
            clij2.meanOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        computers.put("local_mean_average_distance_of_touching_neighbors", () -> {
            clij2.averageDistanceOfTouchingNeighbors(distance_matrix, touch_matrix, temp_vector);
            clij2.meanOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        computers.put("local_mean_count_touching_neighbors", () -> {
            clij2.countTouchingNeighbors(touch_matrix, temp_vector);
            clij2.meanOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        // ---------------------------------------------------------------------------------------------------------
        computers.put("local_standard_deviation_average_distance_n_closest_neighbors=2", () -> {
            clij2.averageDistanceOfNClosestPoints(distance_matrix, temp_vector, numericParameter);
            clij2.standardDeviationOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        computers.put("local_standard_deviation_average_distance_of_touching_neighbors", () -> {
            clij2.averageDistanceOfTouchingNeighbors(distance_matrix, touch_matrix, temp_vector);
            clij2.standardDeviationOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
        computers.put("local_standard_deviation_count_touching_neighbors", () -> {
            clij2.countTouchingNeighbors(touch_matrix, temp_vector);
            clij2.standardDeviationOfTouchingNeighbors(temp_vector, touch_matrix, measurement_vector);
        });
         */
        // ---------------------------------------------------------------------------------------------------------
        if (clij2 == null) {
            return;
        }

        this.clij2 = clij2;
        this.input = input;
        this.label_map = label_map;

        number_of_labels = (int) clij2.maximumOfAllPixels(label_map);

        touch_matrix = clij2.create(number_of_labels + 1, number_of_labels + 1);
        clij2.generateTouchMatrix(label_map, touch_matrix);

        pointlist = clij2.create(number_of_labels, label_map.getDimension());
        clij2.centroidsOfLabels(label_map, pointlist);

        distance_matrix = clij2.create(number_of_labels + 1, number_of_labels + 1);
        clij2.generateDistanceMatrix(pointlist, pointlist, distance_matrix);

        temp_vector = clij2.create(number_of_labels, 1, 1);


        // generate features
        feature_definition_keys = preparseFeatures(featureDefinitions);

        result = new ArrayList<>();
        for (String featureDefinition : feature_definition_keys) {
            ClearCLBuffer buffer = generateFeature(featureDefinition);
            result.add(buffer);
        }

        touch_matrix.close();
        pointlist.close();
        distance_matrix.close();
        temp_vector.close();
    }

    private ClearCLBuffer generateFeature(String featureDefinition) {
        System.out.println("Determining " + featureDefinition);

        String[] temp = featureDefinition.split("=");
        String featureName = temp[0].toLowerCase();
        String parameter = temp.length > 1 ? temp[1] : "0";
        numericParameter = Double.parseDouble(parameter);

        measurement_vector = clij2.create(number_of_labels, 1, 1);

        for (String key : computers.keySet()) {
            if (key.toLowerCase().compareTo(featureName) == 0) {
                computers.get(key).compute();
                clij2.print(measurement_vector);
                return measurement_vector;
            }
        }
        System.out.println("NONE");
        return null;
    }

    public String[] getFeatureList() {
        return feature_definition_keys.clone();
    }

    public ArrayList<ClearCLBuffer> getResult() {
        return result;
    }

    private ResultsTable getStatistics() {
        if (statistics_of_labels == null) {
            statistics_of_labels = new ResultsTable();
            clij2.statisticsOfBackgroundAndLabelledPixels(input, label_map, statistics_of_labels);
        }
        return statistics_of_labels;
    }

    private Set<String> getLabelPropertyNames() {
        return computers.keySet();
    }
}