package br.com.prefeitura.eventos.dao;

import br.com.prefeitura.eventos.model.Recurso;
import br.com.prefeitura.eventos.exception.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    public void inserir(Recurso recurso) throws DaoException {
        String sql = "INSERT INTO recurso (nome, tipo, capacidade, descricao) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, recurso.getNome());
            stmt.setString(2, recurso.getTipo());
            
            if (recurso.getCapacidade() != null) {
                stmt.setInt(3, recurso.getCapacidade());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, recurso.getDescricao());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    recurso.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao cadastrar o recurso.", e);
        }
    }

    public Recurso buscarPorId(Long id) throws DaoException {
        String sql = "SELECT * FROM recurso WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaRecurso(rs);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar recurso pelo ID: " + id, e);
        }
        return null;
    }

    public List<Recurso> listarTodos() throws DaoException {
        List<Recurso> recursos = new ArrayList<>();
        String sql = "SELECT * FROM recurso";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                recursos.add(mapearResultSetParaRecurso(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar recursos.", e);
        }
        return recursos;
    }

    public void atualizar(Recurso recurso) throws DaoException {
        String sql = "UPDATE recurso SET nome = ?, tipo = ?, capacidade = ?, descricao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recurso.getNome());
            stmt.setString(2, recurso.getTipo());
            
            if (recurso.getCapacidade() != null) {
                stmt.setInt(3, recurso.getCapacidade());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, recurso.getDescricao());
            stmt.setLong(5, recurso.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new DaoException("Nenhum recurso encontrado para atualização.");
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar o recurso.", e);
        }
    }

    public void deletar(Long id) throws DaoException {
        String sql = "DELETE FROM recurso WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar recurso. Ele pode estar sendo utilizado em um evento.", e);
        }
    }

    private Recurso mapearResultSetParaRecurso(ResultSet rs) throws SQLException {
        Recurso recurso = new Recurso();
        recurso.setId(rs.getLong("id"));
        recurso.setNome(rs.getString("nome"));
        recurso.setTipo(rs.getString("tipo"));
        
        int capacidade = rs.getInt("capacidade");
        if (!rs.wasNull()) {
            recurso.setCapacidade(capacidade);
        }
        
        recurso.setDescricao(rs.getString("descricao"));
        return recurso;
    }
}