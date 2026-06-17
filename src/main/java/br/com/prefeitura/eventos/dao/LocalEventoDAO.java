package br.com.prefeitura.eventos.dao;

import br.com.prefeitura.eventos.model.LocalEvento;
import br.com.prefeitura.eventos.exception.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalEventoDAO {

    public void inserir(LocalEvento local) throws DaoException {
        String sql = "INSERT INTO local_evento (nome, endereco, capacidade, descricao, criado_em) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getEndereco());
            stmt.setInt(3, local.getCapacidade());
            stmt.setString(4, local.getDescricao());
            stmt.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));

            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    local.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao cadastrar o local de evento.", e);
        }
    }

    public List<LocalEvento> listarTodos() throws DaoException {
        List<LocalEvento> locais = new ArrayList<>();
        String sql = "SELECT * FROM local_evento";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                locais.add(mapearResultSetParaLocal(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar locais de evento.", e);
        }
        return locais;
    }

    public LocalEvento buscarPorId(Long id) throws DaoException {
        String sql = "SELECT * FROM local_evento WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaLocal(rs);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar local pelo ID.", e);
        }
        return null;
    }

    public void atualizar(LocalEvento local) throws DaoException {
        String sql = "UPDATE local_evento SET nome = ?, endereco = ?, capacidade = ?, descricao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getEndereco());
            stmt.setInt(3, local.getCapacidade());
            stmt.setString(4, local.getDescricao());
            stmt.setLong(5, local.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new DaoException("Nenhum local encontrado para atualização com o ID: " + local.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar o local.", e);
        }
    }

    public void deletar(Long id) throws DaoException {
        String sql = "DELETE FROM local_evento WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Não é possível deletar este local. Ele pode estar atrelado a algum evento existente.", e);
        }
    }

    // Método utilitário para converter as linhas do banco no nosso POJO
    private LocalEvento mapearResultSetParaLocal(ResultSet rs) throws SQLException {
        LocalEvento local = new LocalEvento();
        local.setId(rs.getLong("id"));
        local.setNome(rs.getString("nome"));
        local.setEndereco(rs.getString("endereco"));
        local.setCapacidade(rs.getInt("capacidade"));
        local.setDescricao(rs.getString("descricao"));
        local.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        return local;
    }
}