package com.msg.geneticimage.gfx;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.msg.geneticimage.interfaces.Cons;
import com.msg.geneticimage.tools.DenseDoubleVector;
import com.msg.geneticimage.tools.DoubleVector;
import com.msg.geneticimage.tools.EuclidianDistance;
import com.msg.geneticimage.tools.ImageReader;
import com.msg.geneticimage.tools.MeanShiftClustering;

public class PictureSegmentation {

  private static final EuclidianDistance DISTANCE = new EuclidianDistance();
  
  public PictureSegmentation() throws Exception {
	  /* Load compare image. */
	  BufferedImage readImg = ImageIO.read(this.getClass().getClassLoader().getResource(Cons.IMAGE_PATH));

    DoubleVector[] luv = ImageReader.readImageAsLUV(readImg);
    DoubleVector[] rgb = ImageReader.readImageAsRGB(readImg);
    List<DoubleVector> cluster = MeanShiftClustering.cluster(
    								Arrays.asList(luv), 20, 20, 50, true);
    System.out.println(cluster.size());
    List<Color> colors = pick(cluster.size());
    final JFrame frame = new JFrame("Segmentation");
    final JPanel panel = new JPanel();
    frame.setLayout(new FlowLayout());
    frame.setLocation(500, 250);
    frame.add(panel);
    final JLabel pictureLabel = new JLabel(new ImageIcon(getImage(readImg, cluster, luv, colors)));
    panel.add(pictureLabel);
    final JLabel pictureLabel2 = new JLabel(new ImageIcon(getRawImage(readImg, rgb)));
    panel.add(pictureLabel2);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
	  try {
		new PictureSegmentation();
	} catch (Exception e) {
		e.printStackTrace();
	}
  }

  private static BufferedImage getRawImage(BufferedImage readImg,
      DoubleVector[] rgb) {
    BufferedImage img = new BufferedImage(readImg.getWidth(),
        readImg.getHeight(), BufferedImage.TYPE_INT_RGB);
    int column = 0;
    int row = 0;
    for (int i = 0; i < rgb.length; i++) {
      DoubleVector rgbVector = rgb[i];
      Color col = new Color((int) rgbVector.get(0), (int) rgbVector.get(1),
          (int) rgbVector.get(2));
      img.setRGB(column, row, col.getRGB());
      column++;
      if (column == readImg.getWidth()) {
        column = 0;
        row++;
      }
    }
    return img;
  }

  private static BufferedImage getImage(BufferedImage readImg,
      List<DoubleVector> cluster, DoubleVector[] luv, List<Color> colors) {
    // assign each cluster a shade, thus rerendering the image with each
    // pixel classified to its nearest center
    BufferedImage img = new BufferedImage(readImg.getWidth(),
        readImg.getHeight(), BufferedImage.TYPE_INT_RGB);
    int column = 0;
    int row = 0;
    for (int i = 0; i < luv.length; i++) {
      int nearest = nearest(luv[i], cluster);
      img.setRGB(column, row, colors.get(nearest).getRGB());
      column++;
      if (column == readImg.getWidth()) {
        column = 0;
        row++;
      }
    }

    return img;
  }

  private static int nearest(DoubleVector doubleVector, List<DoubleVector> cluster) {
    DenseDoubleVector vector = new DenseDoubleVector(cluster.size());
    for (int i = 0; i < vector.getLength(); i++) {
      vector.set(i, DISTANCE.measureDistance(doubleVector, cluster.get(i)));
    }
    return vector.minIndex();
  }

  private static List<Color> pick(int num) {
    List<Color> colors = new ArrayList<>();
    if (num < 2)
      return colors;
    float dx = 1.0f / (num - 1);
    for (int i = 0; i < num; i++) {
      colors.add(get(i * dx));
    }
    return colors;
  }

  private static Color get(float x) {
    float r = 0.0f;
    float g = 0.0f;
    float b = 1.0f;
    if (x >= 0.0f && x < 0.2f) {
      x = x / 0.2f;
      r = 0.0f;
      g = x;
      b = 1.0f;
    } else if (x >= 0.2f && x < 0.4f) {
      x = (x - 0.2f) / 0.2f;
      r = 0.0f;
      g = 1.0f;
      b = 1.0f - x;
    } else if (x >= 0.4f && x < 0.6f) {
      x = (x - 0.4f) / 0.2f;
      r = x;
      g = 1.0f;
      b = 0.0f;
    } else if (x >= 0.6f && x < 0.8f) {
      x = (x - 0.6f) / 0.2f;
      r = 1.0f;
      g = 1.0f - x;
      b = 0.0f;
    } else if (x >= 0.8f && x <= 1.0f) {
      x = (x - 0.8f) / 0.2f;
      r = 1.0f;
      g = 0.0f;
      b = x;
    }
    return new Color(r, g, b);
  }

}
