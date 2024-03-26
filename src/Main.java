import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        PrincipalJFrame frame1 = new PrincipalJFrame();
        frame1.setContentPane(frame1.plane);
        frame1.setTitle("Calculadora de SubRedes");
        frame1.setSize(300, 200);
        frame1.setVisible(true);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (pantalla.width - frame1.getWidth()) / 2;
        int y = (pantalla.height - frame1.getHeight()) / 2;
        frame1.setLocation(x, y);
    }
}