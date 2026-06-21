package br.com.prefeitura.eventos.view;

import br.com.prefeitura.eventos.dao.UsuarioDAO;
import br.com.prefeitura.eventos.exception.DaoException;
import br.com.prefeitura.eventos.model.Usuario;
import br.com.prefeitura.eventos.model.enums.TipoUsuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelUsuario extends JPanel {

    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private Long idSelecionado = null; 
    
    private JTextField txtNome; 
    private JTextField txtEmail; 
    private JTextField txtSenha; 
    private JTextField txtTelefone;
    private JComboBox<TipoUsuario> cmbTipo;
    
    private transient UsuarioDAO usuarioDAO = new UsuarioDAO();

    public PainelUsuario() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "E-mail", "Tipo", "Telefone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> carregarRegistroSelecionado());
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 5, 5, 5); gbc.weightx = 1.0;

        txtNome = new JTextField(); 
        txtEmail = new JTextField();
        txtSenha = new JTextField(); 
        txtTelefone = new JTextField();
        cmbTipo = new JComboBox<>(TipoUsuario.values());

        adicionarCampo(formPanel, "Nome:", txtNome, gbc, 0);
        adicionarCampo(formPanel, "E-mail:", txtEmail, gbc, 1);
        adicionarCampo(formPanel, "Senha (Hash):", txtSenha, gbc, 2);
        adicionarCampo(formPanel, "Tipo:", cmbTipo, gbc, 3);
        adicionarCampo(formPanel, "Telefone:", txtTelefone, gbc, 4);

        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton btnSalvar = new JButton("Salvar"); 
        JButton btnExcluir = new JButton("Excluir"); 
        JButton btnLimpar = new JButton("Limpar");
        
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparCampos());

        btnPanel.add(btnSalvar); btnPanel.add(btnExcluir); btnPanel.add(btnLimpar);
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2; formPanel.add(btnPanel, gbc);
        add(formPanel, BorderLayout.EAST);

        atualizarTabela();
    }

    private void adicionarCampo(JPanel p, String label, JComponent comp, GridBagConstraints gbc, int y) {
        gbc.gridy = y; gbc.gridx = 0; gbc.gridwidth = 1; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; p.add(comp, gbc);
    }

    private void atualizarTabela() {
        try {
            tableModel.setRowCount(0);
            List<Usuario> lista = usuarioDAO.listarTodos();
            for (Usuario u : lista) tableModel.addRow(new Object[]{u.getId(), u.getNome(), u.getEmail(), u.getTipo(), u.getTelefone()});
        } catch (DaoException ex) { mostrarErro("Erro ao carregar usuários: " + ex.getMessage()); }
    }

    private void carregarRegistroSelecionado() {
        int row = tabela.getSelectedRow();
        if (row >= 0) {
            try {
                Long idTabela = (Long) tableModel.getValueAt(row, 0);
                Usuario u = usuarioDAO.buscarPorId(idTabela);
                if (u != null) {
                    idSelecionado = u.getId(); 
                    txtNome.setText(u.getNome());
                    txtEmail.setText(u.getEmail());
                    txtSenha.setText(u.getSenhaHash());
                    txtSenha.setEditable(false); 
                    cmbTipo.setSelectedItem(u.getTipo());
                    txtTelefone.setText(u.getTelefone());
                }
            } catch (DaoException ex) { mostrarErro("Erro ao buscar detalhes: " + ex.getMessage()); }
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            mostrarAviso("O campo 'Nome' é obrigatório.");
            txtNome.requestFocus(); return false;
        }
        if (txtEmail.getText().trim().isEmpty() || !txtEmail.getText().contains("@")) {
            mostrarAviso("Forneça um e-mail válido contendo '@'.");
            txtEmail.requestFocus(); return false;
        }
        if (idSelecionado == null && txtSenha.getText().trim().isEmpty()) {
            mostrarAviso("A senha é obrigatória para o cadastro de um novo usuário.");
            txtSenha.requestFocus(); return false;
        }
        return true;
    }

    private void salvar() {
        if (!validarCampos()) return;

        try {
            Usuario u = new Usuario(txtNome.getText().trim(), txtEmail.getText().trim(), txtSenha.getText().trim(), (TipoUsuario) cmbTipo.getSelectedItem(), txtTelefone.getText().trim());
            
            if (idSelecionado == null) {
                usuarioDAO.inserir(u);
                JOptionPane.showMessageDialog(this, "Usuário criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                u.setId(idSelecionado);
                usuarioDAO.atualizar(u);
                JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            limparCampos(); atualizarTabela();
        } catch (DaoException ex) { mostrarErro("Erro de Banco de Dados: " + ex.getMessage()); }
    }

    private void excluir() {
        if (idSelecionado != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este usuário?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    usuarioDAO.deletar(idSelecionado);
                    JOptionPane.showMessageDialog(this, "Usuário excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparCampos(); atualizarTabela();
                } catch (DaoException ex) { mostrarErro("Erro ao excluir: " + ex.getMessage()); }
            }
        } else { mostrarAviso("Selecione um usuário na tabela primeiro."); }
    }

    private void limparCampos() {
        idSelecionado = null; 
        txtNome.setText(""); txtEmail.setText(""); txtSenha.setText(""); txtTelefone.setText("");
        txtSenha.setEditable(true); cmbTipo.setSelectedIndex(0); tabela.clearSelection();
    }

    private void mostrarErro(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Erro no Sistema", JOptionPane.ERROR_MESSAGE); }
    private void mostrarAviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
}