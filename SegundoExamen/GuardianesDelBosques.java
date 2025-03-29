import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class GuardianesDelBosques extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;
    private JTextArea textoGuia; // Variable de instancia para modificar el texto
    private boolean primeraParte = true; // Para controlar el estado del texto


    public GuardianesDelBosques() {
        setTitle("Guardianes del Bosque");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Pantalla de bienvenida
        JPanel menuPrincipal = crearPantallaInicio();
        // Pantalla de módulos
        JPanel modulos = PantallaModulos();
        // Pantalla Introductoria Seccion1
        JPanel modulo1 = crearModulo1();

        mainPanel.add(menuPrincipal, "MENU");
        mainPanel.add(modulos, "MODULOS");
        mainPanel.add(modulo1, "MODULO1");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel crearPantallaInicio() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(193, 193, 143));
        panel.setLayout(null);

        JLabel bosque = new JLabel(new ImageIcon("SegundoExamen\\Recursos\\bosque.png"));
        bosque.setBounds(150, 200, 700, 300);
        panel.add(bosque);

        // Subtítulo
        JLabel subtitulo = new JLabel("BIENVENIDOS A LA CAPACITACIÓN PARA", SwingConstants.CENTER);
        subtitulo.setBounds(150, 50, 700, 30);
        subtitulo.setForeground(Color.BLACK);
        subtitulo.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 25f));
        panel.add(subtitulo);

        // Título
        JLabel titulo = new JLabel("GUARDIANES DEL BOSQUE", SwingConstants.CENTER);
        titulo.setBounds(150, 100, 700, 60);
        titulo.setForeground(Color.BLACK);
        titulo.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 55f));
        panel.add(titulo);

        JButton botonIniciar = new JButton("INICIAR");
        botonIniciar.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 30f));
        botonIniciar.setBackground(new Color(51, 51, 25));
        botonIniciar.setForeground(Color.WHITE);
        botonIniciar.setBounds(400, 530, 200, 50);
        botonIniciar.setFocusPainted(false);
        botonIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "MODULOS");
            }
        });
        panel.add(botonIniciar);

        ImageIcon iconoX = new ImageIcon(new ImageIcon("SegundoExamen\\Recursos\\botonX.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        JButton botonSalir = new JButton(iconoX);
        botonSalir.setBounds(900, 20, 40, 40);
        botonSalir.setFocusPainted(false);
        botonSalir.setBorderPainted(false);
        botonSalir.setContentAreaFilled(false);

        // Acción de salida
        botonSalir.addActionListener(e -> System.exit(0));

        panel.add(botonSalir);

        return panel;
    }
    
    private JPanel PantallaModulos() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(new Color(206, 212, 169));

        // Título principal
        JLabel titulo = new JLabel("Módulo de aprendizaje", SwingConstants.CENTER);
        titulo.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 40f));
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));
        contenedor.add(titulo, BorderLayout.NORTH);

        // Subtítulo
        JLabel subtitulo = new JLabel("<html><div style='text-align: center;'>Necesitas superar TRES módulos de aprendizaje,<br>con sus respectivos ejercicios</div></html>", SwingConstants.CENTER);
        subtitulo.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 25f));
        subtitulo.setBounds(150, 80, 700, 60);

        contenedor.add(subtitulo);

        // Panel de módulos
        JPanel panelModulos = new JPanel();
        panelModulos.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelModulos.setOpaque(false);
        panelModulos.setBorder(BorderFactory.createEmptyBorder(100, 0, 20, 0));

        panelModulos.add(crearModulo("Módulo UNO:<br>Exploración de ecosistemas", "SegundoExamen\\Recursos\\modulo1.png", true));
        panelModulos.add(crearModulo("Módulo DOS:<br>Optimización<br>de rutas", "SegundoExamen\\Recursos\\modulo2.png", false));
        panelModulos.add(crearModulo("Módulo TRES:<br>Redes <br>ecológicas", "SegundoExamen\\Recursos\\modulo3.png", false));

        // Panel contenedor para centrar verticalmente
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(panelModulos, BorderLayout.CENTER);
        contenedor.add(centerPanel, BorderLayout.CENTER);

        // Botón de certificado bloqueado (esto cambia necesito añadir una variable que cambie el boleano cada que se complete una leccion)
        JButton botonCertificado = new JButton("DESCARGAR CERTIFICADO (BLOQUEADO)");
        botonCertificado.setEnabled(false);
        botonCertificado.setBackground(Color.DARK_GRAY);
        botonCertificado.setForeground(Color.WHITE);
        botonCertificado.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 15f));
        botonCertificado.setPreferredSize(new Dimension(350, 40));

        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(new Color(206, 212, 169));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 0, 90, 0));
        panelInferior.add(botonCertificado);

        contenedor.add(panelInferior, BorderLayout.SOUTH);

        return contenedor;
    }

    //crea el modulo de aprendizaje esto es como base y se llena en la de Pantalla de modulos
    private JPanel crearModulo(String titulo, String rutaImagen, boolean activo) {
        JPanel modulo = new JPanel();
        modulo.setPreferredSize(new Dimension(200, 300));
        modulo.setBackground(Color.WHITE);
        modulo.setLayout(new BoxLayout(modulo, BoxLayout.Y_AXIS));
        modulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel lblTitulo = new JLabel("<html><center>" + titulo + "</center></html>", SwingConstants.CENTER);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 14f));
        modulo.add(lblTitulo);
        modulo.add(Box.createVerticalStrut(15));

        if (activo) {
            JButton btnAprender = new JButton("Aprender ahora");
            btnAprender.setBackground(new Color(87, 124, 88));
            btnAprender.setForeground(Color.WHITE);
            btnAprender.setFocusPainted(false);
            btnAprender.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnAprender.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 12f));
            btnAprender.addActionListener(e -> cardLayout.show(mainPanel, "MODULO1"));
            modulo.add(btnAprender);
        } else {
            JButton bloqueado = botonBloqueado("BLOQUEADO");
            bloqueado.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 12f));
            modulo.add(bloqueado);
        }

        modulo.add(Box.createVerticalStrut(10));
        
        if (activo) {
            JButton ejercicio = botonEjercicio();
            ejercicio.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 12f));
            modulo.add(ejercicio);
        } else {
            JButton bloqueado = botonBloqueado("BLOQUEADO");
            bloqueado.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 12f));
            modulo.add(bloqueado);
        }

        modulo.add(Box.createVerticalStrut(15));
        ImageIcon icon = new ImageIcon(rutaImagen);
        JLabel imagen = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        imagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        modulo.add(imagen);

        return modulo;
    }

    private JButton botonBloqueado(String texto) {
        JButton boton = new JButton(texto);
        boton.setEnabled(false);
        boton.setBackground(new Color(40, 60, 40));
        boton.setForeground(Color.WHITE);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        return boton;
    }

    private JButton botonEjercicio() {
        JButton btn = new JButton("Hacer el ejercicio");
        btn.setBackground(new Color(87, 124, 88));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    
    private JPanel crearModulo1() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(206, 212, 169));
    
        // Panel izquierdo dividido en dos partes
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(new Color(199, 203, 165));
        panelIzquierdo.setPreferredSize(new Dimension(350, 700));
    
        // 1. Panel superior para títulos (ahora centrado)
        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(new Color(199, 203, 165));
        panelTitulos.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20)); // Márgenes equilibrados
        panelTitulos.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado horizontal
    
        JLabel lblTitulo = new JLabel("Módulo de aprendizaje");
        lblTitulo.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 24f));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado
    
        JLabel lblSubtitulo = new JLabel("EXPLORACION DE ECOSISTEMAS");
        lblSubtitulo.setFont(cargarFuente("SegundoExamen\\Recursos\\fuenteTitulo.ttf", 18f));
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setBackground(new Color(217, 120, 82));
        lblSubtitulo.setOpaque(true);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Más padding horizontal
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrado
    
        panelTitulos.add(Box.createVerticalGlue());
        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createRigidArea(new Dimension(0, 15)));
        panelTitulos.add(lblSubtitulo);
        panelTitulos.add(Box.createVerticalGlue());
    
        // 2. Panel inferior para el gato
        JPanel panelGato = new JPanel(new BorderLayout());
        panelGato.setBackground(new Color(199, 203, 165));
        panelGato.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
    
        ImageIcon iconoGato = new ImageIcon("SegundoExamen\\Recursos\\gatoR.PNG");
        Image imagenGato = iconoGato.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
        JLabel lblGato = new JLabel(new ImageIcon(imagenGato));
        lblGato.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelGato.add(lblGato, BorderLayout.CENTER);
    
        // Agregar ambos paneles al panel izquierdo
        panelIzquierdo.add(panelTitulos);
        panelIzquierdo.add(panelGato);
    
        // Panel derecho de instrucciones
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(226, 229, 203));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(150, 50, 150, 50));
    
        JTextArea textoGuia = new JTextArea(
            "Hola! Mi nombre es bob,\nseré tu guía que te ayudará\na convertirte en un...\n"
            + "¡Guardián del bosque!\nPero antes queremos que primero tengas\nen cuenta las definiciones de que es:"
            + "\n\n - BFS (Recorrido a lo ancho)\n - DFS (Recorrido a lo profundo)\n\n Asi que comencemos!"
        );
        textoGuia.setEditable(false);
        textoGuia.setLineWrap(true);
        textoGuia.setWrapStyleWord(true);
        textoGuia.setBackground(Color.WHITE);
        textoGuia.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 20f));
        textoGuia.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 25, 10, 20)
        ));
        textoGuia.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JButton btnContinuar = new JButton("CONTINUAR");
        btnContinuar.setBackground(new Color(63, 84, 54));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 14f));
        btnContinuar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnContinuar.addActionListener(e -> cardLayout.show(mainPanel, "MODULOS"));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        

        panelDerecho.add(Box.createVerticalGlue());
        panelDerecho.add(textoGuia);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 20)));
        panelDerecho.add(btnContinuar);
        panelDerecho.add(Box.createVerticalGlue());
    
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setDividerLocation(350);
        splitPane.setEnabled(false);
    
        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        
        return panelPrincipal;
    }
    
    // Cargar fuentes
    public Font cargarFuente(String ruta, float tamaño) {
        try {
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, new File(ruta));
            fuente = fuente.deriveFont(tamaño);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fuente);
            return fuente;
        } catch (IOException | FontFormatException e) {
            System.err.println("Error al cargar fuente: " + ruta);
            return new Font("SansSerif", Font.PLAIN, (int)tamaño);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuardianesDelBosques());
    }
}
