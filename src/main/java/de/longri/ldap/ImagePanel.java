package de.longri.ldap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImagePanel extends JPanel {
    private BufferedImage image;

    public ImagePanel(BufferedImage image) {
        this.image = image;
    }

    public ImagePanel(File imageFile) {
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException ex) {
            // handle exception...
        }
    }

    public ImagePanel(InputStream imageStream) {
        try {
            image = ImageIO.read(imageStream);
        } catch (IOException ex) {
            // handle exception...
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this); // see javadoc for more info on the parameters
    }
}
