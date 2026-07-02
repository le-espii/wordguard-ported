/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 */
package com.sk89q.worldedit.util.asset.holder;

import com.google.common.annotations.Beta;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

@Beta
public class ImageHeightmap {
    private final BufferedImage image;
    private BufferedImage resizedImage;
    private int lastSize = -1;

    public ImageHeightmap(BufferedImage image) {
        this.image = image;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double getHeightAt(int x, int y, int size) {
        int rgb;
        if (size != this.lastSize || this.resizedImage == null) {
            this.resizedImage = new BufferedImage(size, size, 1);
            Graphics2D graphic = null;
            try {
                graphic = this.resizedImage.createGraphics();
                graphic.drawImage(this.image, 0, 0, size, size, null);
            }
            finally {
                if (graphic != null) {
                    graphic.dispose();
                }
            }
            this.lastSize = size;
        }
        if ((rgb = this.resizedImage.getRGB(x, y = this.resizedImage.getHeight() - 1 - y)) == 0) {
            return 0.0;
        }
        int red = rgb >>> 16 & 0xFF;
        int green = rgb >>> 8 & 0xFF;
        int blue = rgb & 0xFF;
        return (double)(red + blue + green) / 3.0 / 255.0;
    }
}

