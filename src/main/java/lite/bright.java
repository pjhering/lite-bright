package lite;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class bright {
    
    private static final Map<Color, Character> map = new HashMap<>();
    
    public static void main(String[] args) {
        
        for(String path : args) {
            
            File file = new File(path);
            
            if(file.exists()) {
                
                info("opening " + file.getName());
                
                try {
                    
                    BufferedImage raw = ImageIO.read(file);
                    BufferedImage image = new BufferedImage(raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_INT_RGB);
                    image.getGraphics().drawImage(raw, 0, 0, null);
                    
                    if(image != null) {
                        
                        process(file, image);
                        
                    } else {
                        
                        error("unable to open " + file.getName());
                    }
                } catch(Exception ex) {
                    
                    error(ex.getMessage());
                }
                
            } else {
                
                warn("unable to find file: " + file.getAbsolutePath());
                info("skipping " + file.getName());
            }
        }
    }
    
    private static void process(File file, BufferedImage image) {
        
        map.clear();
        char current = 'A';
        
        StringBuilder text = new StringBuilder();
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        info("size: " + width + "x" + height);
        info("total pixels: " + (width * height));
        Raster raster = image.getData();
        
        int[] p = new int[4];
        
        for(int y = 0; y < height; y++) {
            
            for(int x = 0; x < width; x++) {
                
                if((x + 1) % 10 == 0) {
                    
                    text.append(" ");
                }
                
                raster.getPixel(x, y, p);
                Color c = new Color(p[0], p[1], p[2]);
                
                if(!map.containsKey(c)) {
                    
                    map.put(c, current);
                    current++;
                }
                
                text.append(map.get(c));
            }
            
            if((y + 1) % 2 == 0) {
                
                text.append("\n");
            }
            
            text.append("\n");
        }
        
        info("found " + map.size() + " unique colors");
        
        try {
            
            File textFile = new File(file.getName() + ".txt");
            info("creating " + textFile.getAbsolutePath());
            FileWriter out = new FileWriter(textFile);
            index(out);
            out.write(text.toString());
            out.flush();
            out.close();
            
        } catch(IOException ex) {
            
            error(ex.toString());
        }
    }
    
    private static void index(FileWriter out) throws IOException {
        
        List<String> list = new ArrayList<>();
        
        for(Color c : map.keySet()) {
            
            Character key = map.get(c);
            list.add(key + " " + hexify(c));
        }
        
        Collections.sort(list);
        
        for(String s : list) {
            out.write(s);
            out.write("\n");
        }
        
        out.write("\n");
    }
    
    private static String hexify(Color color) {
        
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    private static void info(String message) {
        
        out.print("INFO    | ");
        out.println(message);
    }
    
    private static void warn(String message) {
        
        out.print("WARNING | ");
        out.println(message);
    }
    
    private static void error(String message) {
        
        out.print("ERROR   | ");
        out.println(message);
    }
}
