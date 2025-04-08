import javax.swing.*;
import java.awt.*;

public class PanelManejadorMapa extends JPanel {
    private PanelMapa panelMapa;
    private JPanel panelBotones;
    // Banderas para rastrear la ejecución de cada algoritmo
    private boolean bfsEjecutado = false, dfsEjecutado = false, dijkstraEjecutado = false,
                    floydEjecutado = false, primEjecutado = false, kruskalEjecutado = false;
                    
    // Referencia al botón Terminar Ejercicio para activarlo
    private JButton btnTerminarEjercicio;
    
    public PanelManejadorMapa() {
        setLayout(new BorderLayout());
        panelMapa = new PanelMapa();
        panelBotones = crearPanelBotones();

        // Configurar JSplitPane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            panelMapa,
            panelBotones
        );
        splitPane.setResizeWeight(0.9); // 90% mapa, 10% botones
        splitPane.setDividerLocation(0.9);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // Asignar action listeners a los botones
        asignarEventosBotones();
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnNuevoGrafo = crearBotonEstilizado("Crear nuevo bosque");
        JButton btnBFS = crearBotonEstilizado("Ejecutar BFS");
        JButton btnDFS = crearBotonEstilizado("Ejecutar DFS");
        JButton btnDijkstra = crearBotonEstilizado("Ejecutar Dijkstra");
        JButton btnFloyd = crearBotonEstilizado("Ejecutar Floyd");
        JButton btnPrim = crearBotonEstilizado("Ejecutar Prim");
        JButton btnKruskal = crearBotonEstilizado("Ejecutar Kruskal");
        JButton btnCentros = crearBotonEstilizado("Poner centros de recolección");
        btnTerminarEjercicio = crearBotonEstilizado("Terminar ejercicio");
        btnTerminarEjercicio.setEnabled(false); // Inhabilitado hasta que se ejecuten todos los algoritmos

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
        panel.add(btnCentros);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnTerminarEjercicio);

        return panel;
    }

    private void asignarEventosBotones() {
        // Asignar eventos a los botones según su texto
        getBoton("Crear nuevo bosque").addActionListener(e -> {
            panelMapa.ejecutarNuevoGrafo();
        });
        getBoton("Ejecutar BFS").addActionListener(e -> {
            panelMapa.ejecutarBFS();
            bfsEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar DFS").addActionListener(e -> {
            panelMapa.ejecutarDFS();
            dfsEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar Dijkstra").addActionListener(e -> {
            panelMapa.ejecutarDijkstra();
            dijkstraEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar Floyd").addActionListener(e -> {
            panelMapa.ejecutarFloyd();
            floydEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar Prim").addActionListener(e -> {
            panelMapa.ejecutarPrim();
            primEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Ejecutar Kruskal").addActionListener(e -> {
            panelMapa.ejecutarKruskal();
            kruskalEjecutado = true;
            activarTerminarSiCorresponde();
        });
        getBoton("Poner centros de recolección").addActionListener(e -> {
            panelMapa.toggleCentros();
        });
        btnTerminarEjercicio.addActionListener(e -> {
            panelMapa.terminarEjercicio();
        });
    }

    private void activarTerminarSiCorresponde() {
        // Si se han ejecutado todos los algoritmos, habilitar el botón Terminar Ejercicio
        if (bfsEjecutado && dfsEjecutado && dijkstraEjecutado &&
            floydEjecutado && primEjecutado && kruskalEjecutado) {
            btnTerminarEjercicio.setEnabled(true);
        }
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        return boton;
    }

    private JButton getBoton(String texto) {
        for (Component comp : panelBotones.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals(texto)) {
                return (JButton) comp;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        JFrame ventana = new JFrame("Grafo Interactivo");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);
        ventana.add(new PanelManejadorMapa());
        ventana.setVisible(true);
    }
        
}