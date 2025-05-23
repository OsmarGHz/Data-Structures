import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.swing.*;
import java.util.Arrays;

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
    Color color = new Color(210, 210, 255);
    String extraInfo = "";
    boolean conexionIzq, conexionDer;
    NodoVisual(int valor) { this.valor = valor; }
}

// Nodo visual especial para árbol B
class NodoVisualB {
    int[] claves;
    int n;
    NodoVisualB[] hijos; // hijos visuales
    int x, y; // posición para dibujar
    Color color = new Color(210, 210, 255);
    int claveResaltada = -1; // -1: ninguna resaltada

    NodoVisualB(int[] claves, int n) {
        this.claves = Arrays.copyOf(claves, n);
        this.n = n;
        this.hijos = null;
    }
}

// Panel que dibuja el árbol
class PanelDibujo extends JPanel {
    private NodoVisual raiz;
    private NodoVisualB raizB; // <- NUEVO

    // --- NUEVO: Cálculo de ancho dinámico para scroll y espaciado ---
    private static final int RADIO_NODO = 28;
    private static final int FUENTE_NODO = 18;
    private static final int BOX_B_ANCHO = 40;
    private static final int BOX_B_ALTO = 40;
    private static final int ESPACIO_H = 56; // ancho mínimo por nodo
    private static final int ESPACIO_V = 100; // espacio vertical entre niveles

    private int calcularAncho(NodoVisual n) {
        if (n == null) return 0;
        int r = ESPACIO_H;
        int izq = calcularAncho(n.izq);
        int der = calcularAncho(n.der);
        if (izq == 0 && der == 0) return r;
        return Math.max(r, izq + der + ESPACIO_H);
    }
    private int calcularAnchoB(NodoVisualB n) {
        if (n == null) return 0;
        int boxWidth = BOX_B_ANCHO * n.n;
        if (n.hijos == null || n.hijos.length == 0) return boxWidth;
        int total = 0;
        for (NodoVisualB h : n.hijos) total += calcularAnchoB(h);
        total += BOX_B_ANCHO * (n.hijos.length - 1); // espacio entre hijos
        return Math.max(boxWidth, total);
    }

    // --- NUEVO: Cálculo de altura dinámica para scroll vertical ---
    private int calcularAltura(NodoVisual n) {
        if (n == null) return 0;
        return 1 + Math.max(calcularAltura(n.izq), calcularAltura(n.der));
    }
    private int calcularAlturaB(NodoVisualB n) {
        if (n == null) return 0;
        int maxH = 0;
        if (n.hijos != null) {
            for (NodoVisualB h : n.hijos) {
                maxH = Math.max(maxH, calcularAlturaB(h));
            }
        }
        return 1 + maxH;
    }

    void setRaizVisual(NodoVisual r) { 
        raiz = r; 
        raizB = null;
        ajustarTamanioPanel();
        repaint(); 
    }
    void setRaizVisualB(NodoVisualB r) { 
        raizB = r; 
        raiz = null;
        ajustarTamanioPanel();
        repaint(); 
    }
    // Nuevo método para limpiar completamente el panel
    void limpiarTodo() {
        raiz = null;
        raizB = null;
        ajustarTamanioPanel();
        repaint();
    }
    //Método getRaizVisual para obtener la raiz visual del árbol
    public NodoVisual getRaizVisual() {
        return raiz;
    }

    // Ajusta el tamaño preferido del panel para scroll
    private void ajustarTamanioPanel() {
        int ancho = 800, alto = 600;
        int niveles = 1;
        if (raizB != null) {
            ancho = Math.max(800, calcularAnchoB(raizB) + 100);
            niveles = calcularAlturaB(raizB);
        } else if (raiz != null) {
            ancho = Math.max(800, calcularAncho(raiz) + 100);
            niveles = calcularAltura(raiz);
        }
        // Calcula alto dinámico: ESPACIO_V px por nivel + margen
        alto = Math.max(200, niveles * ESPACIO_V + 60);
        setPreferredSize(new Dimension(ancho, alto));
        revalidate();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (raizB != null) {
            int anchoTotal = calcularAnchoB(raizB);
            dibujarB((Graphics2D)g, raizB, getWidth()/2, 50, anchoTotal);
        } else if (raiz != null) {
            int anchoTotal = calcularAncho(raiz);
            dibujar((Graphics2D)g, raiz, getWidth()/2, 50, anchoTotal);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    

    // Modifica dibujar para usar el ancho calculado
    private void dibujar(Graphics2D g, NodoVisual n, int x, int y, int ancho) {
        if (n == null) return;
        int r = RADIO_NODO;
        g.setColor(n.color);
        g.fillOval(x-r, y-r, 2*r, 2*r);
        g.setColor(new Color(150, 150, 220)); // Verde oscuro para el contorno
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x-r, y-r, 2*r, 2*r);
        g.setStroke(new BasicStroke(1));
        g.setFont(new Font("Arial", Font.BOLD, FUENTE_NODO));
        String t = String.valueOf(n.valor);
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.BLACK);
        g.drawString(t, x - fm.stringWidth(t)/2, y + fm.getAscent()/4);

        // --- DIBUJA LAS ARISTAS Y LOS HIJOS ---
        int izqAncho = n.izq != null ? calcularAncho(n.izq) : 0;
        int derAncho = n.der != null ? calcularAncho(n.der) : 0;
        int totalHijos = izqAncho + derAncho;
        int baseY = y + ESPACIO_V;
        if (n.izq != null) {
            int childX = x - (totalHijos > 0 ? (derAncho + ESPACIO_H)/2 : 80);
            g.setColor(n.conexionIzq ? Color.BLUE : new Color(0, 100, 0));
            ((Graphics2D)g).setStroke(new BasicStroke(1.5f)); // Línea un poco más gruesa
            g.drawLine(x, y + r, childX, baseY - r);
            ((Graphics2D)g).setStroke(new BasicStroke(1)); // Restaurar grosor
            dibujar(g, n.izq, childX, baseY, izqAncho);
        }
        if (n.der != null) {
            int childX = x + (totalHijos > 0 ? (izqAncho + ESPACIO_H)/2 : 80);
            g.setColor(n.conexionDer ? Color.BLUE : new Color(0, 100, 0));
            ((Graphics2D)g).setStroke(new BasicStroke(1.5f)); // Líneaun poco  más gruesa
            g.drawLine(x, y + r, childX, baseY - r);
            ((Graphics2D)g).setStroke(new BasicStroke(1)); // Restaurar grosor
            dibujar(g, n.der, childX, baseY, derAncho);
        }

        // --- DIBUJAR LA ETIQUETA (extraInfo) ENCIMA DE LAS ARISTAS ---
        if (!n.extraInfo.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 15));
            FontMetrics fmInfo = g.getFontMetrics();
            int infoWidth = fmInfo.stringWidth(n.extraInfo);
            int infoHeight = fmInfo.getHeight();
            int paddingX = 8, paddingY = 3;
            int arc = 12; // radio de las esquinas redondeadas

            int infoX = x - infoWidth / 2 - paddingX;
            int infoY = y + RADIO_NODO + fmInfo.getAscent() + 2;
            int rectY = infoY - fmInfo.getAscent() + 1;

            // Fondo semitransparente y redondeado
            Color fondo = new Color(255, 255, 200, 230); // Amarillo claro, alfa 180/255
            g.setColor(fondo);
            g.fillRoundRect(infoX, rectY, infoWidth + 2 * paddingX, infoHeight + 2 * paddingY, arc, arc);

            // Borde opcional
            g.setColor(new Color(180, 180, 100, 200));
            g.drawRoundRect(infoX, rectY, infoWidth + 2 * paddingX, infoHeight + 2 * paddingY, arc, arc);

            // Texto
            g.setColor(Color.DARK_GRAY);
            g.drawString(n.extraInfo, x - infoWidth / 2, infoY + paddingY);

            g.setFont(new Font("Arial", Font.BOLD, FUENTE_NODO));
        }
    }

    // Modifica dibujarB para usar el ancho calculado
    private void dibujarB(Graphics2D g, NodoVisualB n, int x, int y, int ancho) {
        if (n == null) return;
        // --- Calcular ancho dinámico de cada clave ---
        g.setFont(new Font("Arial", Font.BOLD, FUENTE_NODO));
        FontMetrics fm = g.getFontMetrics();
        int[] claveAnchos = new int[n.n];
        int maxClaveAlto = 0;
        for (int i = 0; i < n.n; i++) {
            String clave = String.valueOf(n.claves[i]);
            claveAnchos[i] = Math.max(BOX_B_ANCHO, fm.stringWidth(clave) + 24);
            maxClaveAlto = Math.max(maxClaveAlto, fm.getHeight() + 16);
        }
        int boxWidth = 0;
        for (int w : claveAnchos) boxWidth += w;
        int boxHeight = Math.max(BOX_B_ALTO, maxClaveAlto);
        // --- Dibujar el recuadro del nodo ---
        g.setColor(n.color);
        g.fillRect(x - boxWidth/2, y, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x - boxWidth/2, y, boxWidth, boxHeight);
        // --- Dibujar claves con ancho variable ---
        int cx = x - boxWidth/2;
        for (int i = 0; i < n.n; i++) {
            String clave = String.valueOf(n.claves[i]);
            int claveX = cx + (claveAnchos[i] - fm.stringWidth(clave))/2;
            int claveY = y + boxHeight/2 + FUENTE_NODO/2 - 2;
            if (n.claveResaltada == i) {
                Color fondo = (n.color == Color.GREEN) ? Color.GREEN : Color.RED;
                g.setColor(fondo);
                g.fillRoundRect(cx + 2, y + 6, claveAnchos[i] - 4, boxHeight - 12, 10, 10);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, FUENTE_NODO+2));
                g.drawString(clave, claveX, claveY);
                g.setFont(new Font("Arial", Font.BOLD, FUENTE_NODO));
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.BLACK);
                g.drawString(clave, claveX, claveY);
            }
            // Línea divisoria entre claves
            if (i < n.n-1) {
                g.setColor(Color.BLACK);
                g.drawLine(cx + claveAnchos[i], y, cx + claveAnchos[i], y + boxHeight);
            }
            cx += claveAnchos[i];
        }
        // --- Dibujar hijos ---
        if (n.hijos != null) {
            int[] hijosAnchos = new int[n.hijos.length];
            int totalHijosAncho = 0;
            for (int i = 0; i < n.hijos.length; i++) {
                hijosAnchos[i] = calcularAnchoB(n.hijos[i]);
                totalHijosAncho += hijosAnchos[i];
            }
            totalHijosAncho += BOX_B_ANCHO * (n.hijos.length - 1);
            int startX = x - totalHijosAncho/2;
            int childX = startX;
            cx = x - boxWidth/2;
            for (int i = 0; i < n.hijos.length; i++) {
                int childCenter = childX + hijosAnchos[i]/2;
                int claveOffset = 0;
                for (int j = 0; j < i; j++) claveOffset += claveAnchos[j];
                int fromX = cx + claveOffset;
                ((Graphics2D)g).setStroke(new BasicStroke(4)); // Línea más gruesa
                g.drawLine(fromX, y + boxHeight, childCenter, y + boxHeight + ESPACIO_V - boxHeight/2);
                ((Graphics2D)g).setStroke(new BasicStroke(1)); // Restaurar grosor
                dibujarB(g, n.hijos[i], childCenter, y + boxHeight + ESPACIO_V - boxHeight/2, hijosAnchos[i]);
                childX += hijosAnchos[i] + BOX_B_ANCHO;
            }
        }
    }
}

// Árbol binario completo (sin orden)
class ArbolBinario implements ArbolBase {
    protected Nodo raiz;

    // Nuevo método para insertar con padre y lado
    public boolean insertar(int valor, int padreValor, String lado) {
        if (buscar(valor)) return false; // No duplicados
        if (raiz == null) {
            raiz = new Nodo(valor);
            return true;
        }
        Nodo padre = buscarNodo(raiz, padreValor);
        if (padre == null) return false;
        if (lado.equalsIgnoreCase("I")) {
            if (padre.izq != null) return false;
            padre.izq = new Nodo(valor);
            return true;
        } else if (lado.equalsIgnoreCase("D")) {
            if (padre.der != null) return false;
            padre.der = new Nodo(valor);
            return true;
        }
        return false;
    }

    // Busca un nodo por valor
    protected Nodo buscarNodo(Nodo n, int v) {
        if (n == null) return null;
        if (n.valor == v) return n;
        Nodo izq = buscarNodo(n.izq, v);
        if (izq != null) return izq;
        return buscarNodo(n.der, v);
    }

    public void insertar(int v) { 
        //Metodo temporalmente deshabilitado para insertar elementos en arbolBinario
        if (!buscar(v)) {
            raiz = insertarRec(raiz, v); 
        }
    }
    protected Nodo insertarRec(Nodo n, int v) {
        //Metodo temporalmente deshabilitado para insertar elementos en arbolBinario
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
            if (height((NodoAVL)n.izq.izq) < height((NodoAVL)n.izq.der)) {
                registrarRotacion(n.valor, "Rotación Izq-Der");
                n.izq = rotateLeft((NodoAVL)n.izq);
            }
            registrarRotacion(n.valor, "Rotación Derecha");
            return rotateRight(n);
        }
        if (bf < -1) {
            if (height((NodoAVL)n.der.der) < height((NodoAVL)n.der.izq)) {
                registrarRotacion(n.valor, "Rotación Der-Izq");
                n.der = rotateRight((NodoAVL)n.der);
            }
            registrarRotacion(n.valor, "Rotación Izquierda");
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

    // --- Evento de rotación para animación AVL ---
    class RotacionEvento {
        int pivoteValor;
        String tipo;
        RotacionEvento(int v, String t) { pivoteValor = v; tipo = t; }
    }
    private java.util.List<RotacionEvento> rotaciones = new java.util.ArrayList<>();
    private boolean registrarRotaciones = false;
    public void iniciarRegistroRotaciones() { rotaciones.clear(); registrarRotaciones = true; }
    public java.util.List<RotacionEvento> obtenerRotaciones() { registrarRotaciones = false; return new java.util.ArrayList<>(rotaciones); }
    private void registrarRotacion(int pivoteValor, String tipo) {
        if (registrarRotaciones) rotaciones.add(new RotacionEvento(pivoteValor, tipo));
    }
}

// B-Tree orden minimo de 2
class ArbolB implements ArbolBase {
    private final int m; // orden
    private final int T; // mínimo de hijos (ceil(m/2))
    class NodoB {
        int[] claves;
        NodoB[] hijos;
        int n;
        boolean hoja;
        NodoB() {
            claves = new int[m-1+1]; // +1 para overflow temporal
            hijos = new NodoB[m+1];
            n = 0; hoja = true;
        }
    }
    private NodoB raiz;
    public ArbolB(int orden) {
        if (orden < 3) orden = 3;
        m = orden;
        T = (int)Math.ceil(m/2.0);
        raiz = new NodoB();
    }
    @Override
    public void insertar(int k) {
        if (!buscar(k)) {
            NodoB r = raiz;
            if (r.n == m-1) { // Si está lleno (m-1 claves), dividir
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
            if (x.hijos[i].n == m-1) {
                split(x, i, x.hijos[i]);
                if (k > x.claves[i]) i++;
            }
            insertNonFull(x.hijos[i], k);
        }
    }
    private void split(NodoB p, int i, NodoB y) {
        NodoB z = new NodoB(); z.hoja = y.hoja; z.n = T-1;
        // Copia las últimas T-1 claves de y a z
        for (int j = 0; j < T-1; j++) z.claves[j] = y.claves[j+T];
        if (!y.hoja) for (int j = 0; j < T; j++) z.hijos[j] = y.hijos[j+T];
        y.n = T-1;
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

    @Override
    public NodoVisual getVisualTree() {
        NodoVisualB raizB = convertBVisual(raiz);
        return wrapBVisual(raizB); // Convierte a NodoVisual para que el panel lo pueda dibujar
    }
    public NodoVisualB getRaizVisualB() { return convertBVisual(raiz); }
    public NodoB getRaiz() { return raiz; }
    // Convierte el árbol B a la estructura visual fiel
    public NodoVisualB convertBVisual(NodoB x) {
        if (x == null || x.n == 0) return null;
        NodoVisualB v = new NodoVisualB(x.claves, x.n);
        if (!x.hoja) {
            v.hijos = new NodoVisualB[x.n + 1];
            for (int i = 0; i <= x.n; i++) {
                v.hijos[i] = convertBVisual(x.hijos[i]);
            }
        }
        return v;
    }

    // Convierte NodoVisualB a NodoVisual para que el panel lo dibuje (todos los hijos en horizontal)
    private NodoVisual wrapBVisual(NodoVisualB b) {
        if (b == null) return null;
        // Un solo nodo visual para todas las claves
        NodoVisual v = new NodoVisual(b.claves[0]);
        v.extraInfo = Arrays.toString(Arrays.copyOf(b.claves, b.n));
        // Conecta todos los hijos como una lista horizontal a partir de izq
        NodoVisual prevHijo = null;
        if (b.hijos != null && b.hijos.length > 0) {
            for (int i = 0; i < b.hijos.length; i++) {
                NodoVisual hijo = wrapBVisual(b.hijos[i]);
                if (i == 0) {
                    v.izq = hijo;
                    if (hijo != null) hijo.padre = v;
                    prevHijo = hijo;
                } else {
                    if (prevHijo != null) {
                        prevHijo.der = hijo;
                        if (hijo != null) hijo.padre = prevHijo;
                    }
                    prevHijo = hijo;
                }
            }
        }
        return v;
    }
    
    /*
    @Override public NodoVisual getVisualTree() { return convert(raiz); }
    private NodoVisual convert(NodoB x) {
        if (x == null || x.n == 0) return null;
        // Usamos la primera clave como valor principal solo para dibujar el círculo
        NodoVisual v = new NodoVisual(x.claves[0]);
        v.extraInfo = Arrays.toString(Arrays.copyOf(x.claves, x.n));
        // Para visualizar todos los hijos, los encadenamos a la derecha
        NodoVisual actual = v;
        for (int i = 1; i < x.n; i++) {
            NodoVisual siguiente = new NodoVisual(x.claves[i]);
            siguiente.extraInfo = ""; // Ya están todas las claves en el primero
            actual.der = siguiente;
            siguiente.padre = actual;
            actual = siguiente;
        }
        // Ahora conectamos los hijos (si no es hoja)
        if (!x.hoja) {
            NodoVisual hijoVisual = null;
            NodoVisual hijoActual = v;
            for (int i = 0; i <= x.n; i++) {
                NodoVisual hijo = convert(x.hijos[i]);
                if (i == 0) {
                    hijoVisual = hijo;
                    if (hijoVisual != null) hijoVisual.padre = v;
                    v.izq = hijoVisual;
                } else {
                    if (hijoActual != null) {
                        hijoActual.izq = hijo;
                        if (hijo != null) hijo.padre = hijoActual;
                        hijoActual = hijoActual.der;
                    }
                }
            }
        }
        */
        /*
        NodoVisual v = new NodoVisual(x.claves[0]);
        v.extraInfo = Arrays.toString(Arrays.copyOf(x.claves, x.n));
        if (!x.hoja) {
            v.izq = convert(x.hijos[0]); if (v.izq != null) v.izq.padre = v;
            v.der = convert(x.hijos[1]); if (v.der != null) v.der.padre = v;
        }
        //return v;
    }*/
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

    public int getOrden() { return m; }
}

class BotonRedondeado extends JButton {
    private final int arc;
    private boolean hover = false;

    public BotonRedondeado(String text, int arc) {
        super(text);
        this.arc = arc;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        // Detecta hover
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hover = true;
                repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Fondo redondeado
        Color base = getBackground();
        Color fondo = hover ? base.brighter() : base;
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        // Dibuja el texto centrado
        FontMetrics fm = g2.getFontMetrics(getFont());
        String text = getText();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2 - 2;
        g2.setColor(getForeground());
        g2.setFont(getFont());
        g2.drawString(text, x, y);

        // Si hay icono, dibújalo (opcional)
        if (getIcon() != null) {
            getIcon().paintIcon(this, g2, 6, (getHeight() - getIcon().getIconHeight()) / 2);
        }

        g2.dispose();
    }
}

class BordeRedondeado extends javax.swing.border.AbstractBorder {
    private final int arc;
    private final Color color;
    public BordeRedondeado(int arc, Color color) {
        this.arc = arc;
        this.color = color;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x+1, y+1, width-3, height-3, arc, arc);
        g2.dispose();
    }
    @Override
    public Insets getBorderInsets(Component c) { return new Insets(10, 10, 10, 10); }
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = 10;
        return insets;
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
    private JTextField entrada = new JTextField(5);
    private JComboBox<String> tipo;
    Color azulCeleste = new Color(140, 170, 255);
    Color azulCelesteClaro = new Color(200, 220, 255);

    public ArbolesGUI() {
        setTitle("Visualizador Árboles"); setSize(800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE); setLocationRelativeTo(null);
        
        // Panel principal
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollDibujo = new JScrollPane(dibujo, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollDibujo, BorderLayout.CENTER);

        // Controles
        JPanel ctrl = new JPanel();
        tipo = new JComboBox<>(new String[]{"AB","ABB","AVL","B"});
        ctrl.add(tipo);
        ctrl.add(new JLabel("Valor:")); ctrl.add(entrada);

        JButton ins = new BotonRedondeado("Insertar", 18);
        JButton del = new BotonRedondeado("Eliminar", 18);
        JButton bus = new BotonRedondeado("Buscar", 18);
        JButton rec = new BotonRedondeado("Recorrer", 18);
        ctrl.add(ins); ctrl.add(del); ctrl.add(bus); ctrl.add(rec);
        panel.add(ctrl, BorderLayout.NORTH);

        JButton guardar = new BotonRedondeado("Guardar", 18);
        JButton cargar = new BotonRedondeado("Cargar", 18);

        ctrl.add(guardar);
        ctrl.add(cargar);

        guardar.addActionListener(e -> guardarArbol());
        cargar.addActionListener(e -> cargarArbol());

        // Área de salida con scroll y altura fija
        salida.setRows(2);
        salida.setLineWrap(true);
        salida.setWrapStyleWord(true);
        salida.setEditable(false);
        JScrollPane scrollSalida = new JScrollPane(salida, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollSalida.setPreferredSize(new Dimension(800, salida.getFont().getSize() * 3 + 10));
        panel.add(scrollSalida, BorderLayout.SOUTH);
        add(panel);

        scrollSalida.setBorder(new BordeRedondeado(18, new Color(120, 180, 255)));
        estilizarBoton(ins);
        estilizarBoton(del);
        estilizarBoton(bus);
        estilizarBoton(rec);
        estilizarBoton(guardar);
        estilizarBoton(cargar);

        estilizarCombo(tipo);
        estilizarTextField(entrada);

        salida.setBackground(new Color(235, 245, 255));
        salida.setForeground(new Color(30, 60, 120));
        salida.setFont(new Font("Arial", Font.PLAIN, 14));
        salida.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        UIManager.put("ScrollBar.width", 12); // Scrollbar más delgada

        javax.swing.plaf.basic.BasicScrollBarUI customScrollBarUI = new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0, 120, 60, 180); // Verde oscuro translúcido
                this.trackColor = new Color(230, 255, 230);   // Verde muy claro
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 12, 12);
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 12, 12);
                g2.dispose();
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMinimumSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                btn.setVisible(false);
                return btn;
            }
        };

        scrollDibujo.getVerticalScrollBar().setUI(customScrollBarUI);
        scrollDibujo.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0, 120, 60, 180);
                this.trackColor = new Color(230, 255, 230);
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 12, 12);
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 12, 12);
                g2.dispose();
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMinimumSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                btn.setVisible(false);
                return btn;
            }
        });

        scrollSalida.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0, 120, 60, 180);
                this.trackColor = new Color(230, 255, 230);
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 12, 12);
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 12, 12);
                g2.dispose();
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMinimumSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                btn.setVisible(false);
                return btn;
            }
        });

        // Al cambiar tipo, actualizar visibilidad y crear B si aplica
        tipo.addActionListener(e -> {
            String t = (String) tipo.getSelectedItem();
            dibujo.limpiarTodo();
            if (t.equals("B")) {
                int o = 0;
                while (o < 3) {
                    String input = JOptionPane.showInputDialog(this, "Ingrese el orden del árbol B (mínimo 3):", "6");
                    if (input == null) return; // Cancelado
                    try {
                        o = Integer.parseInt(input.trim());
                        if (o < 3) {
                            JOptionPane.showMessageDialog(this, "El orden debe ser al menos 3.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                b = new ArbolB(o); // Nueva instancia con orden correcto
                activo = b;
                dibujo.setRaizVisualB(b.convertBVisual(b.getRaiz()));
            } else if (t.equals("ABB")) {
                abb = new ArbolBusqueda();
                activo = abb;
                dibujo.setRaizVisual(abb.getVisualTree());
            } else if (t.equals("AVL")) {
                avl = new ArbolAVL();
                activo = avl;
                dibujo.setRaizVisual(avl.getVisualTree());
            } else {
                ab = new ArbolBinario();
                activo = ab;
                dibujo.setRaizVisual(ab.getVisualTree());
            }
            if (animacionActual != null) animacionActual.cancelar();
            dibujo.repaint();
        });
       	// Acciones botones
        ins.addActionListener(e -> operar("insertar"));
        del.addActionListener(e -> operar("eliminar"));
        bus.addActionListener(e -> operar("buscar"));
        rec.addActionListener(e -> mostrarDialogoRecorrido());

        // Inicial activo
        activo = ab;
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(azulCeleste);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        //boton.setContentAreaFilled(true);
        //boton.setOpaque(true);
        //boton.setBorderPainted(true);
        boton.setBorder(new BordeRedondeado(18, azulCeleste.darker()));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setBackground(azulCelesteClaro);
        combo.setForeground(new Color(30, 60, 120));
        combo.setFont(new Font("Arial", Font.BOLD, 14));
        combo.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(azulCeleste);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });
    }

    private void estilizarTextField(JTextField campo) {
        campo.setBackground(new Color(220, 235, 255));
        campo.setForeground(new Color(30, 60, 120));
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 255), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    // Elimina un nodo por valor (solo desconecta el nodo, no reestructura)
    private void eliminarNodoAB(ArbolBinario ab, int valor) {
        if (ab.raiz == null) {
            salida.append("\nEl árbol está vacío.");
            return;
        }
        // Caso especial: eliminar la raíz
        if (ab.raiz.valor == valor) {
            ab.raiz = null;
            salida.append("\nVértice raíz eliminado. Árbol vacío.");
            return;
        }
        Nodo padre = buscarPadre(ab.raiz, valor);
        if (padre == null) {
            salida.append("\nVértice no encontrado.");
            return;
        }
        if (padre.izq != null && padre.izq.valor == valor) {
            padre.izq = null;
        } else if (padre.der != null && padre.der.valor == valor) {
            padre.der = null;
        }
        salida.append("\nVértice eliminado exitosamente.");
    }

    // Busca el padre de un nodo por valor
    private Nodo buscarPadre(Nodo nodo, int valor) {
        if (nodo == null) return null;
        if ((nodo.izq != null && nodo.izq.valor == valor) ||
            (nodo.der != null && nodo.der.valor == valor)) {
            return nodo;
        }
        Nodo izq = buscarPadre(nodo.izq, valor);
        if (izq != null) return izq;
        return buscarPadre(nodo.der, valor);
    }

    // --- Encapsulador de animaciones de búsqueda ---
    private AnimacionBusqueda animacionActual;
    private class AnimacionBusqueda {
        private Timer timer;
        private Object tipoActivo;
        AnimacionBusqueda(Timer timer, Object tipoActivo) {
            this.timer = timer;
            this.tipoActivo = tipoActivo;
        }
        void cancelar() {
            if (timer != null) timer.stop();
        }
        boolean sigueActivo(Object actual) {
            return actual == tipoActivo;
        }
    }

    private void animarBusquedaAB(NodoVisual raiz, int valorBuscado) {
        if (animacionActual != null) animacionActual.cancelar();
        java.util.List<NodoVisual> camino = new java.util.ArrayList<>();
        if (!buscarCaminoVisual(raiz, valorBuscado, camino)) {
            salida.append("\nNo se encontró el valor " + valorBuscado);
            return;
        }
        final Object tipoAnimacion = activo;
        Timer timer = new Timer(700, null);
        animacionActual = new AnimacionBusqueda(timer, tipoAnimacion);
        final int[] idx = {0};
        timer.addActionListener(e -> {
            if (!animacionActual.sigueActivo(activo)) { timer.stop(); return; }
            if (idx[0] < camino.size()) {
                NodoVisual actual = camino.get(idx[0]);
                actual.color = Color.YELLOW;
                if (actual.padre != null) {
                    if (actual.padre.izq == actual) actual.padre.conexionIzq = true;
                    if (actual.padre.der == actual) actual.padre.conexionDer = true;
                }
                dibujo.setRaizVisual(raiz);
                idx[0]++;
            } else {
                NodoVisual ultimo = camino.get(camino.size()-1);
                if (ultimo.valor == valorBuscado) {
                    ultimo.color = Color.GREEN;
                    salida.append("\nEncontrado: " + valorBuscado);
                } else {
                    ultimo.color = Color.RED;
                    salida.append("\nNo se encontró el valor " + valorBuscado);
                }
                dibujo.setRaizVisual(raiz);
                timer.stop();
                // Limpiar colores después de un tiempo
                Timer t2 = new Timer(1200, ev -> {
                    if (!animacionActual.sigueActivo(activo)) { ((Timer)ev.getSource()).stop(); return; }
                    limpiarColoresVisual(raiz);
                    dibujo.setRaizVisual(raiz);
                });
                t2.setRepeats(false);
                t2.start();
            }
        });
        limpiarColoresVisual(raiz);
        dibujo.setRaizVisual(raiz);
        timer.start();
    }

    private void animarBusquedaB(NodoVisualB raiz, int valorBuscado) {
        if (animacionActual != null) animacionActual.cancelar();
        java.util.List<NodoVisualB> camino = new java.util.ArrayList<>();
        if (!buscarCaminoB(raiz, valorBuscado, camino)) {
            salida.append("\nNo se encontró el valor " + valorBuscado);
            return;
        }
        final Object tipoAnimacion = activo;
        Timer timer = new Timer(700, null);
        animacionActual = new AnimacionBusqueda(timer, tipoAnimacion);
        final int[] idx = {0};
        limpiarColoresVisualB(raiz);
        dibujo.setRaizVisualB(raiz);
        timer.addActionListener(e -> {
            if (!animacionActual.sigueActivo(activo)) { timer.stop(); return; }
            if (idx[0] < camino.size()) {
                NodoVisualB actual = camino.get(idx[0]);
                actual.color = Color.YELLOW;
                actual.claveResaltada = -1; // No resaltar aún
                dibujo.setRaizVisualB(raiz);
                idx[0]++;
            } else {
                NodoVisualB ultimo = camino.get(camino.size()-1);
                int claveIdx = -1;
                for (int i = 0; i < ultimo.n; i++) {
                    if (ultimo.claves[i] == valorBuscado) {
                        claveIdx = i;
                        break;
                    }
                }
                boolean encontrado = claveIdx != -1;
                ultimo.color = encontrado ? Color.GREEN : Color.RED;
                ultimo.claveResaltada = claveIdx; // Resalta la clave encontrada (o ninguna si -1)
                salida.append(encontrado ? "\nEncontrado: " + valorBuscado : "\nNo se encontró el valor " + valorBuscado);
                dibujo.setRaizVisualB(raiz);
                timer.stop();
                // Limpiar colores después de un tiempo
                Timer t2 = new Timer(1200, ev -> {
                    if (!animacionActual.sigueActivo(activo)) { ((Timer)ev.getSource()).stop(); return; }
                    limpiarColoresVisualB(raiz);
                    dibujo.setRaizVisualB(raiz);
                });
                t2.setRepeats(false);
                t2.start();
            }
        });
        timer.start();
    }

    // Busca el camino de búsqueda en el árbol B
    private boolean buscarCaminoB(NodoVisualB n, int valor, java.util.List<NodoVisualB> camino) {
        if (n == null) return false;
        camino.add(n);
        int i = 0;
        while (i < n.n && valor > n.claves[i]) i++;
        if (i < n.n && valor == n.claves[i]) return true;
        if (n.hijos == null || n.hijos.length == 0) return false;
        return buscarCaminoB(n.hijos[i], valor, camino);
    }

    // Limpia colores en el árbol B visual
    private void limpiarColoresVisualB(NodoVisualB n) {
        if (n == null) return;
        n.color = new Color(210, 210, 255);
        n.claveResaltada = -1;
        if (n.hijos != null) {
            for (NodoVisualB h : n.hijos) limpiarColoresVisualB(h);
        }
    }

    // Busca el camino hasta el nodo (o hasta donde termina la búsqueda)
    private boolean buscarCaminoVisual(NodoVisual n, int valor, java.util.List<NodoVisual> camino) {
        if (n == null) return false;
        camino.add(n);
        if (n.valor == valor) return true;
        if (buscarCaminoVisual(n.izq, valor, camino)) return true;
        if (buscarCaminoVisual(n.der, valor, camino)) return true;
        camino.remove(camino.size()-1);
        return false;
    }

    // Limpia colores y conexiones visuales
    private void limpiarColoresVisual(NodoVisual n) {
        if (n == null) return;
        n.color = new Color(210, 210, 255);
        n.conexionIzq = false;
        n.conexionDer = false;
        limpiarColoresVisual(n.izq);
        limpiarColoresVisual(n.der);
    }

    // --- Animación de eliminación ABB: parpadeo en rojo el nodo a eliminar y azul el sucesor, antes y después de la eliminación ---
    private void animarEliminacionABB(int valor) {
        NodoVisual raizVisual = dibujo.getRaizVisual();
        java.util.List<NodoVisual> camino = new java.util.ArrayList<>();
        if (!buscarCaminoVisual(raizVisual, valor, camino)) {
            salida.append("\nNo se encontró el valor " + valor);
            return;
        }
        NodoVisual nodoAEliminar = camino.get(camino.size()-1);
        NodoVisual sucesor = null;
        Integer sucesorValor = null;
        boolean esAVL = activo instanceof ArbolAVL;
        ArbolAVL avlRef = esAVL ? (ArbolAVL)activo : null;
        if (nodoAEliminar.izq != null && nodoAEliminar.der != null) {
            sucesor = nodoAEliminar.der;
            while (sucesor.izq != null) sucesor = sucesor.izq;
            sucesorValor = sucesor.valor;
        }
        Runnable trasEliminar = () -> {
            if (esAVL) {
                java.util.List<ArbolAVL.RotacionEvento> rots = avlRef.obtenerRotaciones();
                animarRotacionesAVL(rots, 0, () -> {
                    salida.append("\nEliminado: " + valor);
                    dibujo.setRaizVisual(activo.getVisualTree());
                });
            } else {
                salida.append("\nEliminado: " + valor);
                dibujo.setRaizVisual(activo.getVisualTree());
            }
        };
        if (sucesorValor != null) {
            NodoVisual[] nodosRojo = {nodoAEliminar};
            NodoVisual[] nodosAzul = {sucesor};
            final int sucesorValorFinal = sucesorValor;
            final int pasos = 1;
            final int delay = 500;
            Timer timer = new Timer(delay, null);
            final int[] paso = {0};
            if (esAVL) avlRef.iniciarRegistroRotaciones();
            timer.addActionListener(e -> {
                if (paso[0] % 2 == 0) {
                    nodosRojo[0].color = Color.RED;
                    nodosAzul[0].color = Color.BLUE;
                } else {
                    nodosRojo[0].color = new Color(210, 210, 255);
                    nodosAzul[0].color = new Color(210, 210, 255);
                }
                dibujo.repaint();
                paso[0]++;
                if (paso[0] >= pasos * 2) {
                    timer.stop();
                    nodosRojo[0].color = new Color(210, 210, 255);
                    nodosAzul[0].color = new Color(210, 210, 255);
                    dibujo.repaint();
                    activo.eliminar(valor);
                    dibujo.setRaizVisual(activo.getVisualTree());
                    NodoVisual nuevaRaiz = dibujo.getRaizVisual();
                    NodoVisual nuevoSucesor = null;
                    if (nuevaRaiz != null) {
                        java.util.List<NodoVisual> caminoS = new java.util.ArrayList<>();
                        buscarCaminoVisual(nuevaRaiz, sucesorValorFinal, caminoS);
                        if (!caminoS.isEmpty()) nuevoSucesor = caminoS.get(caminoS.size()-1);
                    }
                    if (nuevoSucesor != null) {
                        NodoVisual[] sucesorArr = {nuevoSucesor};
                        animarNodos(sucesorArr, trasEliminar, Color.BLUE, new Color(210, 210, 255), Color.BLUE);
                    } else {
                        trasEliminar.run();
                    }
                }
            });
            timer.start();
        } else {
            NodoVisual[] nodos = {nodoAEliminar};
            if (esAVL) avlRef.iniciarRegistroRotaciones();
            animarNodos(nodos, () -> {
                activo.eliminar(valor);
                trasEliminar.run();
            }, Color.RED, new Color(210, 210, 255), Color.RED);
        }
    }

    // --- Animación de rotaciones AVL tras eliminación ---
    private void animarRotacionesAVL(java.util.List<ArbolAVL.RotacionEvento> rots, int idx, Runnable fin) {
        if (rots == null || idx >= rots.size()) { if (fin != null) fin.run(); return; }
        ArbolAVL.RotacionEvento ev = rots.get(idx);
        NodoVisual raiz = dibujo.getRaizVisual();
        java.util.List<NodoVisual> camino = new java.util.ArrayList<>();
        buscarCaminoVisual(raiz, ev.pivoteValor, camino);
        if (camino.isEmpty()) { animarRotacionesAVL(rots, idx+1, fin); return; }
        NodoVisual pivote = camino.get(camino.size()-1);
        java.util.List<NodoVisual> afectados = new java.util.ArrayList<>();
        afectados.add(pivote);
        if (pivote.izq != null) afectados.add(pivote.izq);
        if (pivote.der != null) afectados.add(pivote.der);
        NodoVisual[] arr = afectados.toArray(new NodoVisual[0]);
        animarNodos(arr, () -> {
            dibujo.setRaizVisual(activo.getVisualTree());
            animarNodos(arr, () -> animarRotacionesAVL(rots, idx+1, fin), Color.GREEN, new Color(210, 210, 255), Color.GREEN);
        }, Color.ORANGE, new Color(210, 210, 255), Color.ORANGE);
    }

    // Método para ejecutar operación y mostrar resultado
    private void operar(String op) {
        try {
            int v = Integer.parseInt(entrada.getText());
            switch(op) {
                case "insertar":
                    // Árbol binario normal (AB)
                    if (activo instanceof ArbolBinario && !(activo instanceof ArbolBusqueda)) {
                        ArbolBinario ab = (ArbolBinario)activo;
                        if (ab.raiz == null) {
                            ab.raiz = new Nodo(v);
                            salida.append("\nInsertado raíz: " + v);
                            dibujo.setRaizVisual(ab.getVisualTree());
                        } else {
                            String padreStr = JOptionPane.showInputDialog(this, "Ingrese el valor del nodo padre:");
                            if (padreStr == null || padreStr.trim().isEmpty()) return;
                            int padreValor = Integer.parseInt(padreStr.trim());
                            Nodo padre = ab.buscarNodo(ab.raiz, padreValor);
                            if (padre == null) {
                                salida.append("\nNo existe el nodo padre " + padreValor);
                                return;
                            }
                            String lado = JOptionPane.showInputDialog(this, "¿Insertar a la izquierda (I) o derecha (D)?");
                            if (lado == null || (!lado.equalsIgnoreCase("I") && !lado.equalsIgnoreCase("D"))) {
                                salida.append("\nOpción inválida. Use I o D.");
                                return;
                            }
                            boolean ok = ab.insertar(v, padreValor, lado);
                            if (!ok) {
                                salida.append("\nNo se pudo insertar. Puede que ya exista el valor, el padre no exista, o el lado esté ocupado.");
                            } else {
                                salida.append("\nInsertado: " + v + " como hijo " + (lado.equalsIgnoreCase("I") ? "izquierdo" : "derecho") + " de " + padreValor);
                            }
                            dibujo.setRaizVisual(ab.getVisualTree());
                        }
                    }
                    // Árbol B
                    else if (activo instanceof ArbolB) {
                        ArbolB arbolB = (ArbolB)activo;
                        if (arbolB.buscar(v)) {
                            salida.append("\nEl valor " + v + " ya existe en el árbol B");
                        } else {
                            arbolB.insertar(v);
                            salida.append("\nInsertado: " + v + " en Árbol B");
                        }
                        dibujo.setRaizVisualB(arbolB.convertBVisual(arbolB.getRaiz()));
                    }
                    // Otros tipos (ABB, AVL)
                    else {
                        if (activo.buscar(v)) {
                            salida.append("\nEl valor " + v + " ya existe en el árbol");
                        } else {
                            // --- INICIO: Animación de rotaciones AVL tras inserción ---
                            if (activo instanceof ArbolAVL) {
                                ArbolAVL avlRef = (ArbolAVL)activo;
                                avlRef.iniciarRegistroRotaciones();
                                activo.insertar(v);
                                java.util.List<ArbolAVL.RotacionEvento> rots = avlRef.obtenerRotaciones();
                                dibujo.setRaizVisual(activo.getVisualTree());
                                if (!rots.isEmpty()) {
                                    animarRotacionesAVL(rots, 0, () -> {
                                        salida.append("\nInsertado: " + v);
                                        dibujo.setRaizVisual(activo.getVisualTree());
                                    });
                                } else {
                                    salida.append("\nInsertado: " + v);
                                    dibujo.setRaizVisual(activo.getVisualTree());
                                }
                            } else {
                                activo.insertar(v);
                                salida.append("\nInsertado: " + v);
                                dibujo.setRaizVisual(activo.getVisualTree());
                            }
                            // --- FIN: Animación de rotaciones AVL tras inserción ---
                        }
                    }
                    break;

                case "eliminar":
                    // Árbol binario normal (AB)
                    if (activo instanceof ArbolBinario && !(activo instanceof ArbolBusqueda)) {
                        ArbolBinario ab = (ArbolBinario)activo;
                        eliminarNodoAB(ab, v);
                        dibujo.setRaizVisual(ab.getVisualTree());
                    }
                    // Árbol B
                    else if (activo instanceof ArbolB) {
                        ArbolB arbolB = (ArbolB)activo;
                        if (arbolB.buscar(v)) {
                            arbolB.eliminar(v);
                            salida.append("\nEliminado: " + v + " de Árbol B");
                        } else {
                            salida.append("\nEl valor " + v + " no existe en el Árbol B");
                        }
                        dibujo.setRaizVisualB(arbolB.convertBVisual(arbolB.getRaiz()));
                    }
                    // ABB o AVL
                    else if (activo instanceof ArbolBusqueda || activo instanceof ArbolAVL) {
                        if (activo.buscar(v)) {
                            animarEliminacionABB(v);
                        } else {
                            salida.append("\nEl valor " + v + " no existe en el árbol");
                            dibujo.setRaizVisual(activo.getVisualTree());
                        }
                    }
                    break;
                case "buscar":
                    boolean enc = activo.buscar(v);
                    salida.append("\nBuscando: " + v + (enc ? " existe" : " no existe"));
                    // Animación solo para AB, ABB, AVL (puedes mejorar para B si quieres)
                    if (activo instanceof ArbolBinario || activo instanceof ArbolBusqueda || activo instanceof ArbolAVL) {
                        NodoVisual raizVisual = activo.getVisualTree();
                        animarBusquedaAB(raizVisual, v);
                    }
                    // Para Árbol B, solo actualiza visualización (puedes implementar animación especial si gustas)
                    else if (activo instanceof ArbolB) {
                        ArbolB arbolB = (ArbolB)activo;
                        NodoVisualB raizB = arbolB.convertBVisual(arbolB.getRaiz());
                        animarBusquedaB(raizB, v);
                    }
                    break;
            }
        } catch(Exception ex) {
            salida.append("\nError: entrada inválida");
        }
    }

    // --- Diálogo y lógica de recorrido ---
    private void mostrarDialogoRecorrido() {
        String[] opciones = {"Inorden", "Preorden", "Postorden"};
        String tipoRec = (String) JOptionPane.showInputDialog(this, "Seleccione el tipo de recorrido:", "Recorrido", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (tipoRec == null) return;
        if (activo instanceof ArbolB) {
            // Para Árbol B, solo inorden tiene sentido clásico (los otros no son estándar)
            if (tipoRec.equals("Inorden")) {
                String resultado = activo.inorden();
                ArbolB arbolB = (ArbolB)activo;
                NodoVisualB raizB = arbolB.convertBVisual(arbolB.getRaiz());
                Runnable despues = () -> salida.append("\n" + tipoRec + ": " + resultado);
                animarRecorridoInordenB(raizB, despues);
            } else {
                JOptionPane.showMessageDialog(this, "Solo el recorrido inorden es estándar para Árbol B.", "No disponible", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        } else {
            NodoVisual raiz = activo.getVisualTree();
            String resFinal;
            switch (tipoRec) {
                case "Inorden":
                    resFinal = recorridoInorden();
                    break;
                case "Preorden":
                    resFinal = recorridoPreorden();
                    break;
                case "Postorden":
                    resFinal = recorridoPostorden();
                    break;
                default:
                    resFinal = "";
            }
            final String resultadoFinal = resFinal;
            Runnable despues = () -> salida.append("\n" + tipoRec + ": " + resultadoFinal);
            animarRecorrido(raiz, tipoRec, despues);
        }
    }

    private String recorridoInorden() {
        if (activo == null) return "";
        if (activo instanceof ArbolB) return activo.inorden();
        NodoVisual raiz = activo.getVisualTree();
        StringBuilder sb = new StringBuilder();
        inordenRec(raiz, sb);
        return sb.toString();
    }
    private void inordenRec(NodoVisual n, StringBuilder sb) {
        if (n == null) return;
        inordenRec(n.izq, sb);
        sb.append(n.valor).append(' ');
        inordenRec(n.der, sb);
    }
    private String recorridoPreorden() {
        if (activo == null) return "";
        NodoVisual raiz = activo.getVisualTree();
        StringBuilder sb = new StringBuilder();
        preordenRec(raiz, sb);
        return sb.toString();
    }
    // --- NUEVO: Recolectar preorden para guardar ABB y AVL ---
    private void recolectarPreorden(Nodo n, java.util.List<Integer> lista) {
        if (n == null) return;
        lista.add(n.valor);
        recolectarPreorden(n.izq, lista);
        recolectarPreorden(n.der, lista);
    }
    private void preordenRec(NodoVisual n, StringBuilder sb) {
        if (n == null) return;
        sb.append(n.valor).append(' ');
        preordenRec(n.izq, sb);
        preordenRec(n.der, sb);
    }
    private String recorridoPostorden() {
        if (activo == null) return "";
        NodoVisual raiz = activo.getVisualTree();
        StringBuilder sb = new StringBuilder();
        postordenRec(raiz, sb);
        return sb.toString();
    }
    private void postordenRec(NodoVisual n, StringBuilder sb) {
        if (n == null) return;
        postordenRec(n.izq, sb);
        postordenRec(n.der, sb);
        sb.append(n.valor).append(' ');
    }

    // Método 1: Recolectar nodos en orden de recorrido
    private void recolectarRecorrido(NodoVisual n, java.util.List<NodoVisual> lista, String tipo) {
        if (n == null) return;
        switch (tipo) {
            case "Inorden":
                recolectarRecorrido(n.izq, lista, tipo);
                lista.add(n);
                recolectarRecorrido(n.der, lista, tipo);
                break;
            case "Preorden":
                lista.add(n);
                recolectarRecorrido(n.izq, lista, tipo);
                recolectarRecorrido(n.der, lista, tipo);
                break;
            case "Postorden":
                recolectarRecorrido(n.izq, lista, tipo);
                recolectarRecorrido(n.der, lista, tipo);
                lista.add(n);
                break;
        }
    }

    // Método 2: Animar el recorrido
    private void animarRecorrido(NodoVisual raiz, String tipoRecorrido, Runnable despues) {
        java.util.List<NodoVisual> recorrido = new java.util.ArrayList<>();
        recolectarRecorrido(raiz, recorrido, tipoRecorrido);
        if (recorrido.isEmpty()) {
            if (despues != null) despues.run();
            return;
        }
        final int[] idx = {0};
        Timer timer = new Timer(600, null);
        timer.addActionListener(e -> {
            if (idx[0] > 0) recorrido.get(idx[0] - 1).color = new Color(210, 210, 255);
            if (idx[0] < recorrido.size()) {
                NodoVisual actual = recorrido.get(idx[0]);
                actual.color = Color.ORANGE;
                dibujo.setRaizVisual(raiz);
                idx[0]++;
            } else {
                timer.stop();
                if (!recorrido.isEmpty()) recorrido.get(recorrido.size() - 1).color = new Color(210, 210, 255);
                dibujo.setRaizVisual(raiz);
                if (despues != null) despues.run();
            }
        });
        limpiarColoresVisual(raiz);
        dibujo.setRaizVisual(raiz);
        timer.start();
    }

    // Recolecta pares (nodo, índice de clave) en orden inorden para Árbol B
    private void recolectarInordenB(NodoVisualB n, java.util.List<int[]> lista) {
        if (n == null) return;
        for (int i = 0; i < n.n; i++) {
            if (n.hijos != null && n.hijos[i] != null) recolectarInordenB(n.hijos[i], lista);
            lista.add(new int[]{System.identityHashCode(n), i}); // Usamos hash para identificar el nodo
        }
        if (n.hijos != null && n.hijos[n.n] != null) recolectarInordenB(n.hijos[n.n], lista);
    }

    // Busca el nodo visual B por su identityHashCode
    private NodoVisualB buscarNodoPorHash(NodoVisualB n, int hash) {
        if (n == null) return null;
        if (System.identityHashCode(n) == hash) return n;
        if (n.hijos != null) {
            for (NodoVisualB h : n.hijos) {
                NodoVisualB res = buscarNodoPorHash(h, hash);
                if (res != null) return res;
            }
        }
        return null;
    }

    // Animación de recorrido inorden para Árbol B
    private void animarRecorridoInordenB(NodoVisualB raizB, Runnable despues) {
        java.util.List<int[]> recorrido = new java.util.ArrayList<>();
        recolectarInordenB(raizB, recorrido);
        if (recorrido.isEmpty()) {
            if (despues != null) despues.run();
            return;
        }
        final int[] idx = {0};
        Timer timer = new Timer(700, null);
        timer.addActionListener(e -> {
            if (idx[0] > 0) {
                int[] prev = recorrido.get(idx[0] - 1);
                NodoVisualB nodoPrev = buscarNodoPorHash(raizB, prev[0]);
                if (nodoPrev != null) {
                    nodoPrev.color = new Color(210, 210, 255);
                    nodoPrev.claveResaltada = -1;
                }
            }
            if (idx[0] < recorrido.size()) {
                int[] actual = recorrido.get(idx[0]);
                NodoVisualB nodo = buscarNodoPorHash(raizB, actual[0]);
                if (nodo != null) {
                    nodo.color = Color.ORANGE;
                    nodo.claveResaltada = actual[1];
                }
                dibujo.setRaizVisualB(raizB);
                idx[0]++;
            } else {
                timer.stop();
                if (!recorrido.isEmpty()) {
                    int[] ult = recorrido.get(recorrido.size() - 1);
                    NodoVisualB nodoUlt = buscarNodoPorHash(raizB, ult[0]);
                    if (nodoUlt != null) {
                        nodoUlt.color = new Color(210, 210, 255);
                        nodoUlt.claveResaltada = -1;
                    }
                }
                dibujo.setRaizVisualB(raizB);
                if (despues != null) despues.run();
            }
        });
        limpiarColoresVisualB(raizB);
        dibujo.setRaizVisualB(raizB);
        timer.start();
    }

    // --- Módulo reutilizable para animaciones de parpadeo de nodos/aristas ---
    private void animarNodos(NodoVisual[] nodos, Runnable despues, Color... colores) {
        if (nodos == null || nodos.length == 0 || colores == null || colores.length == 0) {
            if (despues != null) despues.run();
            return;
        }
        Timer timer = new Timer(350, null);
        final int[] paso = {0};
        timer.addActionListener(e -> {
            int idx = paso[0] % colores.length;
            for (NodoVisual nv : nodos) nv.color = colores[idx];
            dibujo.repaint();
            paso[0]++;
            if (paso[0] >= colores.length) {
                timer.stop();
                for (NodoVisual nv : nodos) nv.color = new Color(210, 210, 255);
                dibujo.repaint();
                if (despues != null) despues.run();
            }
        });
        timer.start();
    }
    private void animarNodosB(NodoVisualB[] nodos, Runnable despues, Color... colores) {
        if (nodos == null || nodos.length == 0 || colores == null || colores.length == 0) {
            if (despues != null) despues.run();
            return;
        }
        Timer timer = new Timer(350, null);
        final int[] paso = {0};
        timer.addActionListener(e -> {
            int idx = paso[0] % colores.length;
            for (NodoVisualB nv : nodos) nv.color = colores[idx];
            dibujo.repaint();
            paso[0]++;
            if (paso[0] >= colores.length) {
                timer.stop();
                for (NodoVisualB nv : nodos) nv.color = new Color(210, 210, 255);
                dibujo.repaint();
                if (despues != null) despues.run();
            }
        });
        timer.start();
    }

    // GUARDAR ÁRBOL
    private void guardarArbol() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
                String tipoStr = (String) tipo.getSelectedItem();
                pw.println(tipoStr); // Guardamos el tipo de árbol

                if (tipoStr.equals("B")) {
                    // Guardar el orden del árbol B
                    int orden = (b != null) ? b.getOrden() : 6;
                    pw.println(orden);
                }

                if (tipoStr.equals("AB")) {
                    guardarAB((ArbolBinario) activo, pw, null);
                } else if (tipoStr.equals("ABB") || tipoStr.equals("AVL")) {
                    // Guardar en preorden para preservar estructura
                    java.util.List<Integer> preorden = new java.util.ArrayList<>();
                    Nodo raizABB = null;
                    if (activo instanceof ArbolBusqueda) raizABB = ((ArbolBusqueda)activo).raiz;
                    else if (activo instanceof ArbolAVL) raizABB = ((ArbolAVL)activo).raiz;
                    recolectarPreorden(raizABB, preorden);
                    for (int val : preorden) pw.println(val);
                } else {
                    // Para B: solo guardamos los valores en inorden
                    String[] valores = activo.inorden().trim().split("\\s+");
                    for (String val : valores) {
                        pw.println(val);
                    }
                }

                salida.append("\nÁrbol guardado correctamente.");
            } catch (Exception ex) {
                salida.append("\nError al guardar: " + ex.getMessage());
            }
        }
    }

    // Guarda el árbol binario completo (AB) con estructura
    private void guardarAB(ArbolBinario ab, PrintWriter pw, Nodo padre) {
        if (ab == null || ab.raiz == null) return;
        guardarABRec(ab.raiz, pw, padre, ""); // Raíz no tiene padre
    }

    private void guardarABRec(Nodo actual, PrintWriter pw, Nodo padre, String lado) {
        if (actual == null) return;
        if (padre == null) {
            pw.println("ROOT " + actual.valor);
        } else {
            pw.println(padre.valor + " " + lado + " " + actual.valor);
        }
        guardarABRec(actual.izq, pw, actual, "I");
        guardarABRec(actual.der, pw, actual, "D");
    }

    // CARGAR ÁRBOL
    private void cargarArbol() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new java.io.FileReader(chooser.getSelectedFile()))) {
                String tipoStr = br.readLine().trim();
                tipo.setSelectedItem(tipoStr);

                if (tipoStr.equals("B")) {
                    String ordenStr = br.readLine().trim();
                    int orden = Integer.parseInt(ordenStr);
                    b = new ArbolB(orden);
                    activo = b;
                } else if (tipoStr.equals("ABB")) {
                    abb = new ArbolBusqueda();
                    activo = abb;
                } else if (tipoStr.equals("AVL")) {
                    avl = new ArbolAVL();
                    activo = avl;
                } else {
                    ab = new ArbolBinario();
                    activo = ab;
                }

                String linea;
                while ((linea = br.readLine()) != null) {
                    if (tipoStr.equals("AB")) {
                        String[] partes = linea.trim().split("\\s+");
                        if (partes[0].equals("ROOT")) {
                            ab.raiz = new Nodo(Integer.parseInt(partes[1]));
                        } else {
                            int padre = Integer.parseInt(partes[0]);
                            String lado = partes[1];
                            int valor = Integer.parseInt(partes[2]);
                            ab.insertar(valor, padre, lado);
                        }
                    } else {
                        if (!linea.trim().isEmpty())
                            activo.insertar(Integer.parseInt(linea.trim()));
                    }
                }

                salida.append("\nÁrbol cargado correctamente.");
                // Actualiza la visualización
                if (activo instanceof ArbolB) {
                    dibujo.setRaizVisualB(((ArbolB)activo).convertBVisual(((ArbolB)activo).getRaiz()));
                } else {
                    dibujo.setRaizVisual(activo.getVisualTree());
                }

            } catch (Exception ex) {
                salida.append("\nError al cargar: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArbolesGUI().setVisible(true));
    }
}