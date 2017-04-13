package com.github.diplombmstu.converter3d;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO add comment
 */
public class Converter3d
{

    private static final int MAX_DEPTH_VALUE = 255;
    private static final int MAX_SHIFT = 70;

    public void convert(Path input, Path depthMap, Path leftEye, Path rightEye) throws Exception
    {
        BufferedImage inputImage = Utils.readImage(input);
        BufferedImage depthMapImage = Utils.readImage(depthMap);

        int inputWidth = inputImage.getWidth();
        int inputHeight = inputImage.getHeight();

        if (inputHeight != depthMapImage.getHeight() && inputWidth != depthMapImage.getWidth())
            throw new Exception("Images have different sizes.");

        BufferedImage leftImage = Utils.createSameSizeImage(inputImage);
        BufferedImage rightImage = Utils.createSameSizeImage(inputImage);

        Map<Double, List<Pair<Integer, Integer>>> depths = findDepths(inputImage, depthMapImage);
        Set<Double> sortedKeys = depths.keySet()
                .stream()
                .sorted((o1, o2) -> o1 > o2 ? 1 : (o1.equals(o2) ? 0 : -1))
                .collect(Collectors.toSet());

        for (Double sortedKey : sortedKeys)
        {
            for (Pair<Integer, Integer> coordinates : depths.get(sortedKey))
                shift(inputImage, depthMapImage, leftImage, rightImage, coordinates);
        }

        ImageIO.write(leftImage, "png", leftEye.toFile());
        ImageIO.write(rightImage, "png", rightEye.toFile());
    }

    private void shift(BufferedImage inputImage,
                       BufferedImage depthMapImage,
                       BufferedImage leftImage,
                       BufferedImage rightImage,
                       Pair<Integer, Integer> coordinates)
    {
        int inputWidth = inputImage.getWidth();

        int i = coordinates.getKey();
        int j = coordinates.getValue();

        double depthRed = new Color(depthMapImage.getRGB(i, j)).getRed();

        int inputColor = inputImage.getRGB(i, j);
        double depthValue = depthRed / MAX_DEPTH_VALUE;

        int di = (int) (depthValue * MAX_SHIFT);

        int lx = i + di;
        int rx = i - di;

        if (lx < inputWidth)
        {
            leftImage.setRGB(lx, j, inputColor);
            leftImage.setRGB(i, j, rx >= 0 ? inputImage.getRGB(rx, j) : inputImage.getRGB(i, j));
        }

        if (rx >= 0)
        {
            rightImage.setRGB(rx, j, inputColor);
            rightImage.setRGB(i, j, lx < inputWidth ? inputImage.getRGB(lx, j) : inputImage.getRGB(i, j));
        }
    }

    private Map<Double, List<Pair<Integer, Integer>>> findDepths(BufferedImage inputImage, BufferedImage depthMapImage)
    {
        int inputWidth = inputImage.getWidth();
        int inputHeight = inputImage.getHeight();

        Map<Double, List<Pair<Integer, Integer>>> result = new HashMap<>();

        for (int i = 0; i < inputWidth; i++)
        {
            for (int j = 0; j < inputHeight; j++)
            {
                double depthRed = new Color(depthMapImage.getRGB(i, j)).getRed();

                if (!result.containsKey(depthRed))
                {
                    ArrayList<Pair<Integer, Integer>> values = new ArrayList<>();
                    values.add(new Pair<>(i, j));
                    result.put(depthRed, values);
                }
                else
                    result.get(depthRed).add(new Pair<>(i, j));
            }
        }

        return result;
    }
}
