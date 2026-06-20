package br.com.prefeitura.eventos.view;

import br.com.prefeitura.eventos.dao.EventoDAO;
import br.com.prefeitura.eventos.dao.UsuarioDAO;
import br.com.prefeitura.eventos.exception.DaoException;
import br.com.prefeitura.eventos.exception.RegraNegocioException;
import br.com.prefeitura.eventos.model.Evento;
import br.com.prefeitura.eventos.model.Inscricao;
import br.com.prefeitura.eventos.model.Usuario;
import br.com.prefeitura.eventos.service.InscricaoService;

import javax.swing.*;
import java.awt.*;

public class TelaInscricao extends JFrame {

    private JComboBox<Evento> cmbEvento;
    private JComboBox<Usuario> cmbUsuario;
    private JTextField txtObservacao;

    public TelaInscricao() {
        setTitle("Inscrição em Evento");
        setSize(400, 200);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); // Fecha a aplicação inteira
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cmbEvento = new JComboBox<>();
        cmbUsuario = new JComboBox<>();
        carregarDadosComboBox();

        formPanel.add(new JLabel("Selecione o Evento:"));
        formPanel.add(cmbEvento);

        formPanel.add(new JLabel("Selecione o Participante:"));
        formPanel.add(cmbUsuario);

        formPanel.add(new JLabel("Observação:"));
        txtObservacao = new JTextField();
        formPanel.add(txtObservacao);

        add(formPanel, BorderLayout.CENTER);

        JButton btnInscrever = new JButton("Confirmar Inscrição");
        btnInscrever.addActionListener(e -> realizarInscricao());
        add(btnInscrever, BorderLayout.SOUTH);
    }

    private void carregarDadosComboBox() {
        try {
            EventoDAO eventoDAO = new EventoDAO();
            for (Evento evento : eventoDAO.listarTodos()) {
                cmbEvento.addItem(evento);
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            for (Usuario usuario : usuarioDAO.listarTodos()) {
                cmbUsuario.addItem(usuario);
            }
        } catch (DaoException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarInscricao() {
        try {
            Evento eventoSelecionado = (Evento) cmbEvento.getSelectedItem();
            Usuario usuarioSelecionado = (Usuario) cmbUsuario.getSelectedItem();

            if (eventoSelecionado == null || usuarioSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um evento e um usuário.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Inscricao inscricao = new Inscricao(
                eventoSelecionado.getId(),
                usuarioSelecionado.getId(),
                txtObservacao.getText()
            );

            InscricaoService service = new InscricaoService();
            service.realizarInscricao(inscricao);

            JOptionPane.showMessageDialog(this, "Inscrição confirmada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Inscrição Recusada", JOptionPane.WARNING_MESSAGE);
        } catch (DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}