package br.com.prefeitura.eventos.view;

import br.com.prefeitura.eventos.dao.LocalEventoDAO;
import br.com.prefeitura.eventos.exception.DaoException;
import br.com.prefeitura.eventos.model.LocalEvento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelLocal extends JPanel {
    
    private JTable tabela;
    private DefaultTableModel tableModel;
    
    private Long idSelecionado = null;
    
    private JTextField txtNome; 
    private JTextField txtEndereco; 
    private JTextField txtCapacidade; 
    private JTextField txtDescricao;
    
    private transient LocalEventoDAO localDAO = new LocalEventoDAO();

    public PainelLocal() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Tabela (Read)
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Endereço", "Capacidade"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> carregarSelecionado());
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // 2. Formulário Lateral de Edição
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Local"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.weightx = 1.0;

        txtNome = new JTextField(); 
        txtEndereco = new JTextField();
        txtCapacidade = new JTextField(); 
        txtDescricao = new JTextField();

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome:"), gbc); gbc.gridx = 1; formPanel.add(txtNome, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Endereço:"), gbc); gbc.gridx = 1; formPanel.add(txtEndereco, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Capacidade:"), gbc); gbc.gridx = 1; formPanel.add(txtCapacidade, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Descrição:"), gbc); gbc.gridx = 1; formPanel.add(txtDescricao, gbc);

        // Painel de Botões
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton btnSalvar = new JButton("Salvar"); 
        JButton btnExcluir = new JButton("Excluir"); 
        JButton btnLimpar = new JButton("Limpar");
        
        btnPanel.add(btnSalvar); btnPanel.add(btnExcluir); btnPanel.add(btnLimpar);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2; formPanel.add(btnPanel, gbc);

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limpar());

        add(formPanel, BorderLayout.EAST);
        atualizarTabela();
    }

    private void atualizarTabela() {
        try {
            tableModel.setRowCount(0);
            List<LocalEvento> lista = localDAO.listarTodos();
            for (LocalEvento l : lista) {
                tableModel.addRow(new Object[]{l.getId(), l.getNome(), l.getEndereco(), l.getCapacidade()});
            }
        } catch (DaoException ex) { 
            mostrarErro("Erro de conexão ao carregar locais: " + ex.getMessage()); 
        }
    }

    private void carregarSelecionado() {
        int row = tabela.getSelectedRow();
        if (row >= 0) {
            try {
                Long idTabela = (Long) tableModel.getValueAt(row, 0);
                LocalEvento l = localDAO.buscarPorId(idTabela);
                if (l != null) {
                    idSelecionado = l.getId(); // Guarda na memória
                    txtNome.setText(l.getNome());
                    txtEndereco.setText(l.getEndereco()); 
                    txtCapacidade.setText(l.getCapacidade().toString());
                    txtDescricao.setText(l.getDescricao());
                }
            } catch (DaoException ex) { 
                mostrarErro("Erro ao buscar detalhes do local: " + ex.getMessage()); 
            }
        }
    }

    // --- VALIDAÇÃO DE DADOS ---
    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            mostrarAviso("O campo 'Nome' é obrigatório.");
            txtNome.requestFocus(); // Coloca o cursor piscando no campo com erro
            return false;
        }
        if (txtEndereco.getText().trim().isEmpty()) {
            mostrarAviso("O campo 'Endereço' é obrigatório.");
            txtEndereco.requestFocus();
            return false;
        }
        if (txtCapacidade.getText().trim().isEmpty()) {
            mostrarAviso("O campo 'Capacidade' é obrigatório.");
            txtCapacidade.requestFocus();
            return false;
        }
        
        try {
            int cap = Integer.parseInt(txtCapacidade.getText().trim());
            if (cap <= 0) {
                mostrarAviso("A capacidade do local deve ser um número maior que zero.");
                txtCapacidade.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAviso("O campo 'Capacidade' deve conter apenas números inteiros válidos (ex: 50, 100).");
            txtCapacidade.requestFocus();
            return false;
        }
        
        return true; // Se passou por todos os IFs, os dados estão perfeitos
    }

    private void salvar() {
        // Se a validação falhar, interrompe o salvamento imediatamente
        if (!validarCampos()) {
            return;
        }

        try {
            LocalEvento l = new LocalEvento(
                txtNome.getText().trim(), 
                txtEndereco.getText().trim(), 
                Integer.parseInt(txtCapacidade.getText().trim()), 
                txtDescricao.getText().trim()
            );
            
            if (idSelecionado == null) {
                localDAO.inserir(l);
                JOptionPane.showMessageDialog(this, "Local cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                l.setId(idSelecionado);
                localDAO.atualizar(l);
                JOptionPane.showMessageDialog(this, "Local atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            
            limpar(); 
            atualizarTabela();
            
        } catch (DaoException ex) { 
            mostrarErro("Erro no Banco de Dados: " + ex.getMessage()); 
        } catch (Exception ex) { 
            mostrarErro("Ocorreu um erro inesperado: " + ex.getMessage()); 
        }
    }

    private void excluir() {
        if (idSelecionado != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Atenção: Tem certeza que deseja excluir este local?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    localDAO.deletar(idSelecionado);
                    JOptionPane.showMessageDialog(this, "Local excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limpar(); 
                    atualizarTabela();
                } catch (DaoException ex) { 
                    mostrarErro("Erro ao excluir: " + ex.getMessage()); 
                }
            }
        } else {
            mostrarAviso("Selecione um local na tabela antes de clicar em Excluir.");
        }
    }

    private void limpar() {
        idSelecionado = null; 
        txtNome.setText(""); 
        txtEndereco.setText(""); 
        txtCapacidade.setText(""); 
        txtDescricao.setText(""); 
        tabela.clearSelection();
    }

    // Métodos utilitários para padronizar os pop-ups
    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}