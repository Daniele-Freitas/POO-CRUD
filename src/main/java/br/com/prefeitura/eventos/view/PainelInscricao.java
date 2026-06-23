package br.com.prefeitura.eventos.view;

import br.com.prefeitura.eventos.dao.EventoDAO;
import br.com.prefeitura.eventos.dao.InscricaoDAO;
import br.com.prefeitura.eventos.dao.UsuarioDAO;
import br.com.prefeitura.eventos.exception.DaoException;
import br.com.prefeitura.eventos.exception.RegraNegocioException;
import br.com.prefeitura.eventos.model.Evento;
import br.com.prefeitura.eventos.model.Inscricao;
import br.com.prefeitura.eventos.model.Usuario;
import br.com.prefeitura.eventos.service.InscricaoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelInscricao extends JPanel {
    
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JComboBox<Evento> cmbEvento;
    private JComboBox<Usuario> cmbUsuario;
    private JTextField txtObs;
    
    private transient InscricaoDAO inscricaoDAO = new InscricaoDAO();
    private transient InscricaoService inscricaoService = new InscricaoService();
    private transient EventoDAO eventoDAO = new EventoDAO();
    private transient UsuarioDAO usuarioDAO = new UsuarioDAO();

    public PainelInscricao() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new Object[]{"Nº Inscrição", "Evento", "Participante", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } 
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("Nova Inscrição"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 5, 5, 5); gbc.weightx = 1.0;

        txtObs = new JTextField(); 
        cmbEvento = new JComboBox<>(); 
        cmbUsuario = new JComboBox<>();

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Evento:"), gbc); gbc.gridx = 1; formPanel.add(cmbEvento, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Participante:"), gbc); gbc.gridx = 1; formPanel.add(cmbUsuario, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Observação:"), gbc); gbc.gridx = 1; formPanel.add(txtObs, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton btnSalvar = new JButton("Inscrever"); 
        JButton btnExcluir = new JButton("Deletar");
        btnPanel.add(btnSalvar); btnPanel.add(btnExcluir);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; formPanel.add(btnPanel, gbc);

        btnSalvar.addActionListener(e -> inscrever());
        btnExcluir.addActionListener(e -> remover());

        add(formPanel, BorderLayout.EAST);
        
        recarrregarCombos();
        atualizarTabela();

        // Recarrega os combos e a tabela toda vez que o usuário clica nesta aba
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                recarrregarCombos();
                atualizarTabela();
            }
        });
    }

    public void recarrregarCombos() {
        try {
            cmbEvento.removeAllItems(); cmbUsuario.removeAllItems();
            for (Evento ev : eventoDAO.listarTodos()) cmbEvento.addItem(ev);
            for (Usuario u : usuarioDAO.listarTodos()) cmbUsuario.addItem(u);
        } catch (DaoException ignored) {}
    }

    private void atualizarTabela() {
        try {
            tableModel.setRowCount(0);
            List<Inscricao> lista = inscricaoDAO.listarTodos();
            
            for (Inscricao i : lista) {
                Evento ev = eventoDAO.buscarPorId(i.getEventoId());
                Usuario us = usuarioDAO.buscarPorId(i.getUsuarioId());
                
                String nomeEvento = (ev != null) ? ev.getTitulo() : "Evento Removido";
                String nomeUsuario = (us != null) ? us.getNome() : "Usuário Removido";

                tableModel.addRow(new Object[]{i.getId(), nomeEvento, nomeUsuario, i.getStatus()});
            }
        } catch (DaoException ex) { mostrarErro("Erro de Banco de Dados: " + ex.getMessage()); }
    }

    private boolean validarCampos() {
        if (cmbEvento.getSelectedItem() == null) {
            mostrarAviso("É necessário selecionar um evento para a inscrição."); return false;
        }
        if (cmbUsuario.getSelectedItem() == null) {
            mostrarAviso("É necessário selecionar um participante."); return false;
        }
        return true;
    }

    private void inscrever() {
        if (!validarCampos()) return;

        try {
            Evento ev = (Evento) cmbEvento.getSelectedItem();
            Usuario us = (Usuario) cmbUsuario.getSelectedItem();
            
            Inscricao ins = new Inscricao(ev.getId(), us.getId(), txtObs.getText().trim());
            inscricaoService.realizarInscricao(ins);
            JOptionPane.showMessageDialog(this, "Inscrição realizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            txtObs.setText(""); 
            atualizarTabela();
        } catch (RegraNegocioException ex) { mostrarAviso(ex.getMessage());
        } catch (DaoException ex) { mostrarErro("Erro interno: " + ex.getMessage()); }
    }

    private void remover() {
        int row = tabela.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja cancelar e excluir esta inscrição?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long idInscricao = (Long) tableModel.getValueAt(row, 0);
                    inscricaoDAO.deletar(idInscricao);
                    JOptionPane.showMessageDialog(this, "Inscrição deletada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    atualizarTabela();
                } catch (DaoException ex) { mostrarErro("Erro ao excluir: " + ex.getMessage()); }
            }
        } else { mostrarAviso("Selecione uma inscrição na tabela clicando nela para poder deletar."); }
    }

    private void mostrarErro(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Erro no Sistema", JOptionPane.ERROR_MESSAGE); }
    private void mostrarAviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
}