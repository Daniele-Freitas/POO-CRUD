package br.com.prefeitura.eventos.service;

import br.com.prefeitura.eventos.dao.EventoDAO;
import br.com.prefeitura.eventos.exception.DaoException;
import br.com.prefeitura.eventos.exception.RegraNegocioException;
import br.com.prefeitura.eventos.model.Evento;

public class EventoService {

    private EventoDAO eventoDAO;

    public EventoService() {
        this.eventoDAO = new EventoDAO();
    }

    public void cadastrarEvento(Evento evento) throws RegraNegocioException, DaoException {
        // Validação básica de datas
        if (evento.getDataFim().isBefore(evento.getDataInicio())) {
            throw new RegraNegocioException("A data de término do evento não pode ser anterior à data de início.");
        }

        // REGRA DE NEGÓCIO: Conflito de Horário em Locais [cite: 22]
        if (evento.getLocalId() != null) {
            boolean temConflito = eventoDAO.existeConflitoDeLocal(evento.getLocalId(), evento.getDataInicio(), evento.getDataFim());
            if (temConflito) {
                throw new RegraNegocioException("Conflito de agenda: Já existe um evento marcado para este local neste mesmo horário.");
            }
        }

        // Se passou pelas regras, salva no banco
        eventoDAO.inserir(evento);
        System.out.println("Evento '" + evento.getTitulo() + "' cadastrado com sucesso!");
    }
}