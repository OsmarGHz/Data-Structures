import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class Vertice {
    int x, y, id;
    
    public Vertice(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
}

class Arista {
    Vertice origen, destino;
    
    public Arista(Vertice origen, Vertice destino) {
        this.origen = origen;
        this.destino = destino;
    }
}

public class EditorGrafo extends JPanel {
    private final ArrayList<Vertice> vertices = new ArrayList<>();
    private final ArrayList<Arista> aristas = new ArrayList<>();
    
    // Para manejo de deshacer (undo)
    private final Stack<Accion> acciones = new Stack<>();
    
    // Variables para selección y arrastre
    private Vertice verticeCandidato = null; // Vértice donde se hizo mousePressed
    private Vertice verticeSeleccionadoParaArista = null; // Vértice seleccionado para formar arista
    private int inicioX, inicioY; // Coordenadas del mouse al presionar
    private boolean esArrastre = false;
    private int contadorVertices = 0;
    private final int RADIO = 15;           // Radio del vértice (diámetro = 30)
    private final int UMBRAL_ARRASTRE = 5;    // Umbral para distinguir arrastre de click

    public EditorGrafo() {
        // Configurar key binding para Ctrl+Z
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"), "deshacerAccion");
        getActionMap().put("deshacerAccion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deshacerAccion();
            }
        });
        
        // Manejo de eventos de mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Si se presiona sobre un vértice, lo marca como candidato
                    Vertice vEncontrado = buscarVertice(e.getX(), e.getY());
                    if (vEncontrado != null) {
                        verticeCandidato = vEncontrado;
                        inicioX = e.getX();
                        inicioY = e.getY();
                        esArrastre = false;
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (verticeCandidato != null) {
                        if (esArrastre) {
                            // Finalizó un arrastre: se verifica colisión y se registra la acción de mover
                            verificarColision(verticeCandidato);
                            if (verticeCandidato.x != inicioX || verticeCandidato.y != inicioY) {
                                acciones.push(new AccionMoverVertice(verticeCandidato, inicioX, inicioY));
                            }
                        } else {
                            // Fue un click sin arrastre sobre un vértice
                            if (verticeSeleccionadoParaArista == null) {
                                // Selecciona el vértice para formar arista (se resaltará en amarillo con aro gris)
                                verticeSeleccionadoParaArista = verticeCandidato;
                            } else {
                                // Si se hace click sobre otro vértice, se crea la arista
                                if (verticeSeleccionadoParaArista != verticeCandidato) {
                                    Arista nuevaArista = new Arista(verticeSeleccionadoParaArista, verticeCandidato);
                                    aristas.add(nuevaArista);
                                    acciones.push(new AccionAgregarArista(nuevaArista));
                                }
                                // Se limpia la selección
                                verticeSeleccionadoParaArista = null;
                            }
                        }
                        verticeCandidato = null;
                        repaint();
                    } else {
                        // Si no se hizo click sobre un vértice, se crea uno nuevo
                        if (buscarVertice(e.getX(), e.getY()) == null) {
                            Vertice nuevo = new Vertice(e.getX(), e.getY(), contadorVertices++);
                            vertices.add(nuevo);
                            acciones.push(new AccionAgregarVertice(nuevo));
                            repaint();
                        }
                    }
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // Primero, se verifica si se hizo click derecho sobre un vértice
                    Vertice v = buscarVertice(e.getX(), e.getY());
                    if (v != null) {
                        List<Arista> aristasEliminadas = new ArrayList<>();
                        Iterator<Arista> itA = aristas.iterator();
                        while (itA.hasNext()) {
                            Arista a = itA.next();
                            if (a.origen == v || a.destino == v) {
                                aristasEliminadas.add(a);
                                itA.remove();
                            }
                        }
                        vertices.remove(v);
                        if (verticeSeleccionadoParaArista == v) {
                            verticeSeleccionadoParaArista = null;
                        }
                        acciones.push(new AccionEliminarVertice(v, aristasEliminadas));
                        repaint();
                    } else {
                        // Si no fue sobre un vértice, se verifica si fue sobre una arista
                        Arista aEncontrada = buscarArista(e.getX(), e.getY());
                        if (aEncontrada != null) {
                            aristas.remove(aEncontrada);
                            acciones.push(new AccionEliminarArista(aEncontrada));
                            repaint();
                        }
                    }
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (verticeCandidato != null) {
                        int dx = e.getX() - inicioX;
                        int dy = e.getY() - inicioY;
                        if (!esArrastre && (Math.abs(dx) > UMBRAL_ARRASTRE || Math.abs(dy) > UMBRAL_ARRASTRE)) {
                            // Se detecta que se arrastra; se cancela la intención de seleccionar para arista
                            esArrastre = true;
                            if (verticeSeleccionadoParaArista == verticeCandidato) {
                                verticeSeleccionadoParaArista = null;
                            }
                        }
                        if (esArrastre) {
                            // Mueve el vértice
                            verticeCandidato.x = e.getX();
                            verticeCandidato.y = e.getY();
                            repaint();
                        }
                    }
                }
            }
        });
    }
    
    // Retorna el vértice cuyo centro está cerca de (x, y)
    private Vertice buscarVertice(int x, int y) {
        for (Vertice v : vertices) {
            if (Math.hypot(v.x - x, v.y - y) <= RADIO) {
                return v;
            }
        }
        return null;
    }
    
    // Retorna una arista que esté cerca del punto (x, y)
    private Arista buscarArista(int x, int y) {
        for (Arista a : aristas) {
            int x1 = a.origen.x, y1 = a.origen.y, x2 = a.destino.x, y2 = a.destino.y;
            double dist = distanciaPuntoASegmento(x, y, x1, y1, x2, y2);
            if (dist <= 5) {
                return a;
            }
        }
        return null;
    }
    
    // Calcula la distancia desde un punto hasta un segmento
    private double distanciaPuntoASegmento(int px, int py, int x1, int y1, int x2, int y2) {
        double l2 = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
        if (l2 == 0) return Math.hypot(px - x1, py - y1);
        double t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / l2;
        t = Math.max(0, Math.min(1, t));
        double projX = x1 + t * (x2 - x1);
        double projY = y1 + t * (y2 - y1);
        return Math.hypot(px - projX, py - projY);
    }
    
    // Verifica si el vértice 'v' está colisionando con otro y, de ser así, ajusta su posición
    private void verificarColision(Vertice v) {
        boolean colision = true;
        while (colision) {
            colision = false;
            for (Vertice otro : vertices) {
                if (otro != v) {
                    if (Math.hypot(v.x - otro.x, v.y - otro.y) < 2 * RADIO) {
                        // Se mueve el vértice a la derecha del otro con una separación extra
                        v.x = otro.x + 2 * RADIO + 5;
                        v.y = otro.y;
                        colision = true;
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dibuja las aristas
        for (Arista a : aristas) {
            g.setColor(Color.BLACK);
            g.drawLine(a.origen.x, a.origen.y, a.destino.x, a.destino.y);
        }
        // Dibuja los vértices
        for (Vertice v : vertices) {
            if (v == verticeSeleccionadoParaArista) {
                // Vértice resaltado: relleno amarillo y aro gris
                g.setColor(Color.YELLOW);
                g.fillOval(v.x - RADIO, v.y - RADIO, 2 * RADIO, 2 * RADIO);
                g.setColor(Color.GRAY);
                g.drawOval(v.x - RADIO - 3, v.y - RADIO - 3, 2 * RADIO + 6, 2 * RADIO + 6);
            } else {
                g.setColor(Color.BLUE);
                g.fillOval(v.x - RADIO, v.y - RADIO, 2 * RADIO, 2 * RADIO);
            }
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(v.id), v.x - 5, v.y + 5);
        }
    }
    
    // Método para deshacer la última acción (Ctrl+Z)
    private void deshacerAccion() {
        if (!acciones.isEmpty()) {
            Accion accion = acciones.pop();
            accion.deshacer();
            repaint();
        }
    }
    
    // Clases internas para representar acciones que se pueden deshacer
    private abstract class Accion {
        abstract void deshacer();
    }
    
    private class AccionAgregarVertice extends Accion {
        private Vertice vertice;
        
        public AccionAgregarVertice(Vertice v) {
            this.vertice = v;
        }
        
        @Override
        void deshacer() {
            vertices.remove(vertice);
        }
    }
    
    private class AccionEliminarVertice extends Accion {
        private Vertice vertice;
        private List<Arista> aristasEliminadas;
        
        public AccionEliminarVertice(Vertice v, List<Arista> aristasEliminadas) {
            this.vertice = v;
            this.aristasEliminadas = aristasEliminadas;
        }
        
        @Override
        void deshacer() {
            vertices.add(vertice);
            aristas.addAll(aristasEliminadas);
        }
    }
    
    private class AccionAgregarArista extends Accion {
        private Arista arista;
        
        public AccionAgregarArista(Arista a) {
            this.arista = a;
        }
        
        @Override
        void deshacer() {
            aristas.remove(arista);
        }
    }
    
    private class AccionEliminarArista extends Accion {
        private Arista arista;
        
        public AccionEliminarArista(Arista a) {
            this.arista = a;
        }
        
        @Override
        void deshacer() {
            aristas.add(arista);
        }
    }
    
    private class AccionMoverVertice extends Accion {
        private Vertice vertice;
        private int xAnterior, yAnterior;
        
        public AccionMoverVertice(Vertice v, int xAnt, int yAnt) {
            this.vertice = v;
            this.xAnterior = xAnt;
            this.yAnterior = yAnt;
        }
        
        @Override
        void deshacer() {
            vertice.x = xAnterior;
            vertice.y = yAnterior;
        }
    }
    
    public static void main(String[] args) {
        JFrame ventana = new JFrame("Editor de Grafo");
        EditorGrafo editor = new EditorGrafo();
        ventana.add(editor);
        ventana.setSize(800, 600);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);
    }
}
