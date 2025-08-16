/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.gui;

/**
 *
 * @author ingri
 */

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PrestamoDialog extends JDialog {
    private JTextField txtPrestatario;
    private JTextField txtFechaDevolucion;
    private boolean confirmado = false;

    public PrestamoDialog(Frame parent, String tituloLibro) {
        super(parent, "Prestar Libro", true);
        inicializarComponentes(tituloLibro);
    }

    private void inicializarComponentes(String tituloLibro) {
        setLayout(new BorderLayout());
        setSize(400, 250);
        setLocationRelativeTo(getParent());

        // Panel de información
        JPanel panelInfo = new JPanel();
        panelInfo.add(new JLabel("Prestando: " + tituloLibro));
        add(panelInfo, BorderLayout.NORTH);

        // Panel de campos
        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Prestatario
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(new JLabel("Prestatario:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtPrestatario = new JTextField(20);
        panelCampos.add(txtPrestatario, gbc);

        // Fecha de devolución
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelCampos.add(new JLabel("Fecha devolución (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtFechaDevolucion = new JTextField(20);
        txtFechaDevolucion.setText(LocalDate.now().plusDays(15).toString());
        panelCampos.add(txtFechaDevolucion, gbc);

        add(panelCampos, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnPrestar = new JButton("Prestar");
        btnPrestar.addActionListener(e -> confirmarPrestamo());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnPrestar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void confirmarPrestamo() {
        if (txtPrestatario.getText().trim().isEmpty()) {
            mostrarError("El nombre del prestatario es obligatorio");
            return;
        }

        try {
            LocalDate.parse(txtFechaDevolucion.getText().trim());
            confirmado = true;
            dispose();
        } catch (DateTimeParseException e) {
            mostrarError("Formato de fecha inválido. Use YYYY-MM-DD");
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getPrestatario() {
        return txtPrestatario.getText().trim();
    }

    public LocalDate getFechaDevolucion() {
        return LocalDate.parse(txtFechaDevolucion.getText().trim());
    }
}