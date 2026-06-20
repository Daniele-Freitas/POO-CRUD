package br.com.prefeitura.eventos.view;

import br.com.prefeitura.eventos.dao.UsuarioDAO;
import br.com.prefeitura.eventos.exception.DaoException;
import br.com.prefeitura.eventos.model.Usuario;
import br.com.prefeitura.eventos.model.enums.TipoUsuario;

import javax.swing.*;
import java.awt.*;

public class TelaUsuario extends JFrame {

    private JTextField txtNome;
    private JTextField txtEmail;
    private JTextField txtSenha; 
    private JTextField txtTelefone;
    private JComboBox<TipoUsuario> cmbTipo;

    public TelaUsuario() {
        setTitle("Cadastro de Usuário");
        setSize(350, 300);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);

        formPanel.add(new JLabel("E-mail:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Senha (Hash):"));
        txtSenha = new JTextField();
        formPanel.add(txtSenha);

        formPanel.add(new JLabel("Tipo:"));
        cmbTipo = new JComboBox<>(TipoUsuario.values());
        formPanel.add(cmbTipo);

        formPanel.add(new JLabel("Telefone:"));
        txtTelefone = new JTextField();
        formPanel.add(txtTelefone);

        add(formPanel, BorderLayout.CENTER);

        JButton btnSalvar = new JButton("Salvar Usuário");
        btnSalvar.addActionListener(e -> salvarUsuario());
        add(btnSalvar, BorderLayout.SOUTH);
    }

    private void salvarUsuario() {
        try {
            Usuario usuario = new Usuario(
                txtNome.getText(),
                txtEmail.getText(),
                txtSenha.getText(),
                (TipoUsuario) cmbTipo.getSelectedItem(),
                txtTelefone.getText()
            );

            UsuarioDAO dao = new UsuarioDAO();
            dao.inserir(usuario);

            JOptionPane.showMessageDialog(this, "Usuário salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Fecha a tela após o sucesso
        } catch (DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Adicionamos o ex.getMessage() e o printStackTrace para debug
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}