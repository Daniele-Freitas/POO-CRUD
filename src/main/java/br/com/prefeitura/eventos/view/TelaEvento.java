package br.com.prefeitura.eventos.view;

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
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TelaEvento extends JFrame {

    private JTextField txtTitulo;
    private JTextField txtDescricao;
    private JTextField txtInicio;
    private JTextField txtFim;
    private JTextField txtCapacidade;
    private JTextField txtCategoria;

    private JComboBox<LocalEvento> cmbLocal;
    private JComboBox<Usuario> cmbOrganizador;

    public TelaEvento() {
        setTitle("Cadastro de Evento");
        setSize(400, 400);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Título:"));
        txtTitulo = new JTextField();
        formPanel.add(txtTitulo);

        formPanel.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        formPanel.add(txtDescricao);

        formPanel.add(new JLabel("Início (DD/MM/AAAA HH:MM):"));
        txtInicio = new JTextField();
        formPanel.add(txtInicio);

        formPanel.add(new JLabel("Fim (DD/MM/AAAA HH:MM):"));
        txtFim = new JTextField();
        formPanel.add(txtFim);

        // Inicializando as caixas de seleção
        cmbLocal = new JComboBox<>();
        cmbOrganizador = new JComboBox<>();
        carregarDadosComboBox();

        formPanel.add(new JLabel("Local:"));
        formPanel.add(cmbLocal);

        formPanel.add(new JLabel("Capacidade:"));
        txtCapacidade = new JTextField();
        formPanel.add(txtCapacidade);

        formPanel.add(new JLabel("Categoria:"));
        txtCategoria = new JTextField();
        formPanel.add(txtCategoria);

        formPanel.add(new JLabel("Organizador:"));
        formPanel.add(cmbOrganizador);

        add(formPanel, BorderLayout.CENTER);

        JButton btnSalvar = new JButton("Cadastrar Evento");
        btnSalvar.addActionListener(e -> salvarEvento());
        add(btnSalvar, BorderLayout.SOUTH);
    }

    private void carregarDadosComboBox() {
        try {
            LocalEventoDAO localDAO = new LocalEventoDAO();
            for (LocalEvento local : localDAO.listarTodos()) {
                cmbLocal.addItem(local);
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            for (Usuario usuario : usuarioDAO.listarTodos()) {
                // Filtro extra de UX: Mostrar apenas usuários que são organizadores
                if (usuario.getTipo() == TipoUsuario.ORGANIZADOR) {
                    cmbOrganizador.addItem(usuario);
                }
            }
        } catch (DaoException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar locais ou organizadores: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarEvento() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            // Resgatando os objetos selecionados no combo box
            LocalEvento localSelecionado = (LocalEvento) cmbLocal.getSelectedItem();
            Usuario organizadorSelecionado = (Usuario) cmbOrganizador.getSelectedItem();

            if (localSelecionado == null || organizadorSelecionado == null) {
                JOptionPane.showMessageDialog(this, "É necessário selecionar um local e um organizador.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Evento evento = new Evento(
                txtTitulo.getText(),
                txtDescricao.getText(),
                LocalDateTime.parse(txtInicio.getText(), formatter),
                LocalDateTime.parse(txtFim.getText(), formatter),
                localSelecionado.getId(), // Pega o ID direto do objeto
                Integer.parseInt(txtCapacidade.getText()),
                txtCategoria.getText(),
                organizadorSelecionado.getId() // Pega o ID direto do objeto
            );

            EventoService service = new EventoService();
            service.cadastrarEvento(evento);

            JOptionPane.showMessageDialog(this, "Evento cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Bloqueio de Regra de Negócio", JOptionPane.WARNING_MESSAGE);
        } catch (DaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Verifique o formato das datas e dos números.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }
}