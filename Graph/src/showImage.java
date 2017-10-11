import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;

public class showImage extends JFrame {
	public static final long serialVersionUID = 1L;
    private JLabel jlb = new JLabel();
    private ImageIcon image;
    private int width, height;
private int a = 10;
    public showImage(String imgName) {
        try {
            a
        	BufferedImage sourceImage = ImageIO.read(new FileInputStream(imgName));
        	with = sourceImage.getWidth() / 3;
            height = sourceImage.getHeight() / 3;
//            System.out.println(width);
//            System.out.println(height);
        }catch (FileNotFoundException e) {
			return;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
        this.setSize(width, height);
        this.setLayout(null);

        image = new ImageIcon("DotGraph.jpg");
        Image img = image.getImage();
        img = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        image.setImage(img);
        jlb.setIcon(image);

        this.add(jlb);
        jlb.setSize(width, height);
        this.setVisible(true);
    }
}
