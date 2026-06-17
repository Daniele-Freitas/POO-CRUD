package br.com.prefeitura.eventos.dao;

import br.com.prefeitura.eventos.model.Inscricao;
import br.com.prefeitura.eventos.model.enums.StatusInscricao;
import br.com.prefeitura.eventos.exception.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscricaoDAO {

    public void inserir(Inscricao inscricao) throws DaoException {
        String sql = "INSERT INTO inscricao (evento_id, usuario_id, data_inscricao, status, observacao) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, inscricao.getEventoId());
            stmt.setLong(2, inscricao.getUsuarioId());
            stmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setString(4, inscricao.getStatus().name());
            stmt.setString(5, inscricao.getObservacao());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    inscricao.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            // Como criamos uma restrição UNIQUE no banco para (evento_id, usuario_id), 
            // essa exceção vai estourar se tentarem inscrever o mesmo usuário duas vezes no mesmo evento.
            throw new DaoException("Erro ao realizar inscrição. O usuário pode já estar inscrito neste evento.", e);
        }
    }

    public Inscricao buscarPorId(Long id) throws DaoException {
        String sql = "SELECT * FROM inscricao WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaInscricao(rs);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar inscrição pelo ID: " + id, e);
        }
        return null;
    }

    public List<Inscricao> listarTodos() throws DaoException {
        List<Inscricao> inscricoes = new ArrayList<>();
        String sql = "SELECT * FROM inscricao";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inscricoes.add(mapearResultSetParaInscricao(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar inscrições.", e);
        }
        return inscricoes;
    }
    
    // Método extra para ajudar nas Regras de Negócio (Contar inscritos em um evento)
    public int contarInscricoesPorEvento(Long eventoId) throws DaoException {
        String sql = "SELECT COUNT(*) FROM inscricao WHERE evento_id = ? AND status != 'CANCELADA'";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setLong(1, eventoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao contar inscrições do evento.", e);
        }
        return 0;
    }

    public void atualizarStatus(Long id, StatusInscricao novoStatus) throws DaoException {
        String sql = "UPDATE inscricao SET status = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus.name());
            stmt.setLong(2, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new DaoException("Nenhuma inscrição encontrada para atualização com o ID: " + id);
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar o status da inscrição.", e);
        }
    }

    public void deletar(Long id) throws DaoException {
        String sql = "DELETE FROM inscricao WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar inscrição.", e);
        }
    }

    private Inscricao mapearResultSetParaInscricao(ResultSet rs) throws SQLException {
        Inscricao inscricao = new Inscricao();
        inscricao.setId(rs.getLong("id"));
        inscricao.setEventoId(rs.getLong("evento_id"));
        inscricao.setUsuarioId(rs.getLong("usuario_id"));
        inscricao.setDataInscricao(rs.getTimestamp("data_inscricao").toLocalDateTime());
        inscricao.setStatus(StatusInscricao.valueOf(rs.getString("status")));
        inscricao.setObservacao(rs.getString("observacao"));
        return inscricao;
    }
}