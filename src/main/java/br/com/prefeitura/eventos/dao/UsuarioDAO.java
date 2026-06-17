package br.com.prefeitura.eventos.dao;

import br.com.prefeitura.eventos.model.Usuario;
import br.com.prefeitura.eventos.model.enums.TipoUsuario;
import br.com.prefeitura.eventos.exception.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void inserir(Usuario usuario) throws DaoException {
        String sql = "INSERT INTO usuario (nome, email, senha_hash, tipo, telefone, criado_em) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());// começa no 1 porque o 0 é reservado para o ID gerado automaticamente
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenhaHash());
            stmt.setString(4, usuario.getTipo().name());
            stmt.setString(5, usuario.getTelefone());
            stmt.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now()));

            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getLong(1)); // Atualiza o objeto com o ID gerado pelo banco
                    }
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao cadastrar o usuário: " + usuario.getEmail() + ". O e-mail pode já estar em uso.", e);
        }
    }

    public Usuario buscarPorId(Long id) throws DaoException {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar usuário pelo ID: " + id, e);
        }
        return null;
    }

    public List<Usuario> listarTodos() throws DaoException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearResultSetParaUsuario(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar usuários.", e);
        }
        return usuarios;
    }

    public void atualizar(Usuario usuario) throws DaoException {
        String sql = "UPDATE usuario SET nome = ?, email = ?, tipo = ?, telefone = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTipo().name());
            stmt.setString(4, usuario.getTelefone());
            stmt.setLong(5, usuario.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new DaoException("Nenhum usuário encontrado para atualização com o ID: " + usuario.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar o usuário: " + usuario.getNome(), e);
        }
    }

    public void deletar(Long id) throws DaoException {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar usuário. Ele pode estar atrelado a algum evento ou inscrição.", e);
        }
    }

    // Método utilitário para evitar duplicação de código ao montar o objeto
    private Usuario mapearResultSetParaUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenhaHash(rs.getString("senha_hash"));
        usuario.setTipo(TipoUsuario.valueOf(rs.getString("tipo")));
        usuario.setTelefone(rs.getString("telefone"));
        usuario.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        return usuario;
    }
}