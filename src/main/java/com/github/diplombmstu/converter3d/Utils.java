package com.github.diplombmstu.converter3d;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * TODO add comment
 */
public class Utils
{
    public static BufferedImage readImage(Path imagePath) throws IOException
    {
        try (FileInputStream fileInputStream = new FileInputStream(imagePath.toFile());
             FileChannel channel = fileInputStream.getChannel();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
        {
            channel.transferTo(0, channel.size(), Channels.newChannel(byteArrayOutputStream));
            return ImageIO.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        }
    }

    static BufferedImage createSameSizeImage(BufferedImage inputImage)
    {
        return new BufferedImage(inputImage.getWidth(),
                                 inputImage.getHeight(),
                                 BufferedImage.TYPE_INT_ARGB);
    }
}
