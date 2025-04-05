package vista;

import javax.swing.*;

import controlador.BotonesControlador;
import modelo.grafo.GeneradorGrafo;

import java.awt.*;

public class PanelManejadorMapa extends JPanel {
    private PanelMapa panelMapa;
    private JPanel panelBotones;

    public PanelManejadorMapa() {
        // Configuración del layout principal
        setLayout(new BorderLayout());
        
        // 1. Inicializar componentes
        panelMapa = new PanelMapa();
        panelBotones = crearPanelBotones();

        // 2. Configurar JSplitPane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            panelMapa,
            panelBotones
        );
        splitPane.setResizeWeight(0.9); // 90% mapa, 10% botones
        splitPane.setDividerLocation(0.9); // Posición inicial
        splitPane.setContinuousLayout(true); // Redibujo continuo al mover divisor

        // 3. Añadir al panel principal
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Botones de ejemplo con estilo
        JButton btnNuevoGrafo = crearBotonEstilizado("Nuevo Grafo");
        JButton btnBFS = crearBotonEstilizado("Ejecutar BFS");
        JButton btnDFS = crearBotonEstilizado("Ejecutar DFS");
        JButton btnDijkstra = crearBotonEstilizado("Ejecutar Dijkstra");
        JButton btnFloyd = crearBotonEstilizado("Ejecutar Floyd");
        JButton btnPrim = crearBotonEstilizado("Ejecutar Prim");
        JButton btnKruskal = crearBotonEstilizado("Ejecutar Kruskal");
        JButton btnCentrosR = crearBotonEstilizado("Mostrar CentrosR");

        // Espaciado entre botones
        panel.add(btnNuevoGrafo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnBFS);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnDFS);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnDijkstra);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnFloyd);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnPrim);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnKruskal);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnCentrosR);

        return panel;
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Altura fija
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBackground(new Color(70, 130, 180)); // SteelBlue
        boton.setForeground(Color.WHITE);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return boton;
    }

    // Métodos para acceder a los componentes desde el controlador
    public PanelMapa getPanelMapa() {
        return panelMapa;
    }

    public JButton getBoton(String nombre) {
        for (Component comp : panelBotones.getComponents()) {
            if (comp instanceof JButton && ((JButton)comp).getText().equals(nombre)) {
                return (JButton)comp;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Prueba BFS");
            GeneradorGrafo modelo = new GeneradorGrafo();
            PanelManejadorMapa panel = new PanelManejadorMapa();
            
            // Configurar controlador
            new BotonesControlador(panel, modelo);
            
            frame.add(panel);
            frame.setSize(1000, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}