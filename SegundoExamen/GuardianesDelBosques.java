import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class GuardianesDelBosques extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;

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

        mainPanel.add(menuPrincipal, "MENU");
        mainPanel.add(modulos, "MODULOS");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel crearPantallaInicio() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(193, 193, 143));
        panel.setLayout(null);

        ImageIcon arbolIcon1 = new ImageIcon(new ImageIcon("Recursos\\arbol.png").getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
        JLabel arbolIzquierdo = new JLabel(arbolIcon1);
        arbolIzquierdo.setBounds(50, 200, 250, 250);
        panel.add(arbolIzquierdo);

        ImageIcon arbolIcon2 = new ImageIcon(new ImageIcon("Recursos\\arbol.png").getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
        JLabel arbolDerecho  = new JLabel(arbolIcon2);
        arbolDerecho .setBounds(700, 200, 250, 250);
        panel.add(arbolDerecho);

        // Subtítulo con fuente 2
        JLabel subtitulo = new JLabel("BIENVENIDOS A LA CAPACITACIÓN PARA", SwingConstants.CENTER);
        subtitulo.setBounds(150, 50, 700, 30);
        subtitulo.setForeground(Color.BLACK);
        subtitulo.setFont(cargarFuente("Recursos\\fuenteTitulo.ttf", 25f));
        panel.add(subtitulo);

        // Título con fuente 1
        JLabel titulo = new JLabel("GUARDIANES DEL BOSQUE", SwingConstants.CENTER);
        titulo.setBounds(150, 100, 700, 60);
        titulo.setForeground(Color.BLACK);
        titulo.setFont(cargarFuente("Recursos\\fuenteTitulo.ttf", 55f));
        panel.add(titulo);

        JButton botonIniciar = new JButton("INICIAR");
        botonIniciar.setFont(cargarFuente("Recursos\\fuenteTitulo.ttf", 30f));
        botonIniciar.setBackground(new Color(51, 51, 25));
        botonIniciar.setForeground(Color.WHITE);
        botonIniciar.setBounds(400, 400, 200, 50);
        botonIniciar.setFocusPainted(false);
        botonIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "MODULOS");
            }
        });
        panel.add(botonIniciar);

        ImageIcon iconoX = new ImageIcon(new ImageIcon("Recursos\\botonX.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
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
        JPanel panel = new JPanel();
        panel.setBackground(new Color(193, 193, 143));
        panel.setLayout(null);

        JLabel titulo = new JLabel("Módulo de aprendizaje", SwingConstants.CENTER);
        titulo.setBounds(200, 30, 600, 40);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        panel.add(titulo);

        JLabel subtitulo = new JLabel("<html>Necesitas superar 3 módulos de aprendizaje, con sus respectivos ejercicios,<br>para convertirte en: ¡Guardián del Bosque!</html>", SwingConstants.CENTER);
        subtitulo.setBounds(200, 80, 600, 50);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(subtitulo);

        panel.add(crearModulo("Módulo 1:\nExploración de ecosistemas", 100, true, true));
        panel.add(crearModulo("Módulo 2:\nOptimización de rutas de recolección", 400, true, false));
        panel.add(crearModulo("Módulo 3:\nDiseño de redes ecológicas", 700, false, false));

        JButton certificado = new JButton("DESCARGAR CERTIFICADO (BLOQUEADO)");
        certificado.setBounds(300, 550, 400, 40);
        certificado.setBackground(new Color(51, 51, 25));
        certificado.setForeground(Color.WHITE);
        certificado.setFocusPainted(false);
        certificado.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(certificado);

        ImageIcon personaje = new ImageIcon(new ImageIcon("personaje.png").getImage().getScaledInstance(60, 100, Image.SCALE_SMOOTH));
        JLabel personajeLabel = new JLabel(personaje);
        personajeLabel.setBounds(470, 590, 60, 100);
        panel.add(personajeLabel);

        return panel;
    }

    private JPanel crearModulo(String texto, int x, boolean puedeAprender, boolean puedeEjercitar) {
        JPanel modulo = new JPanel();
        modulo.setLayout(null);
        modulo.setBackground(Color.WHITE);
        modulo.setBounds(x, 160, 200, 180);

        JLabel titulo = new JLabel("<html>" + texto.replace("\n", "<br>") + "</html>", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titulo.setBounds(10, 10, 180, 60);
        modulo.add(titulo);

        JButton boton1 = new JButton(puedeAprender ? "Aprender ahora" : "BLOQUEADO");
        boton1.setBounds(20, 100, 160, 30);
        boton1.setBackground(new Color(51, 51, 25));
        boton1.setForeground(Color.WHITE);
        boton1.setFocusPainted(false);
        boton1.setFont(new Font("SansSerif", Font.BOLD, 12));
        modulo.add(boton1);

        JButton boton2 = new JButton((puedeAprender && puedeEjercitar) ? "Hacer el ejercicio" : "BLOQUEADO");
        boton2.setBounds(20, 140, 160, 30);
        boton2.setBackground(new Color(51, 51, 25));
        boton2.setForeground(Color.WHITE);
        boton2.setFocusPainted(false);
        boton2.setFont(new Font("SansSerif", Font.BOLD, 12));
        modulo.add(boton2);

        return modulo;
    }

     // Método para cargar fuentes
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
