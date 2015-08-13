package lite;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class temp {

    private static final Map<Color, Character> colormap = new HashMap<>();
    private static char currentChar = 'A';
    private static String[] previous;
    private static File outputRoot;

    public static void main(String[] args) {

        File root = getRoot();
        if (root == null) {

            return;
        }
        log("reading contents of " + root);

        outputRoot = new File(root, "output");
        outputRoot.mkdirs();

        File[] files = root.listFiles();
        log("found " + files.length + " files");

        for (File file : files) {

            try {

                if (!file.isDirectory()) {

                    process(file);
                }

            } catch (IOException ex) {

                report(ex);
            }
        }

        index();
    }

    private static void index() {

        File index = new File(outputRoot, "color-index.txt");

        try (FileWriter out = new FileWriter(index)) {
            
            List<String> list = new ArrayList<>();

            for (Color c : colormap.keySet()) {

                Character key = colormap.get(c);
                list.add(key + " " + hexify(c));
            }

            Collections.sort(list);

            for (String s : list) {
                
                out.write(s);
                out.write("\n");
            }
        } catch(Exception ex) {
            
            report(ex);
        }
    }

    private static String hexify(Color color) {

        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static void process(File file) throws IOException {

        log("opening " + file.getName());
        BufferedImage image = loadImage(file);

        if (image == null) {

            log("skipping " + file.getName());

        } else {

            String[] current = getText(image);
            File textFile = new File(outputRoot, file.getName() + ".txt");

            try (FileWriter out = new FileWriter(textFile)) {

                log("writing output to " + textFile.getAbsolutePath());
                if (previous == null) {

                    for (String line : current) {

                        out.write(line);
                        out.write("\n");
                    }
                } else {

                    // hmmm...
                }

                out.flush();
                previous = current;
            }
        }
    }

    private static String[] getText(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();
        String[] text = new String[height];
        Raster data = image.getData();
        int[] px = new int[4];

        for (int y = 0; y < height; y++) {

            StringBuilder buffer = new StringBuilder();

            for (int x = 0; x < width; x++) {

                data.getPixel(x, y, px);
                Color color = new Color(px[0], px[1], px[2]);

                if (!colormap.containsKey(color)) {

                    colormap.put(color, currentChar);
                    currentChar++;
                }

                Character c = colormap.get(color);
                buffer.append(c);
            }

            text[y] = buffer.toString();
        }

        return text;
    }

    private static BufferedImage loadImage(File file) {

        BufferedImage raw = null;
        try {

            raw = ImageIO.read(file);

        } catch (Exception ex) {

            report(ex);
        }

        if (raw == null) {

            log(file.getName() + " does not appear to be a valid image file");
            return null;
        }

        BufferedImage cooked = new BufferedImage(raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_INT_RGB);
        cooked.getGraphics().drawImage(raw, 0, 0, null);

        return cooked;
    }

    private static File getRoot() {

        return new File("/home/scarecrow/NetBeansProjects/lite-bright/src/test/resources/");
//        JFileChooser fc = new JFileChooser();
//        fc.setAcceptAllFileFilterUsed(false);
//        fc.setMultiSelectionEnabled(false);
//        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
//        
//        int val = fc.showOpenDialog(null);
//        
//        return fc.getSelectedFile();
    }

    private static void log(Object obj) {

        System.out.print(">>");
        System.out.println(obj);
    }

    private static void report(Exception ex) {

        log("ERROR: " + ex.toString());
    }
}
