package front;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import background.filter;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Stack;

public class MainForm {
    private JButton load;
    private JPanel panel1;
    private JLabel photoLbl;
    private JButton undoButton;
    private JButton redoButton;
    private JButton modifySmoothnessButton;
    private JButton modifyExposureButton;
    private JButton blackAndWhiteFilterButton;
    private JSlider slider1;
    private JButton saveChangesButton;
    private Stack<BufferedImage> undo;
    private Stack<BufferedImage> redo;


    public MainForm() {
        slider1.setMinimum(0);
        slider1.setMaximum(100);
        undo = new Stack<BufferedImage>();
        redo = new Stack<BufferedImage>();

        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("png","png");
                FileNameExtensionFilter bmpFilter = new FileNameExtensionFilter("bmp","bmp");
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(pngFilter);
                fileChooser.setFileFilter(bmpFilter);
                int result = fileChooser.showOpenDialog(null);
                String filename;
                try {
                    if (result == JFileChooser.APPROVE_OPTION) {
                        filename = fileChooser.getSelectedFile().getPath();
                        BufferedImage p = null;
                        p = ImageIO.read(fileChooser.getSelectedFile());
                        ImageIcon imageIcon = new ImageIcon(p);
                        photoLbl.setIcon(imageIcon);
                        modifyExposureButton.setEnabled(true);
                        modifySmoothnessButton.setEnabled(true);
                        blackAndWhiteFilterButton.setEnabled(true);
                    }
                    else if (result == JFileChooser.ERROR_OPTION)
                        JOptionPane.showMessageDialog(null, "An error occurred.");
                }
                catch (Exception E) {
                    JOptionPane.showMessageDialog(null,
                            E.getMessage(),
                            "ERROR",
                            JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        blackAndWhiteFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon = (ImageIcon)photoLbl.getIcon();
                BufferedImage img = (BufferedImage)(icon.getImage());
                undo.push(img);
                filter blackAndWhite = new filter();
                img = blackAndWhite.convertToBW(img);
                icon = new ImageIcon(img);
                photoLbl.setIcon(icon);
                undoButton.setEnabled(true);
                saveChangesButton.setEnabled(true);
            }
        });

        saveChangesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(null);
                String filename;
                try {
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File output = fileChooser.getSelectedFile();
                        ImageIcon icon = (ImageIcon) photoLbl.getIcon();
                        BufferedImage img = (BufferedImage) (icon.getImage());
                        ImageIO.write(img, "png", output);
                        saveChangesButton.setEnabled(false);
                    }
                    else if (result == JFileChooser.ERROR_OPTION)
                        JOptionPane.showMessageDialog(null, "An error occurred.");
                }
                catch (Exception E) {
                    JOptionPane.showMessageDialog(null,
                            E.getMessage(),
                            "ERROR",
                            JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        modifyExposureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon = (ImageIcon) photoLbl.getIcon();
                BufferedImage img = (BufferedImage) (icon.getImage());
                undo.push(img);
                BufferedImage picture2= new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
                slider1.setEnabled(true);
                int beta = slider1.getValue();
                for (int i = 0; i < img.getHeight(); i++) {
                    for (int j = 0; j < img.getWidth(); j++) {
                        Color c=new Color(img.getRGB(j,i));

                        int r = c.getRed() + beta;
                        int b = c.getBlue() + beta;
                        int g = c.getGreen() + beta;
                        if (r >= 256)
                            r = 255;
                        else if (r < 0)
                            r = 0;

                        if (g >= 256)
                            g = 255;
                        else if (g < 0)
                            g = 0;

                        if (b >= 256)
                            b = 255;
                        else if (b < 0)
                            b = 0;

                        picture2.setRGB(j, i,new Color(r,g,b).getRGB());
                    }
                }
                ImageIcon imageIcon = new ImageIcon(picture2);
                photoLbl.setIcon(imageIcon);
                undoButton.setEnabled(true);
            }
        });

        modifySmoothnessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slider1.setEnabled(true);
                ImageIcon icon = (ImageIcon) photoLbl.getIcon();
                BufferedImage img = (BufferedImage) (icon.getImage());
                undo.push(img);
                int[][][] rgb = new int[3][img.getHeight()][img.getWidth()];
                for (int i = 0; i < img.getHeight(); i++) {
                    for (int j = 0; j < img.getWidth(); j++) {
                        Color c = new Color(img.getRGB(j,i));
                        rgb[0][i][j] = c.getRed();
                        rgb[1][i][j] = c.getGreen();
                        rgb[2][i][j] = c.getBlue();
                    }
                }

                for (int i = 1; i < img.getHeight()-1; i++) {
                    for (int j = 1; j < img.getWidth()-1; j++) {
                        int r = rgb[0][i-1][j-1] + rgb[0][i-1][j] + rgb[0][i-1][j+1] +
                                rgb[0][i][j-1] + rgb[0][i][j] + rgb[0][i][j+1] +
                                rgb[0][i+1][j-1] + rgb[0][i+1][j] + rgb[0][i+1][j+1];

                        int g = rgb[1][i-1][j-1] + rgb[1][i-1][j] + rgb[1][i-1][j+1] +
                                rgb[1][i][j-1] + rgb[1][i][j] + rgb[1][i][j+1] +
                                rgb[1][i+1][j-1] + rgb[1][i+1][j] + rgb[1][i+1][j+1];

                        int b = rgb[2][i-1][j-1] + rgb[2][i-1][j] + rgb[2][i-1][j+1] +
                                rgb[2][i][j-1] + rgb[2][i][j] + rgb[2][i][j+1] +
                                rgb[2][i+1][j-1] + rgb[2][i+1][j] + rgb[2][i+1][j+1];
                        Color color = new Color((r/9)%255,(g/9)%255,(b/9)%255);
                        img.setRGB(j,i,color.getRGB());
                    }
                }
                ImageIcon imageIcon = new ImageIcon(img);
                photoLbl.setIcon(imageIcon);
                undoButton.setEnabled(true);
            }
        });

        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon = (ImageIcon) photoLbl.getIcon();
                ImageIcon imageIcon;
                BufferedImage img = (BufferedImage) (icon.getImage());
                undo.push(img);
                if (!redo.empty()) {
                    imageIcon = new ImageIcon(redo.pop());
                    photoLbl.setIcon(imageIcon);
                }
                else JOptionPane.showMessageDialog(null,"can't redo no more");

            }
        });

        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon = (ImageIcon) photoLbl.getIcon();
                ImageIcon imageIcon;
                BufferedImage img = (BufferedImage) (icon.getImage());
                redo.push(img);
                if (!undo.empty()) {
                    imageIcon = new ImageIcon(undo.pop());
                    photoLbl.setIcon(imageIcon);
                    redoButton.setEnabled(true);
                }
                else JOptionPane.showMessageDialog(null,"can't undo no more");
            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("image toolbox");
        frame.setContentPane(new MainForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
