/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.gui;

/**
 *
 * @author ingri
 */

import biblioteca.dominio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LibroDialog extends JDialog {
    private JTextField txtIsbn, txtTitulo, txtAutor, txtGenero, txtFechaPublicacion, txtUbicacion;
    private JComboBox<EstadoLibro> cmbEstado;
    private JTextArea txtNotas;
    private boolean confirmado = false;
    private Libro libro;

    public LibroDialog(Frame parent, String titulo, Libro libroEditar) {
        super(parent, titulo, true);
        this.libro = libroEditar;
        inicializarComponentes();
        
        if (libroEditar != null) {
            cargarDatosLibro(libroEditar);
            txtIsbn.setEditable(false);
        }
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setSize(500, 600);
        setLocationRelativeTo(getParent());

        // Panel principal
        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campos del formulario
        agregarCampo(panelCampos, gbc, 0, "ISBN *:", txtIsbn = new JTextField(20));
        agregarCampo(panelCampos, gbc, 1, "Título *:", txtTitulo = new JTextField(20));
        agregarCampo(panelCampos, gbc, 2, "Autor *:", txtAutor = new JTextField(20));
        agregarCampo(panelCampos, gbc, 3, "Género:", txtGenero = new JTextField(20));
        agregarCampo(panelCampos, gbc, 4, "Fecha Pub. (YYYY-MM-DD):", txtFechaPublicacion = new JTextField(20));
        
        // Combo box para estado
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelCampos.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbEstado = new JComboBox<>(EstadoLibro.values());
        panelCampos.add(cmbEstado, gbc);
        
        agregarCampo(panelCampos, gbc, 6, "Ubicación:", txtUbicacion = new JTextField(20));
        
        // Área de notas
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.NORTHWEST;
        panelCampos.add(new JLabel("Notas:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtNotas = new JTextArea(5, 20);
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        panelCampos.add(new JScrollPane(txtNotas), gbc);

        add(panelCampos, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(this::guardarLibro);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        // Información de campos obligatorios
        JLabel lblInfo = new JLabel("* Campos obligatorios");
        lblInfo.setFont(lblInfo.getFont().deriveFont(Font.ITALIC));
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.add(lblInfo);
        add(panelInfo, BorderLayout.NORTH);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent componente) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(componente, gbc);
    }

    private void cargarDatosLibro(Libro libro) {
        txtIsbn.setText(libro.getIsbn());
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtGenero.setText(libro.getGenero() != null ? libro.getGenero() : "");
        
        if (libro.getFechaPublicacion() != null) {
            txtFechaPublicacion.setText(libro.getFechaPublicacion().toString());
        }
        
        cmbEstado.setSelectedItem(libro.getEstado());
        txtUbicacion.setText(libro.getUbicacion() != null ? libro.getUbicacion() : "");
        txtNotas.setText(libro.getNotas() != null ? libro.getNotas() : "");
    }

    private void guardarLibro(ActionEvent e) {
        try {
            // Validar campos obligatorios
            if (txtIsbn.getText().trim().isEmpty()) {
                mostrarError("El ISBN es obligatorio");
                return;
            }
            if (txtTitulo.getText().trim().isEmpty()) {
                mostrarError("El título es obligatorio");
                return;
            }
            if (txtAutor.getText().trim().isEmpty()) {
                mostrarError("El autor es obligatorio");
                return;
            }

            // Crear o actualizar libro
            if (libro == null) {
                libro = new Libro(
                    txtIsbn.getText().trim(), 
                    txtTitulo.getText().trim(), 
                    txtAutor.getText().trim()
                );
            } else {
                libro.setTitulo(txtTitulo.getText().trim());
                libro.setAutor(txtAutor.getText().trim());
            }

            // Establecer campos opcionales
            libro.setGenero(txtGenero.getText().trim().isEmpty() ? null : txtGenero.getText().trim());
            libro.setUbicacion(txtUbicacion.getText().trim().isEmpty() ? null : txtUbicacion.getText().trim());
            libro.setNotas(txtNotas.getText().trim().isEmpty() ? null : txtNotas.getText().trim());
            libro.setEstado((EstadoLibro) cmbEstado.getSelectedItem());

            // Parsear fecha si se proporciona
            String fechaTexto = txtFechaPublicacion.getText().trim();
            if (!fechaTexto.isEmpty()) {
                try {
                    LocalDate fecha = LocalDate.parse(fechaTexto);
                    libro.setFechaPublicacion(fecha);
                } catch (DateTimeParseException ex) {
                    mostrarError("Formato de fecha inválido. Use YYYY-MM-DD");
                    return;
                }
            }

            confirmado = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Libro getLibro() {
        return libro;
    }
}