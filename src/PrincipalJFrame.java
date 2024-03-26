import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PrincipalJFrame extends JFrame {
    protected JPanel plane;
    private JTextField IpField;
    private JTextField SubMaskField;
    private JButton calcularButton;

    public PrincipalJFrame() {
        calcularButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String IP = IpField.getText().toString();
                String mascaraSubred = SubMaskField.getText().toString();

                if (IP.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "ERROR!: Ingresa la ip en el campo IP", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (mascaraSubred.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "ERROR!: Ingresa la SubMascara en el campo SubMask", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!IP.matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")) {
                    JOptionPane.showMessageDialog(null, "ERROR!: La dirección IP ingresada no es válida.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }else if (!mascaraSubred.matches("^255\\.(0|128|192|224|240|248|252|254|255)\\." +
                        "(0|128|192|224|240|248|252|254|255)\\.(0|128|192|224|240|248|252|254|255)$")) {
                    JOptionPane.showMessageDialog(null, "ERROR!: La submáscara de red ingresada no es válida.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    programa();
                }
            }
        });
    }

    public void programa() {
        String IP = IpField.getText().toString();
        String mascaraSubred = SubMaskField.getText().toString();
        String claseDireccion = obtenerClaseDireccion(IP);
        List<String> Submask = calcularSubred(IP, mascaraSubred);
        List<String> rango = calcularRangoIPDisponible(IP, Submask.get(1));
        try {
            JOptionPane.showMessageDialog(null, "La clase de dirección IP es: " + claseDireccion
                    + "\nMascaras de red posibles: " + obtenerMascarasPosibles(claseDireccion)
                    + "\nDirección de red: " + Submask.get(0)
                    + "\nDirección de Broadcast: " + Submask.get(1)
                    + "\nRango de direcciones IP disponibles: " + rango.get(0) + " - " + rango.get((rango.size() - 1)));

        } catch (IllegalArgumentException ex) {
            String mensaje = ex.getMessage().toString();
            JOptionPane.showMessageDialog(null, "ERROR!: " + mensaje);
        }
    }

    public static String obtenerClaseDireccion(String ip) {
        try {
            String[] octetos = ip.split("\\.");
            int primeraOcteto = Integer.parseInt(octetos[0]);

            if (1 <= primeraOcteto && primeraOcteto <= 126) {
                return "A";
            } else if (128 <= primeraOcteto && primeraOcteto <= 191) {
                return "B";
            } else if (192 <= primeraOcteto && primeraOcteto <= 223) {
                return "C";
            } else {
                return null;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Error: El formato de la dirección IP es incorrecto.");
        }
    }

    public static List<String> obtenerMascarasPosibles(String clase) {
        List<String> mascaras = new ArrayList<>();

        if (clase.equals("A")) {
            mascaras.add("8");
            mascaras.add("16");
            mascaras.add("24");
        } else if (clase.equals("B")) {
            mascaras.add("16");
            mascaras.add("24");
        } else if (clase.equals("C")) {
            mascaras.add("24");
        }

        return mascaras;
    }

    public List<String> calcularSubred(String direccionIP, String mascaraSubred) {
        try {
            // Convertir la dirección IP y la máscara de subred a bytes
            byte[] direccionIPBytes = InetAddress.getByName(direccionIP).getAddress();
            byte[] mascaraSubredBytes = InetAddress.getByName(mascaraSubred).getAddress();

            // Calcular la dirección de red
            byte[] direccionRedBytes = new byte[direccionIPBytes.length];
            for (int i = 0; i < direccionIPBytes.length; i++) {
                direccionRedBytes[i] = (byte) (direccionIPBytes[i] & mascaraSubredBytes[i]);
            }
            InetAddress direccionRed = InetAddress.getByAddress(direccionRedBytes);

            // Calcular la dirección de broadcast
            byte[] direccionBroadcastBytes = new byte[direccionIPBytes.length];
            for (int i = 0; i < direccionIPBytes.length; i++) {
                direccionBroadcastBytes[i] = (byte) ((direccionIPBytes[i] & mascaraSubredBytes[i]) | (~mascaraSubredBytes[i] & 0xFF));
            }
            InetAddress direccionBroadcast = InetAddress.getByAddress(direccionBroadcastBytes);

            // Imprimir los resultados

            List<String> resultado = new ArrayList<>();
            resultado.add(direccionRed.getHostAddress());
            resultado.add(direccionBroadcast.getHostAddress());

            return resultado;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Error: Dirección IP o máscara de subred no válida.");
        } catch (UnknownHostException ex2) {
            throw new RuntimeException(ex2);
        }
    }

    public static List<String> calcularRangoIPDisponible(String direccionRed, String direccionBroadcast) {
        List<String> rangoIPDisponible = new ArrayList<>();
        try {
            InetAddress direccionRedIP = InetAddress.getByName(direccionRed);
            InetAddress direccionBroadcastIP = InetAddress.getByName(direccionBroadcast);

            // Convertir direcciones IP a bytes
            byte[] direccionRedBytes = direccionRedIP.getAddress();
            byte[] direccionBroadcastBytes = direccionBroadcastIP.getAddress();

            // Incrementar la dirección de red para obtener la primera dirección IP disponible
            byte[] direccionIP = direccionRedBytes.clone();
            for (int i = direccionRedBytes.length - 1; i >= 0; i--) {
                if (direccionBroadcastBytes[i] > direccionRedBytes[i]) {
                    direccionIP[i]++; // Incrementar la dirección de red en 1
                    break;
                }
            }

            // Construir el rango de direcciones IP disponibles
            StringBuilder direccionActual = new StringBuilder();
            while (!isEqual(direccionIP, direccionBroadcastBytes)) {
                direccionActual.setLength(0); // Limpiar el StringBuilder
                for (int i = 0; i < direccionIP.length; i++) {
                    direccionActual.append(direccionIP[i] & 0xFF); // Convertir byte sin signo a int
                    if (i < direccionIP.length - 1) {
                        direccionActual.append(".");
                    }
                }
                rangoIPDisponible.add(direccionActual.toString());

                // Incrementar la dirección IP
                incrementarDireccionIP(direccionIP);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return rangoIPDisponible;
    }

    private static void incrementarDireccionIP(byte[] direccionIP) {
        for (int i = direccionIP.length - 1; i >= 0; i--) {
            if ((direccionIP[i] & 0xFF) < 255) {
                direccionIP[i]++;
                break;
            } else {
                direccionIP[i] = 0;
            }
        }
    }

    private static boolean isEqual(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }
}
