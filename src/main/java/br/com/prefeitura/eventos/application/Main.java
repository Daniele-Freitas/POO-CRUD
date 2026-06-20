package br.com.prefeitura.eventos.application;

import br.com.prefeitura.eventos.view.TelaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Inicializa a interface gráfica de forma segura no padrão do Swing
        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }
}