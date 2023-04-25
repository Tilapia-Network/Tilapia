package com.boydti.fawe.object.brush.heightmap;

import com.boydti.fawe.object.IntegerPair;
import com.boydti.fawe.util.MainUtil;
import com.boydti.fawe.util.MathMan;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

public class ScalableHeightMap implements HeightMap {
    public int size2;
    public int size;

    public enum Shape {
        CONE,
        CYLINDER,
    }

    public ScalableHeightMap() {
        setSize(5);
    }

    public ScalableHeightMap(int size) {
        setSize(size);
    }

    @Override
    public void setSize(int size) {
        this.size = size;
        this.size2 = size * size;
    }

    @Override
    public double getHeight(int x, int z) {
        int dx = Math.abs(x);
        int dz = Math.abs(z);
        int d2 = dx * dx + dz * dz;
        if (d2 > size2) {
            return 0;
        }
        return Math.max(0, size - MathMan.sqrtApprox(d2));
    }

    public static ScalableHeightMap fromShape(Shape shape) {
        switch (shape) {
            default:
            case CONE:
                return new ScalableHeightMap();
            case CYLINDER:
                return new FlatScalableHeightMap();
        }
    }

    public static ScalableHeightMap fromClipboard(Clipboard clipboard) {
        Vector dim = clipboard.getDimensions();
        byte[][] heightArray = new byte[dim.getBlockX()][dim.getBlockZ()];
        int minX = clipboard.getMinimumPoint().getBlockX();
        int minZ = clipboard.getMinimumPoint().getBlockZ();
        int minY = clipboard.getMinimumPoint().getBlockY();
        int maxY = clipboard.getMaximumPoint().getBlockY();
        int clipHeight = maxY - minY + 1;
        HashSet<IntegerPair> visited = new HashSet<>();
        for (Vector pos : clipboard.getRegion()) {
            IntegerPair pair = new IntegerPair(pos.getBlockX(), pos.getBlockZ());
            if (visited.contains(pair)) {
                continue;
            }
            visited.add(pair);
            int xx = pos.getBlockX();
            int zz = pos.getBlockZ();
            int highestY = minY;
            for (int y = minY; y <= maxY; y++) {
                pos.mutY(y);
                BaseBlock block = clipboard.getBlock(pos);
                if (block.getId() != 0) {
                    highestY = y + 1;
                }
            }
            int pointHeight = Math.min(255, (256 * (highestY - minY)) / clipHeight);
            int x = xx - minX;
            int z = zz - minZ;
            heightArray[x][z] = (byte) pointHeight;
        }
        return new ArrayHeightMap(heightArray);
    }

    public static ScalableHeightMap fromPNG(InputStream stream) throws IOException {
        BufferedImage heightFile = MainUtil.readImage(stream);
        int width = heightFile.getWidth();
        int length = heightFile.getHeight();
        Raster data = heightFile.getData();
        byte[][] array = new byte[width][length];
        double third = 1 / 3.0;
        double alphaInverse = 1 / 255.0;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                int pixel = heightFile.getRGB(x, z);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = (pixel >> 0) & 0xFF;
                int alpha = (pixel >> 24) & 0xFF;
                int intensity = (int) (alpha * ((red + green + blue) * third) * alphaInverse);
                array[x][z] = (byte) intensity;
            }
        }
        return new ArrayHeightMap(array);
    }
}
