package br.com.prefeitura.eventos.dao;

import br.com.prefeitura.eventos.model.Evento;
import br.com.prefeitura.eventos.model.enums.StatusEvento;
import br.com.prefeitura.eventos.exception.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public void inserir(Evento evento) throws DaoException {
        String sql = "INSERT INTO evento (titulo, descricao, data_inicio, data_fim, local_id, capacidade, categoria, organizador_id, status, criado_em) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, evento.getTitulo());
            stmt.setString(2, evento.getDescricao());
            stmt.setTimestamp(3, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(4, Timestamp.valueOf(evento.getDataFim()));
            
            // Tratamento para null no Local ID (pode ser um evento online)
            if (evento.getLocalId() != null) {
                stmt.setLong(5, evento.getLocalId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            
            stmt.setInt(6, evento.getCapacidade());
            stmt.setString(7, evento.getCategoria());
            stmt.setLong(8, evento.getOrganizadorId());
            stmt.setString(9, evento.getStatus().name());
            stmt.setTimestamp(10, Timestamp.valueOf(java.time.LocalDateTime.now()));

            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) evento.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao criar evento. Verifique se o organizador e o local existem no sistema.", e);
        }
    }

    public List<Evento> listarTodos() throws DaoException {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM evento";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                eventos.add(mapearResultSetParaEvento(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao listar os eventos.", e);
        }
        return eventos;
    }

    public Evento buscarPorId(Long id) throws DaoException {
        String sql = "SELECT * FROM evento WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaEvento(rs);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao buscar evento pelo ID: " + id, e);
        }
        return null;
    }

    public void atualizar(Evento evento) throws DaoException {
        String sql = "UPDATE evento SET titulo = ?, descricao = ?, data_inicio = ?, data_fim = ?, local_id = ?, capacidade = ?, categoria = ?, organizador_id = ?, status = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getTitulo());
            stmt.setString(2, evento.getDescricao());
            stmt.setTimestamp(3, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(4, Timestamp.valueOf(evento.getDataFim()));
            
            if (evento.getLocalId() != null) {
                stmt.setLong(5, evento.getLocalId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            
            stmt.setInt(6, evento.getCapacidade());
            stmt.setString(7, evento.getCategoria());
            stmt.setLong(8, evento.getOrganizadorId());
            stmt.setString(9, evento.getStatus().name());
            stmt.setLong(10, evento.getId());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new DaoException("Nenhum evento encontrado para atualização com o ID: " + evento.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao atualizar o evento: " + evento.getTitulo(), e);
        }
    }

    public void deletar(Long id) throws DaoException {
        String sql = "DELETE FROM evento WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Erro ao deletar evento. Certifique-se de que não há inscrições ou recursos atrelados a ele antes de excluir.", e);
        }
    }

    public boolean existeConflitoDeLocal(Long localId, java.time.LocalDateTime novoInicio, java.time.LocalDateTime novoFim) throws DaoException {
        // Query otimizada: Dois eventos se cruzam se, e somente se, o Início do Evento A acontece ANTES do Fim do Evento B, 
        // E o Fim do Evento A acontece DEPOIS do Início do Evento B.
        String sql = "SELECT COUNT(*) FROM evento WHERE local_id = ? AND status != 'CANCELADO' AND (? < data_fim AND ? > data_inicio)";
                     
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setLong(1, localId);
            stmt.setTimestamp(2, Timestamp.valueOf(novoInicio)); // ? < data_fim
            stmt.setTimestamp(3, Timestamp.valueOf(novoFim));    // ? > data_inicio
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Se o COUNT for maior que 0, existe conflito
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erro ao verificar disponibilidade do local.", e);
        }
        return false;
    }

    // Método utilitário para evitar repetição de código no listarTodos e buscarPorId
    private Evento mapearResultSetParaEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getLong("id"));
        evento.setTitulo(rs.getString("titulo"));
        evento.setDescricao(rs.getString("descricao"));
        evento.setDataInicio(rs.getTimestamp("data_inicio").toLocalDateTime());
        evento.setDataFim(rs.getTimestamp("data_fim").toLocalDateTime());
        
        long localId = rs.getLong("local_id");
        if (!rs.wasNull()) {
            evento.setLocalId(localId);
        }
        
        evento.setCapacidade(rs.getInt("capacidade"));
        evento.setCategoria(rs.getString("categoria"));
        evento.setOrganizadorId(rs.getLong("organizador_id"));
        evento.setStatus(StatusEvento.valueOf(rs.getString("status")));
        evento.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        
        return evento;
    }
    
}