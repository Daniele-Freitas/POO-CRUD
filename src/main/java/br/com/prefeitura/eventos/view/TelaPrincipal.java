package br.com.prefeitura.eventos.view;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("Sistema de Eventos - Prefeitura Comunitária");
        setSize(400, 300);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); // Fecha a aplicação inteira
        setLocationRelativeTo(null); // Centraliza na tela
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Gestão de Eventos Comunitários", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        JPanel panelBotoes = new JPanel(new GridLayout(3, 1, 10, 10));
        panelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnUsuarios = new JButton("Gerenciar Usuários");
        JButton btnEventos = new JButton("Gerenciar Eventos");
        JButton btnInscricoes = new JButton("Realizar Inscrições");

        // Ações dos botões para abrir as telas secundárias
        btnUsuarios.addActionListener(e -> new TelaUsuario().setVisible(true));
        btnEventos.addActionListener(e -> new TelaEvento().setVisible(true));
        btnInscricoes.addActionListener(e -> new TelaInscricao().setVisible(true));

        panelBotoes.add(btnUsuarios);
        panelBotoes.add(btnEventos);
        panelBotoes.add(btnInscricoes);

        add(panelBotoes, BorderLayout.CENTER);
    }
}