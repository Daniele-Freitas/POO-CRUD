package br.com.prefeitura.eventos.view;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("Dashboard de Gestão - Prefeitura Comunitária");
        setSize(900, 650);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JLabel lblHeader = new JLabel("Sistema Integrado de Eventos Locais", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblHeader, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Injeção dos painéis customizados de CRUD em cada aba
        tabbedPane.addTab("Usuários", new PainelUsuario());
        tabbedPane.addTab("Locais", new PainelLocal());
        tabbedPane.addTab("Eventos", new PainelEvento());
        tabbedPane.addTab("Inscrições", new PainelInscricao());

        add(tabbedPane, BorderLayout.CENTER);
    }
}