package br.com.prefeitura.eventos.view;

import br.com.prefeitura.eventos.dao.EventoDAO;
import br.com.prefeitura.eventos.dao.LocalEventoDAO;
import br.com.prefeitura.eventos.dao.UsuarioDAO;
import br.com.prefeitura.eventos.exception.DaoException;
import br.com.prefeitura.eventos.exception.RegraNegocioException;
import br.com.prefeitura.eventos.model.Evento;
import br.com.prefeitura.eventos.model.LocalEvento;
import br.com.prefeitura.eventos.model.Usuario;
import br.com.prefeitura.eventos.model.enums.TipoUsuario;
import br.com.prefeitura.eventos.service.EventoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PainelEvento extends JPanel {
    
    private JTable tabela;
    private DefaultTableModel tableModel;
    private Long idSelecionado = null; 
    
    private JTextField txtTitulo, txtDescricao, txtInicio, txtFim, txtCapacidade, txtCategoria;
    private JComboBox<LocalEvento> cmbLocal;
    private JComboBox<Usuario> cmbOrganizador;
    
    private transient EventoDAO eventoDAO = new EventoDAO();
    private transient EventoService eventoService = new EventoService();
    private transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PainelEvento() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Título", "Início", "Capacidade"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> carregarSelecionado());
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(350, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Evento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(4, 4, 4, 4); gbc.weightx = 1.0;

        txtTitulo = new JTextField(); txtDescricao = new JTextField();
        txtInicio = new JTextField(); txtFim = new JTextField();
        txtCapacidade = new JTextField(); txtCategoria = new JTextField();
        cmbLocal = new JComboBox<>(); cmbOrganizador = new JComboBox<>();

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Título:"), gbc); gbc.gridx = 1; formPanel.add(txtTitulo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Descrição:"), gbc); gbc.gridx = 1; formPanel.add(txtDescricao, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Início (dd/mm/aaaa hh:mm):"), gbc); gbc.gridx = 1; formPanel.add(txtInicio, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Fim (dd/mm/aaaa hh:mm):"), gbc); gbc.gridx = 1; formPanel.add(txtFim, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Capacidade:"), gbc); gbc.gridx = 1; formPanel.add(txtCapacidade, gbc);
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Categoria:"), gbc); gbc.gridx = 1; formPanel.add(txtCategoria, gbc);
        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Local:"), gbc); gbc.gridx = 1; formPanel.add(cmbLocal, gbc);
        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(new JLabel("Organizador:"), gbc); gbc.gridx = 1; formPanel.add(cmbOrganizador, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir"); 
        JButton btnLimpar = new JButton("Limpar");
        btnPanel.add(btnSalvar); btnPanel.add(btnExcluir); btnPanel.add(btnLimpar);
        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 2; formPanel.add(btnPanel, gbc);

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limpar());

        add(formPanel, BorderLayout.EAST);
        recarrregarCombos();
        atualizarTabela();
    }

    public void recarrregarCombos() {
        try {
            cmbLocal.removeAllItems(); cmbOrganizador.removeAllItems();
            for (LocalEvento l : new LocalEventoDAO().listarTodos()) cmbLocal.addItem(l);
            for (Usuario u : new UsuarioDAO().listarTodos()) {
                if (u.getTipo() == TipoUsuario.ORGANIZADOR) cmbOrganizador.addItem(u);
            }
        } catch (DaoException ignored) {}
    }

    private void atualizarTabela() {
        try {
            tableModel.setRowCount(0);
            List<Evento> lista = eventoDAO.listarTodos();
            for (Evento e : lista) tableModel.addRow(new Object[]{e.getId(), e.getTitulo(), e.getDataInicio().format(formatter), e.getCapacidade()});
        } catch (DaoException ex) { mostrarErro("Erro ao carregar eventos: " + ex.getMessage()); }
    }

    private void carregarSelecionado() {
        int row = tabela.getSelectedRow();
        if (row >= 0) {
            try {
                Evento ev = eventoDAO.buscarPorId((Long) tableModel.getValueAt(row, 0));
                if (ev != null) {
                    idSelecionado = ev.getId();
                    txtTitulo.setText(ev.getTitulo());
                    txtDescricao.setText(ev.getDescricao()); 
                    txtInicio.setText(ev.getDataInicio().format(formatter));
                    txtFim.setText(ev.getDataFim().format(formatter)); 
                    txtCapacidade.setText(ev.getCapacidade().toString());
                    txtCategoria.setText(ev.getCategoria());
                    
                    for (int i=0; i<cmbLocal.getItemCount(); i++) {
                        if (cmbLocal.getItemAt(i).getId().equals(ev.getLocalId())) { cmbLocal.setSelectedIndex(i); break; }
                    }
                    for (int i=0; i<cmbOrganizador.getItemCount(); i++) {
                        if (cmbOrganizador.getItemAt(i).getId().equals(ev.getOrganizadorId())) { cmbOrganizador.setSelectedIndex(i); break; }
                    }
                }
            } catch (DaoException ex) { mostrarErro("Erro ao carregar detalhes: " + ex.getMessage()); }
        }
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty()) { mostrarAviso("O 'Título' é obrigatório."); txtTitulo.requestFocus(); return false; }
        
        try {
            LocalDateTime.parse(txtInicio.getText().trim(), formatter);
        } catch (DateTimeParseException e) {
            mostrarAviso("Formato de Data Inicial inválido. Use dd/MM/aaaa HH:mm (Ex: 25/10/2026 14:00).");
            txtInicio.requestFocus(); return false;
        }

        try {
            LocalDateTime.parse(txtFim.getText().trim(), formatter);
        } catch (DateTimeParseException e) {
            mostrarAviso("Formato de Data Final inválido. Use dd/MM/aaaa HH:mm (Ex: 25/10/2026 18:00).");
            txtFim.requestFocus(); return false;
        }

        try {
            int cap = Integer.parseInt(txtCapacidade.getText().trim());
            if (cap <= 0) { mostrarAviso("A capacidade deve ser maior que zero."); txtCapacidade.requestFocus(); return false; }
        } catch (NumberFormatException e) {
            mostrarAviso("A capacidade deve ser um número inteiro.");
            txtCapacidade.requestFocus(); return false;
        }

        if (cmbLocal.getSelectedItem() == null) { mostrarAviso("Selecione um local válido."); return false; }
        if (cmbOrganizador.getSelectedItem() == null) { mostrarAviso("Selecione um organizador válido."); return false; }

        return true;
    }

    private void salvar() {
        if (!validarCampos()) return;

        try {
            LocalEvento loc = (LocalEvento) cmbLocal.getSelectedItem();
            Usuario org = (Usuario) cmbOrganizador.getSelectedItem();
            Evento ev = new Evento(txtTitulo.getText().trim(), txtDescricao.getText().trim(), LocalDateTime.parse(txtInicio.getText().trim(), formatter), LocalDateTime.parse(txtFim.getText().trim(), formatter), loc.getId(), Integer.parseInt(txtCapacidade.getText().trim()), txtCategoria.getText().trim(), org.getId());
            
            if (idSelecionado == null) {
                eventoService.cadastrarEvento(ev);
                JOptionPane.showMessageDialog(this, "Evento criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                ev.setId(idSelecionado);
                eventoDAO.atualizar(ev);
                JOptionPane.showMessageDialog(this, "Evento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            limpar(); atualizarTabela();
        } catch (RegraNegocioException ex) { mostrarAviso("Validação de Negócio: " + ex.getMessage());
        } catch (DaoException ex) { mostrarErro("Erro de Banco de Dados: " + ex.getMessage()); }
    }

    private void excluir() {
        if (idSelecionado != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este evento?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    eventoDAO.deletar(idSelecionado);
                    JOptionPane.showMessageDialog(this, "Evento excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limpar(); atualizarTabela();
                } catch (DaoException ex) { mostrarErro("Erro ao excluir: " + ex.getMessage()); }
            }
        } else { mostrarAviso("Selecione um evento na tabela primeiro."); }
    }

    private void limpar() {
        idSelecionado = null; 
        txtTitulo.setText(""); txtDescricao.setText(""); txtInicio.setText(""); txtFim.setText(""); txtCapacidade.setText(""); txtCategoria.setText(""); 
        tabela.clearSelection();
    }

    private void mostrarErro(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Erro no Sistema", JOptionPane.ERROR_MESSAGE); }
    private void mostrarAviso(String mensagem) { JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE); }
}