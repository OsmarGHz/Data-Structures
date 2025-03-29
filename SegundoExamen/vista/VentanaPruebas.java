package vista;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class VentanaPruebas extends JFrame {

    public VentanaPruebas() {
        setTitle("Ventana pruebas");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        PanelMapa panelMapa = new PanelMapa();
        add(panelMapa);
        setVisible(true);
    }
    public static void main(String[] args) {
        //VentanaPruebas ventanaPruebas = new VentanaPruebas();
        new VentanaPruebas();
    }
}
