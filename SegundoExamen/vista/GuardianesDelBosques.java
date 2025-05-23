import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;


public class GuardianesDelBosques extends JFrame {

    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private Usuario usuarioActual;
    private Map<String, Usuario> usuariosMap = new HashMap<>(); 
    
    // Componentes UI
    private JComboBox<String> comboUsuarios;
    private JTextField txtNuevoUsuario;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JButton botonCertificado;
    private JButton botonEjercicioFinal;

    //clase usuario
    class Usuario {
        String nombre;
        boolean modulo1Completado;
        boolean modulo2Completado;
        boolean modulo3Completado;
        boolean ejercicioFinalCompletado;
        LocalDateTime ultimoAcceso;
        
        Usuario(String nombre) {
            this.nombre = nombre;
            this.modulo1Completado = false;
            this.modulo2Completado = false;
            this.modulo3Completado = false;
            this.ejercicioFinalCompletado = false;
            this.ultimoAcceso = LocalDateTime.now();
        }
        
        Usuario(String[] datos) {
            this.nombre = datos[0];
            this.modulo1Completado = Boolean.parseBoolean(datos[1]);
            this.modulo2Completado = Boolean.parseBoolean(datos[2]);
            this.modulo3Completado = Boolean.parseBoolean(datos[3]);
            this.ejercicioFinalCompletado = Boolean.parseBoolean(datos[4]);
            this.ultimoAcceso = datos.length > 5 ? LocalDateTime.parse(datos[5], DATE_FORMAT) : LocalDateTime.now();
        }

        public boolean isModuloDisponible(int modulo) {
            switch(modulo) {
                case 1: return true;  // Siempre disponible
                case 2: return modulo1Completado;
                case 3: return modulo2Completado;
                case 4: return modulo3Completado;
                default: return false;
            }
        }
        
        String toFileString() {
            return String.join("|",
                nombre,
                String.valueOf(modulo1Completado),
                String.valueOf(modulo2Completado),
                String.valueOf(modulo3Completado),
                String.valueOf(ejercicioFinalCompletado),
                ultimoAcceso.format(DATE_FORMAT)
            );
        }
        
        boolean todosModulosCompletados() {
            return modulo1Completado && modulo2Completado && modulo3Completado;
        }
    }

    //-----------------------------------------------------
    //       SECCION DE CREACION DE LOS PANELES
    //-----------------------------------------------------

    //creacion de las ventanas
    public GuardianesDelBosques() {
        usuariosMap = new HashMap<>();
        cargarUsuariosConProgreso();

        setTitle("Guardianes del Bosque");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Crear y añadir todas las pantallas
        mainPanel.add(PantallaInicio(), "MENU");
        mainPanel.add(PantallaUsuario(), "USUARIO");
        mainPanel.add(PantallaModulos(), "MODULOS");
        mainPanel.add(crearModulo1(), "MODULO1");
        mainPanel.add(crearModuloEjercicio1(), "EJERCICIO1");
        mainPanel.add(crearModulo2(), "MODULO2");
        mainPanel.add(crearModuloEjercicio2(), "EJERCICIO2");
        mainPanel.add(crearModulo3(), "MODULO3");
        mainPanel.add(crearModuloEjercicio3(), "EJERCICIO3");
        mainPanel.add(crearModuloEjercicioFinal(),"EJERCICIO_FINAL");
        mainPanel.add(PantallaCertificado(), "CERTIFICADO");

        add(mainPanel);
        setVisible(true);
    }

    //pantalla de bienvenida
    private JPanel PantallaInicio() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(193, 193, 143));
        panel.setLayout(null);

        JLabel bosque = new JLabel(new ImageIcon("SegundoExamen\\recursos\\bosque.png"));
        bosque.setBounds(150, 200, 700, 300);
        panel.add(bosque);

        // Subtítulo
        JLabel subtitulo = new JLabel("BIENVENIDOS A LA CAPACITACIÓN PARA", SwingConstants.CENTER);
        subtitulo.setBounds(150, 50, 700, 30);
        subtitulo.setForeground(Color.BLACK);
        subtitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 25f));
        panel.add(subtitulo);

        // Título
        JLabel titulo = new JLabel("GUARDIANES DEL BOSQUE", SwingConstants.CENTER);
        titulo.setBounds(150, 100, 700, 60);
        titulo.setForeground(Color.BLACK);
        titulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 55f));
        panel.add(titulo);

        JButton botonIniciar = new JButton("INICIAR");
        botonIniciar.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 30f));
        botonIniciar.setBackground(new Color(51, 51, 25));
        botonIniciar.setForeground(Color.WHITE);
        botonIniciar.setBounds(400, 530, 200, 50);
        botonIniciar.setFocusPainted(false);
        botonIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "USUARIO");
            }
        });
        panel.add(botonIniciar);

        ImageIcon iconoX = new ImageIcon(new ImageIcon("SegundoExamen\\recursos\\botonX.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
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
    
    //-----------------------------------------------------
    //   SECCION DE MODULOS DE INICIO Y REGISTRO
    //-----------------------------------------------------

    private JPanel PantallaUsuario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(193, 193, 143));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Título
        JLabel lblTitulo = new JLabel("Inicia Sesión");
        lblTitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 32f));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(30));

        // Panel de inicio de sesión
        panel.add(crearPanelLogin());
        panel.add(Box.createVerticalStrut(30));

        // Separador
        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(300, 1));
        panel.add(separador);
        panel.add(Box.createVerticalStrut(30));

        // Panel de registro
        panel.add(crearPanelRegistro());

        return panel;
    }

    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 16f));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblUsuario);

        panel.add(Box.createVerticalStrut(5));

        JLabel lblListaUsuarios = new JLabel("Selecciona tu usuario");
        lblListaUsuarios.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 16f));
        lblListaUsuarios.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblListaUsuarios);

        panel.add(Box.createVerticalStrut(15));

        // Combo box de usuarios
        comboUsuarios = new JComboBox<>(usuariosMap.keySet().toArray(new String[0]));
        comboUsuarios.setMaximumSize(new Dimension(250, 35));
        comboUsuarios.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(comboUsuarios);

        panel.add(Box.createVerticalStrut(25));

        // Botón de ingreso
        JButton btnIngresar = new JButton("Ingresar");
        estiloBoton(btnIngresar, new Color(55, 61, 32), 250);
        btnIngresar.addActionListener(e -> manejarIngresoUsuario());
        panel.add(btnIngresar);

        return panel;
    }

    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        JLabel lblRegistro = new JLabel("Regístrate!");
        lblRegistro.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 16f));
        lblRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblRegistro);

        panel.add(Box.createVerticalStrut(5));

        JLabel lblInstruccion = new JLabel("Ingresa tu nombre de usuario");
        lblInstruccion.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 16f));
        lblInstruccion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblInstruccion);

        panel.add(Box.createVerticalStrut(15));

        // Campo de texto para nuevo usuario
        txtNuevoUsuario = new JTextField();
        txtNuevoUsuario.setMaximumSize(new Dimension(250, 35));
        txtNuevoUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(txtNuevoUsuario);

        panel.add(Box.createVerticalStrut(25));

        // Botón de registro
        JButton btnRegistrar = new JButton("Registrar");
        estiloBoton(btnRegistrar, new Color(55, 61, 32), 250);
        btnRegistrar.addActionListener(e -> manejarRegistroUsuario());
        panel.add(btnRegistrar);

        return panel;
    }

    //--------------------------------------------------------------------
    //  SECCION DE VALIDACION, CREACION Y GUARDADO DE REGISTRO E INICIO 
    //--------------------------------------------------------------------

    private void cargarUsuariosConProgreso() {
        Path path = Paths.get(ARCHIVO_USUARIOS);
        if (Files.exists(path)) {
            try {
                List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
                for (String linea : lineas) {
                    if (!linea.trim().isEmpty()) {
                        String[] datos = linea.split("\\|");
                        if (datos.length == 6) {
                            try {
                                Usuario usuario = new Usuario(datos);
                                usuariosMap.put(usuario.nombre, usuario);
                            } catch (Exception e) {
                                System.err.println("Error al cargar usuario: " + linea);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                mostrarError("Error al cargar usuarios: " + e.getMessage());
            }
        }
    }

    private void guardarUsuariosConProgreso() {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(ARCHIVO_USUARIOS), 
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            
            for (Usuario usuario : usuariosMap.values()) {
                writer.write(usuario.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            mostrarError("Error al guardar usuarios: " + e.getMessage());
        }
    }

    private void manejarIngresoUsuario() {
        String nombreUsuario = (String) comboUsuarios.getSelectedItem();
        
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            mostrarError("Debes seleccionar un usuario válido");
            return;
        }
        
        usuarioActual = usuariosMap.get(nombreUsuario);
        usuarioActual.ultimoAcceso = LocalDateTime.now();
        
        // Mostrar mensaje de bienvenida con progreso
        String mensaje = "Bienvenido, " + usuarioActual.nombre + "!\n\n";
        mensaje += "Progreso actual:\n";
        mensaje += "- Módulo 1: " + (usuarioActual.modulo1Completado ? "✅" : "❌") + "\n";
        mensaje += "- Módulo 2: " + (usuarioActual.modulo2Completado ? "✅" : "❌") + "\n";
        mensaje += "- Módulo 3: " + (usuarioActual.modulo3Completado ? "✅" : "❌") + "\n";
        mensaje += "- Ejercicio Final: " + (usuarioActual.ejercicioFinalCompletado ? "✅" : "❌");
        
        JOptionPane.showMessageDialog(this, mensaje, "Ingreso exitoso", JOptionPane.INFORMATION_MESSAGE);
        

        // Actualizar archivo
        guardarUsuariosConProgreso();
        
        recargarPantallaModulos();

        // Ir a pantalla de módulos
        cardLayout.show(mainPanel, "MODULOS");
    }

    private void manejarRegistroUsuario() {
        String nuevoNombre = txtNuevoUsuario.getText().trim();
        
        if (nuevoNombre.isEmpty()) {
            mostrarError("El nombre no puede estar vacío");
            return;
        }
        
        if (usuariosMap.containsKey(nuevoNombre)) {
            mostrarError("Este usuario ya existe");
            return;
        }
        
        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario(nuevoNombre);
        usuariosMap.put(nuevoNombre, nuevoUsuario);
        comboUsuarios.addItem(nuevoNombre);
        txtNuevoUsuario.setText("");
        
        // Guardar en archivo
        guardarUsuariosConProgreso();
        
        JOptionPane.showMessageDialog(this, 
            "Usuario registrado exitosamente", 
            "Registro exitoso", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void estiloBoton(JButton boton, Color colorFondo, int ancho) {
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(ancho, 40));
        boton.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 14f));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    //-----------------------------------------------------
    //       SECCION DE CREACION DE MODULOS
    //-----------------------------------------------------

    //pantalla de seleccion de modulo 
    private JPanel PantallaModulos() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(new Color(206, 212, 169));

        // Título principal
        JLabel titulo = new JLabel("Módulo de aprendizaje", SwingConstants.CENTER);
        titulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 40f));
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));
        contenedor.add(titulo, BorderLayout.NORTH);

        // Subtítulo
        JLabel subtitulo = new JLabel("<html><div style='text-align: center;'>Necesitas superar TRES módulos de aprendizaje,<br>con sus respectivos ejercicios</div></html>", SwingConstants.CENTER);
        subtitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 25f));
        subtitulo.setBounds(150, 80, 700, 60);

        contenedor.add(subtitulo);

        // Panel de módulos
        JPanel panelModulos = new JPanel();
        panelModulos.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelModulos.setOpaque(false);
        panelModulos.setBorder(BorderFactory.createEmptyBorder(100, 0, 20, 0));

        // Versión corregida en PantallaModulos()
    panelModulos.add(crearModulo(
        "Módulo UNO:<br>Exploración de<br>ecosistemas", 
        "SegundoExamen\\recursos\\modulo1.png", 
        true,  // Módulo 1 siempre desbloqueado
        "MODULO1", 
        "EJERCICIO1"
    ));

    panelModulos.add(crearModulo(
        "Módulo DOS:<br>Optimización<br>de rutas", 
        "SegundoExamen\\recursos\\modulo2.png", 
        usuarioActual != null && usuarioActual.modulo1Completado, // Solo si módulo 1 completado
        "MODULO2", 
        "EJERCICIO2"
    ));

    panelModulos.add(crearModulo(
        "Módulo TRES:<br>Redes <br>ecológicas", 
        "SegundoExamen\\recursos\\modulo3.png", 
        usuarioActual != null && usuarioActual.modulo2Completado, // Solo si módulo 2 completado
        "MODULO3", 
        "EJERCICIO3"
    ));

        // Panel contenedor para centrar verticalmente
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(panelModulos, BorderLayout.CENTER);
        contenedor.add(centerPanel, BorderLayout.CENTER);



    //boton de ejercicio final
    botonEjercicioFinal = new JButton(usuarioActual != null && usuarioActual.modulo3Completado
    ? "REALIZAR EJERCICIO FINAL" 
    : "BLOQUEADO");
    botonEjercicioFinal.setEnabled(usuarioActual != null && usuarioActual.todosModulosCompletados());
    botonEjercicioFinal.setBackground(usuarioActual != null && usuarioActual.todosModulosCompletados() 
    ? new Color(87, 124, 88) 
    : Color.DARK_GRAY);
    botonEjercicioFinal.setForeground(Color.WHITE);
    botonEjercicioFinal.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 15f));
    botonEjercicioFinal.setPreferredSize(new Dimension(350, 40));

    if (usuarioActual != null && usuarioActual.todosModulosCompletados()) {
        botonEjercicioFinal.addActionListener(e -> {
            // Aquí deberías mostrar el ejercicio final
            cardLayout.show(mainPanel, "EJERCICIO_FINAL"); // Asegúrate de tener este panel
        });
    }

    // Botón de certificado (actualizado)
    botonCertificado = new JButton(usuarioActual != null && usuarioActual.todosModulosCompletados() && usuarioActual.ejercicioFinalCompletado
    ? "DESCARGAR CERTIFICADO" 
    : "DESCARGAR CERTIFICADO (BLOQUEADO)");
    botonCertificado.setEnabled(usuarioActual != null && usuarioActual.todosModulosCompletados() && usuarioActual.ejercicioFinalCompletado);
    botonCertificado.setBackground(usuarioActual != null && usuarioActual.todosModulosCompletados() && usuarioActual.ejercicioFinalCompletado
    ? new Color(87, 124, 88) 
    : Color.DARK_GRAY);
    botonCertificado.setForeground(Color.WHITE);
    botonCertificado.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 15f));
    botonCertificado.setPreferredSize(new Dimension(350, 40));

    // Añadir acción al botón
    if (usuarioActual != null && usuarioActual.todosModulosCompletados() && usuarioActual.ejercicioFinalCompletado) {
    botonCertificado.addActionListener(e -> cardLayout.show(mainPanel, "CERTIFICADO"));
    }

        // Agregar el botón de ejercicio final al layout correctamente
        JPanel panelBotonesInferiores = new JPanel(new FlowLayout());
        panelBotonesInferiores.add(botonEjercicioFinal);
        panelBotonesInferiores.add(botonCertificado);
        contenedor.add(panelBotonesInferiores, BorderLayout.SOUTH);

        return contenedor;
    }

    private JPanel crearModulo(String titulo, String nombreImagen, boolean habilitado, 
                         String idPanelTeoria, String idPanelEjercicio) {
        JPanel modulo = new JPanel();
        modulo.setPreferredSize(new Dimension(220, 320)); // Aumenté ligeramente el tamaño
        modulo.setBackground(Color.WHITE);
        modulo.setLayout(new BoxLayout(modulo, BoxLayout.Y_AXIS));
        modulo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), // Paréntesis añadido aquí
            BorderFactory.createEmptyBorder(20, 10, 10, 10)
        ));

        // Título del módulo
        JLabel lblTitulo = new JLabel("<html><div style='text-align:center;width:180px;'>" + titulo + "</div></html>", SwingConstants.CENTER);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 14f));
        modulo.add(lblTitulo);
        modulo.add(Box.createVerticalStrut(15));

        // Botón de Aprender
        JButton btnAprender = habilitado 
            ? crearBotonActivo("Aprender ahora", e -> cardLayout.show(mainPanel, idPanelTeoria))
            : botonBloqueado("BLOQUEADO");
        modulo.add(btnAprender);
        modulo.add(Box.createVerticalStrut(10));

        // Botón de Ejercicio
        JButton btnEjercicio = habilitado 
            ? crearBotonActivo("Comenzar Ejercicio", e -> cardLayout.show(mainPanel, idPanelEjercicio))
            : botonBloqueado("BLOQUEADO");
        modulo.add(btnEjercicio);
        modulo.add(Box.createVerticalStrut(15));

        // Imagen del módulo
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(nombreImagen));
            JLabel imagen = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
            imagen.setAlignmentX(Component.CENTER_ALIGNMENT);
            modulo.add(imagen);
        } catch (Exception e) {
            // Imagen por defecto si falla la carga
            JLabel lblErrorImagen = new JLabel("Imagen no disponible");
            lblErrorImagen.setAlignmentX(Component.CENTER_ALIGNMENT);
            modulo.add(lblErrorImagen);
        }

        return modulo;
    }


    // Método auxiliar para crear botones activos
    private JButton crearBotonActivo(String texto, ActionListener action) {
        JButton boton = new JButton(texto);
        boton.setBackground(new Color(87, 124, 88));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 12f));
        boton.setMaximumSize(new Dimension(180, 30));
        boton.addActionListener(action);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // Método para botones bloqueados 
    private JButton botonBloqueado(String texto) {
        JButton boton = new JButton(texto);
        boton.setEnabled(false);
        boton.setBackground(new Color(180, 180, 180));
        boton.setForeground(Color.DARK_GRAY);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 12f));
        boton.setMaximumSize(new Dimension(180, 30));
        return boton;
    }

    //-----------------------------------------------------
    //       SECCION DE MODULOS Y SUS EJERCICIOS
    //-----------------------------------------------------

    //Modulo 1
    private JPanel crearModulo1() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(206, 212, 169));
    
        // Panel izquierdo dividido en dos partes
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(new Color(199, 203, 165));
        panelIzquierdo.setPreferredSize(new Dimension(350, 700));
    
        // 1. Panel superior para títulos
        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(new Color(199, 203, 165));
        panelTitulos.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        panelTitulos.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JLabel lblTitulo = new JLabel("Módulo de aprendizaje");
        lblTitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 24f));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JLabel lblSubtitulo = new JLabel("EXPLORACION DE ECOSISTEMAS");
        lblSubtitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 18f));
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setBackground(new Color(217, 120, 82));
        lblSubtitulo.setOpaque(true);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        panelTitulos.add(Box.createVerticalGlue());
        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createRigidArea(new Dimension(0, 15)));
        panelTitulos.add(lblSubtitulo);
        panelTitulos.add(Box.createVerticalGlue());
    
        // 2. Panel inferior para el gato
        JPanel panelGato = new JPanel(new BorderLayout());
        panelGato.setBackground(new Color(199, 203, 165));
        panelGato.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
    
        ImageIcon iconoGato = new ImageIcon("SegundoExamen\\recursos\\gatoR.PNG");
        Image imagenGato = iconoGato.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
        JLabel lblGato = new JLabel(new ImageIcon(imagenGato));
        lblGato.setHorizontalAlignment(SwingConstants.CENTER);
    
        panelGato.add(lblGato, BorderLayout.CENTER);
    
        panelIzquierdo.add(panelTitulos);
        panelIzquierdo.add(panelGato);
    
        // Panel derecho de instrucciones
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(226, 229, 203));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(150, 50, 150, 50));
    
        JTextArea textoGuia = new JTextArea();
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
    
        // Lista de textos
        String[] textos = {
            "¡Hola! Mi nombre es Bob, seré tu guía que te ayudará a convertirte en un ¡Guardián del bosque!\n\nPero antes queremos que tengas en cuenta las definiciones de:\n - BFS (Recorrido a lo ancho)\n - DFS (Recorrido a lo profundo)",
            "BFS recorre el grafo por niveles, explorando todos los nodos vecinos antes de avanzar al siguiente nivel.\n\nDFS explora lo más profundo posible por cada camino antes de retroceder.",
            "¡Listo! Ya estás preparado para pasar a los ejercicios.\n¡Buena suerte, proximo guardián del bosque!"
        };
    
        final int[] paso = {0};
        textoGuia.setText(textos[paso[0]]);
    
        JButton btnContinuar = new JButton("CONTINUAR");
        btnContinuar.setBackground(new Color(63, 84, 54));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 14f));
        btnContinuar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        btnContinuar.addActionListener(e -> {
            paso[0]++;
            if (paso[0] < textos.length - 1) {
                textoGuia.setText(textos[paso[0]]);
            } else if (paso[0] == textos.length - 1) {
                textoGuia.setText(textos[paso[0]]);
                btnContinuar.setText("FINALIZAR");
            } else {
                cardLayout.show(mainPanel, "MODULOS");
            }
        });
    
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

    //ejercicio modulo 1
    private JPanel crearModuloEjercicio1() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(206, 212, 169));
        
        // Panel izquierdo (imagen y título)
        JPanel panelIzquierdo = crearPanelLateral(1);
        
        // Panel derecho (contenido del ejercicio)
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(226, 229, 203));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    
        // Array de preguntas y respuestas
        Object[][] preguntas = {
            {
                "¿Qué algoritmo explora todos los nodos vecinos primero?\n\n\n a) BFS \n\n b) DFS \n\n c) Dijkstra",
                "Opcion A", // Respuesta correcta
                new String[]{"Opcion A", "Opcion B", "Opcion C"} // Opciones
            },
            {
                "¿Qué algoritmo usa una estructura de pila para su implementación? \n\n\n a) BFS \n\n b) DFS \n\n c) Prim",
                "Opcion B",
                new String[]{"Opcion A", "Opcion B", "Opcion C"} // Opciones
            },
            {
                "¿Qué algoritmo es mejor para encontrar el camino más corto en un grafo no ponderado? \n\n\n a) Kruskal\n\n b) DFS \n\n c) BFS",
                "Opcion C",
                new String[]{"Opcion A", "Opcion B", "Opcion C"} // Opciones
            }
        };
    
        // Componentes que necesitamos actualizar
        JTextArea textoPregunta = new JTextArea();
        textoPregunta.setEditable(false);
        textoPregunta.setLineWrap(true);
        textoPregunta.setWrapStyleWord(true);
        textoPregunta.setBackground(new Color(226, 229, 203));
        textoPregunta.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 20f));
        textoPregunta.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
    
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 15, 0));
        panelBotones.setBackground(new Color(226, 229, 203));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 20, 70, 20));
    
        // Variables para controlar el flujo
        final int[] preguntaActual = {0};
        final int[] respuestasCorrectas = {0};
    
        // Método para actualizar la pregunta
        ActionListener manejarRespuesta = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton botonPresionado = (JButton) e.getSource();
                String respuestaUsuario = botonPresionado.getText();
                String respuestaCorrecta = (String) preguntas[preguntaActual[0]][1];
                
                if (respuestaUsuario.equals(respuestaCorrecta)) {
                    respuestasCorrectas[0]++;
                    if (preguntaActual[0] < preguntas.length - 1) {
                        preguntaActual[0]++;
                        mostrarPregunta(preguntaActual[0], textoPregunta, panelBotones, preguntas, this);
                    } else {
                        // Todas las preguntas respondidas
                        evaluarResultadoFinal(respuestasCorrectas[0], preguntas.length, 1);
                    }
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal, 
                        "Incorrecto.",
                        "Respuesta incorrecta", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        };
    
        // Mostrar primera pregunta
        mostrarPregunta(0, textoPregunta, panelBotones, preguntas, manejarRespuesta);
    
        panelDerecho.add(textoPregunta);
        panelDerecho.add(Box.createVerticalStrut(30));
        panelDerecho.add(panelBotones);
        panelDerecho.add(Box.createVerticalGlue());
    
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setDividerLocation(350);
        splitPane.setEnabled(false);
    
        panelPrincipal.add(splitPane, BorderLayout.CENTER);        
        return panelPrincipal;
    }
   
    //modulo 2
    private JPanel crearModulo2() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(206, 212, 169));
    
        // Panel izquierdo dividido en dos partes
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(new Color(199, 203, 165));
        panelIzquierdo.setPreferredSize(new Dimension(350, 700));
    
        // 1. Panel superior para títulos
        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(new Color(199, 203, 165));
        panelTitulos.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        panelTitulos.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JLabel lblTitulo = new JLabel("Módulo de aprendizaje");
        lblTitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 24f));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JLabel lblSubtitulo = new JLabel("EXPLORACION DE ECOSISTEMAS");
        lblSubtitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 18f));
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setBackground(new Color(217, 120, 82));
        lblSubtitulo.setOpaque(true);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        panelTitulos.add(Box.createVerticalGlue());
        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createRigidArea(new Dimension(0, 15)));
        panelTitulos.add(lblSubtitulo);
        panelTitulos.add(Box.createVerticalGlue());
    
        // 2. Panel inferior para el gato
        JPanel panelGato = new JPanel(new BorderLayout());
        panelGato.setBackground(new Color(199, 203, 165));
        panelGato.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
    
        ImageIcon iconoGato = new ImageIcon("SegundoExamen\\recursos\\gatoR.PNG");
        Image imagenGato = iconoGato.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
        JLabel lblGato = new JLabel(new ImageIcon(imagenGato));
        lblGato.setHorizontalAlignment(SwingConstants.CENTER);
    
        panelGato.add(lblGato, BorderLayout.CENTER);
    
        panelIzquierdo.add(panelTitulos);
        panelIzquierdo.add(panelGato);
    
        // Panel derecho de instrucciones
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(226, 229, 203));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(150, 50, 150, 50));
    
        JTextArea textoGuia = new JTextArea();
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
    
        // Lista de textos
        String[] textos = {
            "Es hora de que conozcas a Dijkstra y a floyd Warshall",
            " - Dijkstra es encontrar el camino más corto desde un nodo origen a todos los demás en grafos con pesos no negativos.\n\n - Floyd Warshall Calcular las distancias más cortas entre todos los pares de nodos en un grafo dirigido o no dirigido, incluso con pesos negativos (pero sin ciclos negativos).",
            "¡Listo! Ya estás preparado para pasar a los ejercicios.\n¡Buena suerte, proximo guardián del bosque!"
        };
    
        final int[] paso = {0};
        textoGuia.setText(textos[paso[0]]);
    
        JButton btnContinuar = new JButton("CONTINUAR");
        btnContinuar.setBackground(new Color(63, 84, 54));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 14f));
        btnContinuar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        btnContinuar.addActionListener(e -> {
            paso[0]++;
            if (paso[0] < textos.length - 1) {
                textoGuia.setText(textos[paso[0]]);
            } else if (paso[0] == textos.length - 1) {
                textoGuia.setText(textos[paso[0]]);
                btnContinuar.setText("FINALIZAR");
            } else {
                cardLayout.show(mainPanel, "MODULOS");
            }
        });
    
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
    
    //ejercicio modulo 2
    private JPanel crearModuloEjercicio2() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(206, 212, 169));
        
        // Panel izquierdo (imagen y título)
        JPanel panelIzquierdo = crearPanelLateral(2);
        
        // Panel derecho (contenido del ejercicio)
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(226, 229, 203));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    
        // Array de preguntas y respuestas
        Object[][] preguntas = {
            {
                "¿Cuál es la principal limitación del algoritmo de Dijkstra? \n\n\n a) No funciona con grafos que tienen pesos negativos en las aristas. \n\n b) Solo puede usarse en grafos no dirigidos. \n\n c) Su complejidad temporal",
                "Opcion A", //Respuesta correcta

                new String[]{"Opcion A", "Opcion B", "Opcion C"} // Opciones
            },
            {
                "¿Qué algoritmo usa una estructura de pila para su implementación? \n\n\n a) Es más rápido en grafos dispersos. \n\n b) Calcula las distancias más cortas entre todos los pares de nodos en una sola ejecución. \n\n c) No requiere una matriz de adyacencia.", 
                "Opcion B", //Respuesta correcta

                new String[]{"Opcion A", "Opcion B", "Opcion C"} // Opciones
            },
            {
                "¿Qué algoritmo es mejor para encontrar el camino más corto en un grafo no ponderado? \n\n\n a) Una pila (stack). \n\n b) Una cola de prioridad (priority queue). \n\n c) Una tabla hash.",
                "Opcion C", //Respuesta correcta

                new String[]{"Opcion A", "Opcion B", "Opcion C"} // Opciones
            }
        };
    
        // Componentes que necesitamos actualizar
        JTextArea textoPregunta = new JTextArea();
        textoPregunta.setEditable(false);
        textoPregunta.setLineWrap(true);
        textoPregunta.setWrapStyleWord(true);
        textoPregunta.setBackground(new Color(226, 229, 203));
        textoPregunta.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 20f));
        textoPregunta.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
    
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 15, 0));
        panelBotones.setBackground(new Color(226, 229, 203));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 20, 70, 20));
    
        // Variables para controlar el flujo
        final int[] preguntaActual = {0};
        final int[] respuestasCorrectas = {0};
    
        // Método para actualizar la pregunta
        ActionListener manejarRespuesta = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton botonPresionado = (JButton) e.getSource();
                String respuestaUsuario = botonPresionado.getText();
                String respuestaCorrecta = (String) preguntas[preguntaActual[0]][1];
                
                if (respuestaUsuario.equals(respuestaCorrecta)) {
                    respuestasCorrectas[0]++;
                    if (preguntaActual[0] < preguntas.length - 1) {
                        preguntaActual[0]++;
                        mostrarPregunta(preguntaActual[0], textoPregunta, panelBotones, preguntas, this);
                    } else {
                        // Todas las preguntas respondidas
                        evaluarResultadoFinal(respuestasCorrectas[0], preguntas.length, 2);
                    }
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal, 
                        "Incorrecto.",
                        "Respuesta incorrecta", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        };
    
        // Mostrar primera pregunta
        mostrarPregunta(0, textoPregunta, panelBotones, preguntas, manejarRespuesta);
    
        panelDerecho.add(textoPregunta);
        panelDerecho.add(Box.createVerticalStrut(30));
        panelDerecho.add(panelBotones);
        panelDerecho.add(Box.createVerticalGlue());
    
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setDividerLocation(350);
        splitPane.setEnabled(false);
    
        panelPrincipal.add(splitPane, BorderLayout.CENTER);        
        return panelPrincipal;
    }

    //modulo 3
    private JPanel crearModulo3() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(206, 212, 169));
    
        // Panel izquierdo dividido en dos partes
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(new Color(199, 203, 165));
        panelIzquierdo.setPreferredSize(new Dimension(350, 700));
    
        // 1. Panel superior para títulos
        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(new Color(199, 203, 165));
        panelTitulos.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        panelTitulos.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JLabel lblTitulo = new JLabel("Módulo de aprendizaje");
        lblTitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 24f));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JLabel lblSubtitulo = new JLabel("EXPLORACION DE ECOSISTEMAS");
        lblSubtitulo.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 18f));
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setBackground(new Color(217, 120, 82));
        lblSubtitulo.setOpaque(true);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        panelTitulos.add(Box.createVerticalGlue());
        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createRigidArea(new Dimension(0, 15)));
        panelTitulos.add(lblSubtitulo);
        panelTitulos.add(Box.createVerticalGlue());
    
        // 2. Panel inferior para el gato
        JPanel panelGato = new JPanel(new BorderLayout());
        panelGato.setBackground(new Color(199, 203, 165));
        panelGato.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
    
        ImageIcon iconoGato = new ImageIcon("SegundoExamen\\recursos\\gatoR.PNG");
        Image imagenGato = iconoGato.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
        JLabel lblGato = new JLabel(new ImageIcon(imagenGato));
        lblGato.setHorizontalAlignment(SwingConstants.CENTER);
    
        panelGato.add(lblGato, BorderLayout.CENTER);
    
        panelIzquierdo.add(panelTitulos);
        panelIzquierdo.add(panelGato);
    
        // Panel derecho de instrucciones
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(226, 229, 203));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(150, 50, 150, 50));
    
        JTextArea textoGuia = new JTextArea();
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
    
        // Lista de textos
        String[] textos = {
            "es tiempo de que conozcas prim y kruskal",
            " - Prim es encontrar un árbol de expansión mínima (MST) en un grafo conexo y ponderado es decir desde un nodo, expande la arista más cercana.\n\n - Kruskal también encuentra un MST, pero con enfoque diferente es decir ordena todas las aristas y añade las más cortas sin formar ciclo",
            "¡Listo! Ya estás preparado para pasar a los ejercicios.\n¡Buena suerte, proximo guardián del bosque!"
        };
    
        final int[] paso = {0};
        textoGuia.setText(textos[paso[0]]);
    
        JButton btnContinuar = new JButton("CONTINUAR");
        btnContinuar.setBackground(new Color(63, 84, 54));
        btnContinuar.setForeground(Color.WHITE);
        btnContinuar.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 14f));
        btnContinuar.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        btnContinuar.addActionListener(e -> {
            paso[0]++;
            if (paso[0] < textos.length - 1) {
                textoGuia.setText(textos[paso[0]]);
            } else if (paso[0] == textos.length - 1) {
                textoGuia.setText(textos[paso[0]]);
                btnContinuar.setText("FINALIZAR");
            } else {
                cardLayout.show(mainPanel, "MODULOS");
            }
        });
    
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
    
    //ejercicio modulo 3
    private JPanel crearModuloEjercicio3() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(206, 212, 169));
        
        // Panel izquierdo (imagen y título)
        JPanel panelIzquierdo = crearPanelLateral(3);
        
        // Panel derecho (contenido del ejercicio)
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(226, 229, 203));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    
        // Array de preguntas y respuestas
        Object[][] preguntas = {
            {
                "¿Qué algoritmo explora todos los nodos vecinos primero? \n\n\n a) Una cola de prioridad (Heap).\n\n b) Una estructura Union-Find (Disjoint Set). \n\n c) Una tabla hash.",
                "Opcion B", // Respuesta correcta
                new String[]{"Opcion A", "Opcion B", "Opcion C"} // Opciones
            },
            {
                "¿Por qué el algoritmo de Prim es adecuado para grafos densos?\n\n\n a) Porque procesa todas las aristas independientemente de su peso. \n\n b) Porque su complejidad depende principalmente del número de nodos. \n\n c) Porque usa búsqueda en profundidad (DFS).",
                "Opcion B",
                new String[]{"Opcion A", "Opcion B", "Opcion C"} // Opciones
            },
            {
                "¿Qué garantiza que ambos algoritmos encuentren un árbol de expansión mínima?\n\n\n a) Que siempre seleccionen la arista más pesada disponible.\n\n b) Que usen estrategias voraces (greedy) basadas en pesos mínimos. \n\n c) Que recorran el grafo en amplitud (BFS).",
                "Opcion B",
                new String[]{"Opcion A", "Opcion B", "Opcion C"} // Opciones
            }
        };
    
        // Componentes que necesitamos actualizar
        JTextArea textoPregunta = new JTextArea();
        textoPregunta.setEditable(false);
        textoPregunta.setLineWrap(true);
        textoPregunta.setWrapStyleWord(true);
        textoPregunta.setBackground(new Color(226, 229, 203));
        textoPregunta.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 20f));
        textoPregunta.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
    
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 15, 0));
        panelBotones.setBackground(new Color(226, 229, 203));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 20, 70, 20));
    
        // Variables para controlar el flujo
        final int[] preguntaActual = {0};
        final int[] respuestasCorrectas = {0};
    
        // Método para actualizar la pregunta
        ActionListener manejarRespuesta = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton botonPresionado = (JButton) e.getSource();
                String respuestaUsuario = botonPresionado.getText();
                String respuestaCorrecta = (String) preguntas[preguntaActual[0]][1];
                
                if (respuestaUsuario.equals(respuestaCorrecta)) {
                    respuestasCorrectas[0]++;
                    if (preguntaActual[0] < preguntas.length - 1) {
                        preguntaActual[0]++;
                        mostrarPregunta(preguntaActual[0], textoPregunta, panelBotones, preguntas, this);
                    } else {
                        // Todas las preguntas respondidas
                        evaluarResultadoFinal(respuestasCorrectas[0], preguntas.length, 3);
                    }
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal, 
                        "Incorrecto.",
                        "Respuesta incorrecta", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        };
    
        // Mostrar primera pregunta
        mostrarPregunta(0, textoPregunta, panelBotones, preguntas, manejarRespuesta);
    
        panelDerecho.add(textoPregunta);
        panelDerecho.add(Box.createVerticalStrut(30));
        panelDerecho.add(panelBotones);
        panelDerecho.add(Box.createVerticalGlue());
    
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setDividerLocation(350);
        splitPane.setEnabled(false);
    
        panelPrincipal.add(splitPane, BorderLayout.CENTER);        
        return panelPrincipal;
    }

    //Ejercicio final
    private JPanel crearModuloEjercicioFinal() {
        PanelManejadorMapa panelManejador = new PanelManejadorMapa(this);
        return panelManejador; 
    }

    //Certificado
    private JPanel PantallaCertificado() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(206, 212, 169));
        
        JLabel certificado = new JLabel("<html><center><h1>CERTIFICADO</h1><br>" +
            "Se otorga a:<br><h2>" + (usuarioActual != null ? usuarioActual.nombre : "") + "</h2><br>" +
            "Por completar todos los módulos de Guardianes del Bosque</center></html>", 
            SwingConstants.CENTER);
        
            certificado.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 24f));
            panelPrincipal.add(certificado, BorderLayout.CENTER);
        
        // Botón para regresar
        JButton btnRegresar = new JButton("Regresar");
        estiloBoton(btnRegresar, new Color(76, 175, 80), 200);
        btnRegresar.addActionListener(e -> cardLayout.show(mainPanel, "MODULOS"));
        
        JPanel panelSur = new JPanel();
        panelSur.add(btnRegresar);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
        
        return panelPrincipal;
    }

    //-----------------------------------------------------
    //       SECCION DE VALIDACIONES
    //-----------------------------------------------------

    //Esto solo muestra cuando algo esta erroneo
    private void mostrarPregunta(int numPregunta, JTextArea textoPregunta, JPanel panelBotones, 
                               Object[][] preguntas, ActionListener listener) {
        textoPregunta.setText((String) preguntas[numPregunta][0]);
        panelBotones.removeAll();
        
        String[] opciones = (String[]) preguntas[numPregunta][2];
        
        for (String opcion : opciones) {
            JButton btnOpcion = new JButton(opcion);
            estiloBotonPregunta(btnOpcion);
            btnOpcion.addActionListener(listener);
            panelBotones.add(btnOpcion);
        }
        
        panelBotones.revalidate();
        panelBotones.repaint();
    }


    //Evalua los resultados
    public void evaluarResultadoFinal(int correctas, int totalPreguntas, int moduloActual){
        if (correctas >= totalPreguntas * 0.7) {
                // Marcar el módulo actual como completado
                switch(moduloActual) {
                    case 1:
                        usuarioActual.modulo1Completado = true;
                        break;
                    case 2:
                        usuarioActual.modulo2Completado = true;
                        break;
                    case 3:
                        usuarioActual.modulo3Completado = true;
                        break;
                    case 4: // Ejercicio final
                        usuarioActual.ejercicioFinalCompletado = true;
                        break;
                }
                
                guardarUsuariosConProgreso();
                usuariosMap.put(usuarioActual.nombre, usuarioActual);
                
                // Determinar el mensaje según el módulo completado
                String mensaje = "";
                if (moduloActual == 1) {
                    mensaje = "¡Felicidades! Has completado el módulo 1\n\n¡Se desbloqueó la sección 2!";
                } else if (moduloActual == 2) {
                    mensaje = "¡Felicidades! Has completado el módulo 2\n\n¡Se desbloqueó la sección 3!";
                } else if (moduloActual == 3) {
                    mensaje = "¡Felicidades! Has completado el módulo 3\n\n¡Se desbloqueó el ejercicio final!";
                } else if (moduloActual == 4) {
                    mensaje = "¡Felicidades! Has completado todos los módulos\n\n¡Se desbloqueó el certificado!";
                }
                
                JOptionPane.showMessageDialog(this, 
                    mensaje,
                    "Módulo completado", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                recargarPantallaModulos();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Necesitas más práctica. Intenta nuevamente.",
                    "Intenta de nuevo", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }

    //buffer de los modulos en si los actualiza
    public void recargarPantallaModulos() {

        Component[] components = mainPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i].getName() != null && components[i].getName().equals("MODULOS")) {
                mainPanel.remove(i);
                break;
            }
        }
        mainPanel.add(PantallaModulos(), "MODULOS");
        cardLayout.show(mainPanel, "MODULOS");
    }

    //Creacion de panel solo como atajo
    private JPanel crearPanelLateral(int moduloActual) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(199, 203, 165));
        panel.setPreferredSize(new Dimension(350, 700));
    
        // Panel de títulos
        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.setBackground(new Color(199, 203, 165));
        panelTitulos.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        panelTitulos.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JLabel lblTitulo = new JLabel("Sección de preguntas!");
        lblTitulo.setFont(cargarFuente("SegundoExamen/recursos/fuenteTitulo.ttf", 24f));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Determinar el subtítulo según el módulo
        String subtitulo;
        switch(moduloActual) {
            case 1:
                subtitulo = "EXPLORACIÓN DE ECOSISTEMAS";
                break;
            case 2:
                subtitulo = "OPTIMIZACIÓN DE RUTAS";
                break;
            case 3:
                subtitulo = "REDES ECOLÓGICAS";
                break;
            case 4:
                subtitulo = "EJERCICIO FINAL";
                break;
            default:
                subtitulo = "MÓDULO DESCONOCIDO";
        }
    
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(cargarFuente("SegundoExamen/recursos/fuenteTitulo.ttf", 18f));
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setBackground(new Color(217, 120, 82));
        lblSubtitulo.setOpaque(true);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        panelTitulos.add(Box.createVerticalGlue());
        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createRigidArea(new Dimension(0, 15)));
        panelTitulos.add(lblSubtitulo);
        panelTitulos.add(Box.createVerticalGlue());
    
        // Panel de imagen del gato
        JPanel panelGato = new JPanel(new BorderLayout());
        panelGato.setBackground(new Color(199, 203, 165));
        panelGato.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
    
        ImageIcon iconoGato = new ImageIcon(getClass().getResource("/recursos/gatoR.PNG"));
        if (iconoGato.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image imagenGato = iconoGato.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
            JLabel lblGato = new JLabel(new ImageIcon(imagenGato));
            lblGato.setHorizontalAlignment(SwingConstants.CENTER);
            panelGato.add(lblGato, BorderLayout.CENTER);
        }
    
        panel.add(panelTitulos);
        panel.add(panelGato);
        
        return panel;
    }

    //estilo :D
    private void estiloBotonPregunta(JButton boton) {
        boton.setBackground(new Color(87, 124, 88));
        boton.setForeground(Color.WHITE);
        boton.setFont(cargarFuente("SegundoExamen\\recursos\\fuenteTitulo.ttf", 16f)); // Fuente más grande
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25)); // Más padding
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(100, 50)); // Tamaño más grande
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
    
    //-----------------------------------------------------
    //       SECCION DEL MAIN
    //-----------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuardianesDelBosques());
    }
}