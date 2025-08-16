/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.gui;


import biblioteca.dominio.*;
import biblioteca.excepciones.*;
import biblioteca.servicios.BibliotecaService;
import biblioteca.persistencia.BibliotecaDAOImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

public class BibliotecaMainFrame extends JFrame {
    private BibliotecaService bibliotecaService;
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;

    public BibliotecaMainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usar look and feel por defecto
        }
        
        try {
            BibliotecaDAOImpl dao = new BibliotecaDAOImpl();
            bibliotecaService = new BibliotecaService(dao);
            inicializarComponentes();
            cargarDatos();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(null, 
                "Error crítico al inicializar la base de datos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void inicializarComponentes() {
        setTitle("Biblioteca Personal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Gestión de Biblioteca"));

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBusqueda = new JTextField(20);
        txtBusqueda.addActionListener(e -> buscarLibros());
        panelBusqueda.add(txtBusqueda);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarLibros());
        panelBusqueda.add(btnBuscar);

        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.addActionListener(e -> cargarDatos());
        panelBusqueda.add(btnMostrarTodos);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnAgregar = new JButton("Agregar Libro");
        btnAgregar.addActionListener(e -> mostrarDialogoAgregarLibro());
        panelBotones.add(btnAgregar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarLibroSeleccionado());
        panelBotones.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarLibroSeleccionado());
        panelBotones.add(btnEliminar);

        JButton btnPrestar = new JButton("Prestar");
        btnPrestar.addActionListener(e -> prestarLibroSeleccionado());
        panelBotones.add(btnPrestar);

        JButton btnDevolver = new JButton("Devoluciones");
        btnDevolver.addActionListener(e -> mostrarDialogoDevoluciones());
        panelBotones.add(btnDevolver);

        panelSuperior.add(panelBusqueda, BorderLayout.WEST);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createTitledBorder("Lista de Libros"));

        // Modelo y tabla de libros
        String[] columnas = {"ISBN", "Título", "Autor", "Género", "Estado", "Ubicación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaLibros = new JTable(modeloTabla);
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Configurar anchos de columnas
        tablaLibros.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaLibros.getColumnModel().getColumn(1).setPreferredWidth(250);
        tablaLibros.getColumnModel().getColumn(2).setPreferredWidth(200);
        tablaLibros.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaLibros.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaLibros.getColumnModel().getColumn(5).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(tablaLibros);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.setBorder(BorderFactory.createEtchedBorder());
        panelInferior.add(new JLabel("Seleccione un libro para ver más opciones"));
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Libro> libros = bibliotecaService.obtenerTodosLosLibros();
        
        for (Libro libro : libros) {
            Object[] fila = {
                libro.getIsbn(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getGenero() != null ? libro.getGenero() : "",
                libro.getEstado().getDescripcion(),
                libro.getUbicacion() != null ? libro.getUbicacion() : ""
            };
            modeloTabla.addRow(fila);
        }
    }

    private void buscarLibros() {
        String termino = txtBusqueda.getText().trim();
        if (termino.isEmpty()) {
            cargarDatos();
            return;
        }

        modeloTabla.setRowCount(0);
        List<Libro> librosPorTitulo = bibliotecaService.buscarLibrosPorTitulo(termino);
        List<Libro> librosPorAutor = bibliotecaService.buscarLibrosPorAutor(termino);

        // Combinar resultados evitando duplicados
        for (Libro libro : librosPorTitulo) {
            agregarFilaLibro(libro);
        }
        for (Libro libro : librosPorAutor) {
            if (!librosPorTitulo.contains(libro)) {
                agregarFilaLibro(libro);
            }
        }

        if (modeloTabla.getRowCount() == 0) {
            mostrarInformacion("No se encontraron libros que coincidan con la búsqueda.");
        }
    }

    private void agregarFilaLibro(Libro libro) {
        Object[] fila = {
            libro.getIsbn(),
            libro.getTitulo(),
            libro.getAutor(),
            libro.getGenero() != null ? libro.getGenero() : "",
            libro.getEstado().getDescripcion(),
            libro.getUbicacion() != null ? libro.getUbicacion() : ""
        };
        modeloTabla.addRow(fila);
    }

    private void mostrarDialogoAgregarLibro() {
        LibroDialog dialog = new LibroDialog(this, "Agregar Nuevo Libro", null);
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            Libro nuevoLibro = dialog.getLibro();
            try {
                bibliotecaService.agregarLibro(nuevoLibro);
                cargarDatos();
                mostrarInformacion("Libro agregado exitosamente.");
            } catch (BibliotecaException e) {
                mostrarError("Error al agregar libro: " + e.getMessage());
            }
        }
    }

    private void editarLibroSeleccionado() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarInformacion("Seleccione un libro para editar.");
            return;
        }

        String isbn = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        try {
            Libro libro = bibliotecaService.buscarLibroPorIsbn(isbn);
            LibroDialog dialog = new LibroDialog(this, "Editar Libro", libro);
            dialog.setVisible(true);

            if (dialog.isConfirmado()) {
                Libro libroEditado = dialog.getLibro();
                bibliotecaService.actualizarLibro(libroEditado);
                cargarDatos();
                mostrarInformacion("Libro actualizado exitosamente.");
            }
        } catch (BibliotecaException e) {
            mostrarError("Error al editar libro: " + e.getMessage());
        }
    }

    private void eliminarLibroSeleccionado() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarInformacion("Seleccione un libro para eliminar.");
            return;
        }

        String isbn = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String titulo = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el libro '" + titulo + "'?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                bibliotecaService.eliminarLibro(isbn);
                cargarDatos();
                mostrarInformacion("Libro eliminado exitosamente.");
            } catch (BibliotecaException e) {
                mostrarError("Error al eliminar libro: " + e.getMessage());
            }
        }
    }

    private void prestarLibroSeleccionado() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarInformacion("Seleccione un libro para prestar.");
            return;
        }

        String isbn = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String titulo = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        String estado = (String) modeloTabla.getValueAt(filaSeleccionada, 4);

        if (!estado.equals("Disponible")) {
            mostrarInformacion("El libro no está disponible para préstamo.");
            return;
        }

        PrestamoDialog dialog = new PrestamoDialog(this, titulo);
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            try {
                String prestatario = dialog.getPrestatario();
                LocalDate fechaDevolucion = dialog.getFechaDevolucion();
                
                bibliotecaService.prestarLibro(isbn, prestatario, fechaDevolucion);
                cargarDatos();
                mostrarInformacion("Préstamo registrado exitosamente.");
            } catch (BibliotecaException e) {
                mostrarError("Error al registrar préstamo: " + e.getMessage());
            }
        }
    }

    private void mostrarDialogoDevoluciones() {
        DevolucionDialog dialog = new DevolucionDialog(this, bibliotecaService);
        dialog.setVisible(true);
        
        if (dialog.isAlgoDevuelto()) {
            cargarDatos();
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInformacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BibliotecaMainFrame().setVisible(true);
        });
    }
}