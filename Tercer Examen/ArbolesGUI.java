import java.awt.*;
import javax.swing.*;
import java.util.Arrays;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

// Interfaz base para cualquier tipo de árbol
interface ArbolBase {
    void insertar(int valor);
    void eliminar(int valor);
    boolean buscar(int valor);
    String inorden();
    NodoVisual getVisualTree();
}

// Nodo para árboles binarios y ABB
class Nodo {
    int valor;
    Nodo izq, der;
    Nodo(int valor) { this.valor = valor; }
}

// Nodo para visualización gráfica
class NodoVisual {
    int valor;
    NodoVisual izq, der, padre;
    Color color = Color.LIGHT_GRAY;
    String extraInfo = "";
    boolean conexionIzq, conexionDer;
    NodoVisual(int valor) { this.valor = valor; }
}

// Panel que dibuja el árbol
class PanelDibujo extends JPanel {
    private NodoVisual raiz;
    void setRaizVisual(NodoVisual r) { raiz = r; repaint(); }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raiz != null) dibujar((Graphics2D)g, raiz, getWidth()/2, 50, getWidth()/4);
    }
    private void dibujar(Graphics2D g, NodoVisual n, int x, int y, int off) {
        int r = 20;
        g.setColor(n.color);
        g.fillOval(x-r, y-r, 2*r, 2*r);
        g.setColor(Color.BLACK);
        g.drawOval(x-r, y-r, 2*r, 2*r);
        String t = String.valueOf(n.valor);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(t, x - fm.stringWidth(t)/2, y + fm.getAscent()/4);
        if (!n.extraInfo.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString(n.extraInfo, x - 30, y + r + 15);
            g.setFont(new Font("Arial", Font.BOLD, 14));
        }
        int lx = x, ly = y + r;
        if (n.izq != null) {
            g.setColor(n.conexionIzq ? Color.BLUE : Color.BLACK);
            g.drawLine(lx, ly, x-off, y+50-r);
            dibujar(g, n.izq, x-off, y+50, off/2);
        }
        if (n.der != null) {
            g.setColor(n.conexionDer ? Color.BLUE : Color.BLACK);
            g.drawLine(lx, ly, x+off, y+50-r);
            dibujar(g, n.der, x+off, y+50, off/2);
        }
    }
}

// Árbol binario completo (sin orden)
class ArbolBinario implements ArbolBase {
    protected Nodo raiz;
    public void insertar(int v) { 
        if (!buscar(v)) {
            raiz = insertarRec(raiz, v); 
        }
    }
    protected Nodo insertarRec(Nodo n, int v) {
        if (n == null) return new Nodo(v);
        if (n.izq == null) n.izq = insertarRec(n.izq, v);
        else n.der = insertarRec(n.der, v);
        return n;
    }
    public boolean buscar(int v) { return buscarRec(raiz, v); }
    protected boolean buscarRec(Nodo n, int v) {
        if (n == null) return false;
        if (n.valor == v) return true;
        return buscarRec(n.izq, v) || buscarRec(n.der, v);
    }
    public String inorden() {
        StringBuilder sb = new StringBuilder();
        inordenRec(raiz, sb);
        return sb.toString();
    }
    protected void inordenRec(Nodo n, StringBuilder sb) {
        if (n != null) {
            inordenRec(n.izq, sb);
            sb.append(n.valor).append(' ');
            inordenRec(n.der, sb);
        }
    }
    public NodoVisual getVisualTree() { return convertir(raiz); }
    protected NodoVisual convertir(Nodo n) {
        if (n == null) return null;
        NodoVisual v = new NodoVisual(n.valor);
        v.izq = convertir(n.izq); if (v.izq != null) v.izq.padre = v;
        v.der = convertir(n.der); if (v.der != null) v.der.padre = v;
        return v;
    }
    public void eliminar(int v) { raiz = eliminarRec(raiz, v); }
    protected Nodo eliminarRec(Nodo n, int v) {
        if (n == null) return null;
        if (v < n.valor) n.izq = eliminarRec(n.izq, v);
        else if (v > n.valor) n.der = eliminarRec(n.der, v);
        else {
            if (n.izq == null) return n.der;
            if (n.der == null) return n.izq;
            Nodo m = min(n.der);
            n.valor = m.valor;
            n.der = eliminarRec(n.der, m.valor);
        }
        return n;
    }
    private Nodo min(Nodo n) { while (n.izq != null) n = n.izq; return n; }
}

// ABB (árbol binario de búsqueda)
class ArbolBusqueda extends ArbolBinario {
    @Override
    protected Nodo insertarRec(Nodo n, int v) {
        if (n == null) return new Nodo(v);
        if (v < n.valor) n.izq = insertarRec(n.izq, v);
        else if (v > n.valor) n.der = insertarRec(n.der, v);
        return n;
    }
}

// AVL con balanceo
class ArbolAVL extends ArbolBusqueda {
    class NodoAVL extends Nodo { int h = 1; NodoAVL(int v) { super(v); } }
    @Override
    protected Nodo insertarRec(Nodo n, int v) {
        if (n == null) return new NodoAVL(v);
        if (v < n.valor) n.izq = insertarRec(n.izq, v);
        else if (v > n.valor) n.der = insertarRec(n.der, v);
        else return n; // No insertar duplicados
        return balance((NodoAVL)n);
    }
    private NodoAVL balance(NodoAVL n) {
        updateHeight(n);
        int bf = height((NodoAVL)n.izq) - height((NodoAVL)n.der);
        if (bf > 1) {
            if (height((NodoAVL)n.izq.izq) < height((NodoAVL)n.izq.der))
                n.izq = rotateLeft((NodoAVL)n.izq);
            return rotateRight(n);
        }
        if (bf < -1) {
            if (height((NodoAVL)n.der.der) < height((NodoAVL)n.der.izq))
                n.der = rotateRight((NodoAVL)n.der);
            return rotateLeft(n);
        }
        return n;
    }
    private int height(NodoAVL n) { return n == null ? 0 : n.h; }
    private void updateHeight(NodoAVL n) { n.h = 1 + Math.max(height((NodoAVL)n.izq), height((NodoAVL)n.der)); }
    private NodoAVL rotateRight(NodoAVL y) {
        NodoAVL x = (NodoAVL)y.izq;
        NodoAVL T2 = (NodoAVL)x.der;
        x.der = y; y.izq = T2;
        updateHeight(y); updateHeight(x);
        return x;
    }
    private NodoAVL rotateLeft(NodoAVL x) {
        NodoAVL y = (NodoAVL)x.der;
        NodoAVL T2 = (NodoAVL)y.izq;
        y.izq = x; x.der = T2;
        updateHeight(x); updateHeight(y);
        return y;
    }
    @Override public void eliminar(int v) { raiz = eliminarRec(raiz, v); if (raiz != null) raiz = balance((NodoAVL)raiz); }
    @Override protected Nodo eliminarRec(Nodo n, int v) { return super.eliminarRec(n, v); }
    @Override public NodoVisual getVisualTree() {
        NodoVisual vis = super.getVisualTree();
        assignHeights(vis, (NodoAVL)raiz);
        return vis;
    }
    private void assignHeights(NodoVisual v, NodoAVL n) {
        if (v == null || n == null) return;
        v.extraInfo = "h=" + n.h;
        assignHeights(v.izq, (NodoAVL)n.izq);
        assignHeights(v.der, (NodoAVL)n.der);
    }
}

// B-Tree orden 2 a 5
class ArbolB implements ArbolBase {
    private final int T;
    class NodoB {
        int[] claves;
        NodoB[] hijos;
        int n;
        boolean hoja;
        NodoB() {
            claves = new int[2*T - 1];
            hijos = new NodoB[2*T];
            n = 0; hoja = true;
        }
    }
    private NodoB raiz;
    public ArbolB(int orden) {
        if (orden < 2) orden = 2;
        if (orden > 5) orden = 5;  // Aseguramos que no sea mayor a 5
        T = orden;
        raiz = new NodoB();
    }   
    @Override
    public void insertar(int k) {
        if (!buscar(k)) {
            NodoB r = raiz;
            if (r.n == 2*T - 1) {
                NodoB s = new NodoB(); s.hoja = false;
                s.hijos[0] = r;
                split(s, 0, r);
                raiz = s;
                insertNonFull(s, k);
            } else insertNonFull(r, k);
        }
    }
    private void insertNonFull(NodoB x, int k) {
        int i = x.n - 1;
        if (x.hoja) {
            while (i >= 0 && k < x.claves[i]) {
                x.claves[i+1] = x.claves[i]; i--;
            }
            x.claves[i+1] = k; x.n++;
        } else {
            while (i >= 0 && k < x.claves[i]) i--;
            i++;
            if (x.hijos[i].n == 2*T - 1) {
                split(x, i, x.hijos[i]);
                if (k > x.claves[i]) i++;
            }
            insertNonFull(x.hijos[i], k);
        }
    }
    private void split(NodoB p, int i, NodoB y) {
        NodoB z = new NodoB(); z.hoja = y.hoja; z.n = T - 1;
        System.arraycopy(y.claves, T, z.claves, 0, T-1);
        if (!y.hoja) System.arraycopy(y.hijos, T, z.hijos, 0, T);
        y.n = T - 1;
        for (int j = p.n; j >= i+1; j--) p.hijos[j+1] = p.hijos[j];
        p.hijos[i+1] = z;
        for (int j = p.n-1; j >= i; j--) p.claves[j+1] = p.claves[j];
        p.claves[i] = y.claves[T-1]; p.n++;
    }
    @Override public boolean buscar(int k) { return search(raiz, k); }
    private boolean search(NodoB x, int k) {
        int i = 0;
        while (i < x.n && k > x.claves[i]) i++;
        if (i < x.n && k == x.claves[i]) return true;
        if (x.hoja) return false;
        return search(x.hijos[i], k);
    }
    @Override public String inorden() {
        StringBuilder sb = new StringBuilder();
        inorder(raiz, sb);
        return sb.toString();
    }
    private void inorder(NodoB x, StringBuilder sb) {
        for (int i = 0; i < x.n; i++) {
            if (!x.hoja) inorder(x.hijos[i], sb);
            sb.append(x.claves[i]).append(' ');
        }
        if (!x.hoja) inorder(x.hijos[x.n], sb);
    }
    @Override public NodoVisual getVisualTree() { return convert(raiz); }
    private NodoVisual convert(NodoB x) {
        if (x == null || x.n == 0) return null;
        NodoVisual v = new NodoVisual(x.claves[0]);
        v.extraInfo = Arrays.toString(Arrays.copyOf(x.claves, x.n));
        if (!x.hoja) {
            v.izq = convert(x.hijos[0]); if (v.izq != null) v.izq.padre = v;
            v.der = convert(x.hijos[1]); if (v.der != null) v.der.padre = v;
        }
        return v;
    }
    @Override public void eliminar(int k) {
        delete(raiz, k);
        if (!raiz.hoja && raiz.n == 0) raiz = raiz.hijos[0];
    }
    private void delete(NodoB x, int k) {
        int idx = findKey(x, k);
        if (idx < x.n && x.claves[idx] == k) {
            if (x.hoja) {
                for (int i = idx; i < x.n-1; i++) x.claves[i] = x.claves[i+1];
                x.n--;
            } else {
                if (x.hijos[idx].n >= T) {
                    int pred = getPred(x, idx);
                    x.claves[idx] = pred;
                    delete(x.hijos[idx], pred);
                } else if (x.hijos[idx+1].n >= T) {
                    int succ = getSucc(x, idx);
                    x.claves[idx] = succ;
                    delete(x.hijos[idx+1], succ);
                } else {
                    merge(x, idx);
                    delete(x.hijos[idx], k);
                }
            }
        } else {
            if (x.hoja) return;
            boolean last = (idx == x.n);
            if (x.hijos[idx].n < T) fill(x, idx);
            if (last && idx > x.n) delete(x.hijos[idx-1], k);
            else delete(x.hijos[idx], k);
        }
    }
    private int findKey(NodoB x, int k) {
        int idx = 0;
        while (idx < x.n && x.claves[idx] < k) idx++;
        return idx;
    }
    private int getPred(NodoB x, int idx) {
        NodoB cur = x.hijos[idx];
        while (!cur.hoja) cur = cur.hijos[cur.n];
        return cur.claves[cur.n-1];
    }
    private int getSucc(NodoB x, int idx) {
        NodoB cur = x.hijos[idx+1];
        while (!cur.hoja) cur = cur.hijos[0];
        return cur.claves[0];
    }
    private void fill(NodoB x, int idx) {
        if (idx!=0 && x.hijos[idx-1].n>=T) borrowPrev(x, idx);
        else if (idx!=x.n && x.hijos[idx+1].n>=T) borrowNext(x, idx);
        else { if (idx!=x.n) merge(x, idx); else merge(x, idx-1); }
    }
    private void borrowPrev(NodoB x, int idx) {
        NodoB c = x.hijos[idx], s = x.hijos[idx-1];
        for (int i = c.n-1; i>=0; i--) c.claves[i+1] = c.claves[i];
        if (!c.hoja) for (int i = c.n; i>=0; i--) c.hijos[i+1] = c.hijos[i];
        c.claves[0] = x.claves[idx-1];
        if (!c.hoja) c.hijos[0] = s.hijos[s.n];
        x.claves[idx-1] = s.claves[s.n-1];
        c.n++; s.n--;
    }
    private void borrowNext(NodoB x, int idx) {
        NodoB c = x.hijos[idx], s = x.hijos[idx+1];
        c.claves[c.n] = x.claves[idx];
        if (!c.hoja) c.hijos[c.n+1] = s.hijos[0];
        x.claves[idx] = s.claves[0];
        for (int i =1; i<s.n; i++) s.claves[i-1] = s.claves[i];
        if (!s.hoja) for (int i=1; i<=s.n; i++) s.hijos[i-1] = s.hijos[i];
        c.n++; s.n--;
    }
    private void merge(NodoB x, int idx) {
        NodoB c = x.hijos[idx], s = x.hijos[idx+1];
        c.claves[T-1] = x.claves[idx];
        for (int i=0; i<s.n; i++) c.claves[i+T] = s.claves[i];
        if (!c.hoja) for (int i=0; i<=s.n; i++) c.hijos[i+T] = s.hijos[i];
        for (int i=idx+1; i<x.n; i++) x.claves[i-1] = x.claves[i];
        for (int i=idx+2; i<=x.n; i++) x.hijos[i-1] = x.hijos[i];
        c.n += s.n+1; x.n--;
    }
}

// Clase principal GUI
public class ArbolesGUI extends JFrame {
    private ArbolBinario ab = new ArbolBinario();
    private ArbolBusqueda abb = new ArbolBusqueda();
    private ArbolAVL avl = new ArbolAVL();
    private ArbolB b;
    private ArbolBase activo;
    private PanelDibujo dibujo = new PanelDibujo();
    private JTextArea salida = new JTextArea();
    private JTextField entrada = new JTextField(5), ordenField = new JTextField("2",3);
    private JComboBox<String> tipo;
    private JLabel ordenLabel;

    public ArbolesGUI() {
        setTitle("Visualizador Árboles"); setSize(800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE); setLocationRelativeTo(null);
        
        // Panel principal
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(dibujo, BorderLayout.CENTER);

        // Controles
        JPanel ctrl = new JPanel();
        tipo = new JComboBox<>(new String[]{"AB","ABB","AVL","B"});
        ctrl.add(tipo);
        ctrl.add(new JLabel("Valor:")); ctrl.add(entrada);

        ordenLabel = new JLabel("Orden B:");
        ctrl.add(ordenLabel);
        ctrl.add(ordenField);
        // Ocultar orden por defecto
        ordenLabel.setVisible(false);
        ordenField.setVisible(false);

        JButton ins = new JButton("Insertar"), del = new JButton("Eliminar"), bus = new JButton("Buscar");
        ctrl.add(ins); ctrl.add(del); ctrl.add(bus);
        panel.add(ctrl, BorderLayout.NORTH);
        panel.add(new JScrollPane(salida), BorderLayout.SOUTH);
        add(panel);

        // Agregar DocumentListener para validar el orden en tiempo real
        ordenField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { validarOrden(); }
            public void removeUpdate(DocumentEvent e) { validarOrden(); }
            public void insertUpdate(DocumentEvent e) { validarOrden(); }
            
            private void validarOrden() {
                if (ordenField.isVisible()) { // Solo validar si el campo es visible
                    try {
                        int o = Integer.parseInt(ordenField.getText());
                        if (o > 5) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(ArbolesGUI.this, 
                                    "El orden no puede ser mayor a 5. Se usará orden 5", 
                                    "Advertencia", 
                                    JOptionPane.WARNING_MESSAGE);
                                ordenField.setText("5");
                            });
                        } else if (o < 2) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(ArbolesGUI.this, 
                                    "El orden no puede ser menor a 2. Se usará orden 2", 
                                    "Advertencia", 
                                    JOptionPane.WARNING_MESSAGE);
                                ordenField.setText("2");
                            });
                        }
                    } catch (NumberFormatException ex) {
                        // No hacer nada si no es un número válido todavía
                    }
                }
            }
        });

        // Al cambiar tipo, actualizar visibilidad y crear B si aplica
        tipo.addActionListener(e -> {
            String t = (String) tipo.getSelectedItem();
            boolean isB = t.equals("B");
            ordenLabel.setVisible(isB);
            ordenField.setVisible(isB);
            if (isB) {
                int o;
                try { 
                    o = Integer.parseInt(ordenField.getText());
                    if (o > 5) {
                        JOptionPane.showMessageDialog(this, 
                            "El orden no puede ser mayor a 5. Se usará orden 5", 
                            "Advertencia", 
                            JOptionPane.WARNING_MESSAGE);
                        o = 5;
                        ordenField.setText("5");
                    } else if (o < 2) {
                        JOptionPane.showMessageDialog(this, 
                            "El orden no puede ser menor a 2. Se usará orden 2", 
                            "Advertencia", 
                            JOptionPane.WARNING_MESSAGE);
                        o = 2;
                        ordenField.setText("2");
                    }
                }
                catch(Exception ex) { 
                    o = 2; 
                    ordenField.setText("2");
                }
                b = new ArbolB(o);
                activo = b;
            } else if (t.equals("ABB")) activo = abb;
            else if (t.equals("AVL")) activo = avl;
            else activo = ab;
	    dibujo.setRaizVisual(activo.getVisualTree());
	});

       	// Acciones botones
        ins.addActionListener(e -> operar("insertar"));
        del.addActionListener(e -> operar("eliminar"));
        bus.addActionListener(e -> operar("buscar"));

        // Inicial activo
        activo = ab;
    }

    // Método para ejecutar operación y mostrar resultado
    private void operar(String op) {
        try {
            int v = Integer.parseInt(entrada.getText());
            switch(op) {
                case "insertar":
                    if (activo.buscar(v)) {
                        salida.append("\nEl valor " + v + " ya existe en el árbol");
                    } else {
                        activo.insertar(v);
                        salida.append("\nInsertado: " + v);
                        dibujo.setRaizVisual(activo.getVisualTree());
                    }
                    break;
                case "eliminar":
                    if (activo.buscar(v)) {
                        activo.eliminar(v);
                        salida.append("\nEliminado: " + v);
                        dibujo.setRaizVisual(activo.getVisualTree());
                    } else {
                        salida.append("\nEl valor " + v + " no existe en el árbol");
                    }
                    break;
                case "buscar":
                    boolean enc = activo.buscar(v);
                    salida.append("\nBuscando: " + v + (enc ? " existe" : " no existe"));
                    break;
            }
        } catch(Exception ex) {
            salida.append("\nError: entrada inválida");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArbolesGUI().setVisible(true));
    }
}
