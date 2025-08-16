/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.gui;

/**
 *
 * @author ingri
 */

import biblioteca.dominio.Prestamo;
import biblioteca.excepciones.BibliotecaException;
import biblioteca.servicios.BibliotecaService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DevolucionDialog extends JDialog {
    private BibliotecaService bibliotecaService;
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;
    private boolean algoDevuelto = false;

    public DevolucionDialog(Frame parent, BibliotecaService service) {
        super(parent, "Gestión de Devoluciones", true);
        this.bibliotecaService = service;
        inicializarComponentes();
        cargarPrestamos();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setSize(800, 500);
        setLocationRelativeTo(getParent());

        // Modelo y tabla de préstamos
        String[] columnas = {"ID", "Libro", "Prestatario", "Fecha Préstamo", "Fecha Devolución", "Vencido"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPrestamos = new JTable(modeloTabla);
        tablaPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tablaPrestamos);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        
        JButton btnDevolver = new JButton("Devolver Seleccionado");
        btnDevolver.addActionListener(e -> devolverSeleccionado());
        panelBotones.add(btnDevolver);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);

        // Panel de información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.add(new JLabel("Seleccione un préstamo activo para devolver"));
        add(panelInfo, BorderLayout.NORTH);
    }

    private void cargarPrestamos() {
        modeloTabla.setRowCount(0);
        List<Prestamo> prestamosActivos = bibliotecaService.obtenerPrestamosActivos();
        
        for (Prestamo prestamo : prestamosActivos) {
            Object[] fila = {
                prestamo.getId(),
                prestamo.getLibro().getTitulo(),
                prestamo.getPrestatario(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionPrevista(),
                prestamo.estaVencido() ? "SÍ" : "NO"
            };
            modeloTabla.addRow(fila);
        }
    }

    private void devolverSeleccionado() {
        int filaSeleccionada = tablaPrestamos.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarInformacion("Seleccione un préstamo para devolver.");
            return;
        }

        Long prestamoId = (Long) modeloTabla.getValueAt(filaSeleccionada, 0);
        String tituloLibro = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Confirma la devolución del libro '" + tituloLibro + "'?",
            "Confirmar Devolución",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                bibliotecaService.devolverLibro(prestamoId, LocalDate.now());
                cargarPrestamos();
                algoDevuelto = true;
                mostrarInformacion("Libro devuelto exitosamente.");
            } catch (BibliotecaException e) {
                mostrarError("Error al devolver libro: " + e.getMessage());
            }
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInformacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean isAlgoDevuelto() {
        return algoDevuelto;
    }
}
