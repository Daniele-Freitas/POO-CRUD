package br.com.prefeitura.eventos.service;

import br.com.prefeitura.eventos.dao.EventoDAO;
import br.com.prefeitura.eventos.dao.InscricaoDAO;
import br.com.prefeitura.eventos.exception.DaoException;
import br.com.prefeitura.eventos.exception.RegraNegocioException;
import br.com.prefeitura.eventos.model.Evento;
import br.com.prefeitura.eventos.model.Inscricao;

public class InscricaoService {

    private InscricaoDAO inscricaoDAO;
    private EventoDAO eventoDAO;

    // Construtor injetando as dependências (boa prática de engenharia)
    public InscricaoService() {
        this.inscricaoDAO = new InscricaoDAO();
        this.eventoDAO = new EventoDAO();
    }

    public void realizarInscricao(Inscricao inscricao) throws RegraNegocioException, DaoException {
        // 1. Buscar o evento para saber a capacidade total
        Evento evento = eventoDAO.buscarPorId(inscricao.getEventoId());
        
        if (evento == null) {
            throw new RegraNegocioException("Evento não encontrado no sistema.");
        }

        // 2. REGRA DE NEGÓCIO: Verificação de Lotação
        int totalInscritos = inscricaoDAO.contarInscricoesPorEvento(evento.getId());
        
        if (totalInscritos >= evento.getCapacidade()) {
            throw new RegraNegocioException("Não é possível realizar a inscrição. O evento '" + evento.getTitulo() + "' já atingiu sua capacidade máxima de " + evento.getCapacidade() + " pessoas.");
        }

        // 3. REGRA DE NEGÓCIO: Prevenção de Reservas Duplicadas
        // Lembra que colocamos um UNIQUE constraint no banco? Aqui nós capturamos o erro do banco 
        // e traduzimos para uma mensagem limpa para o usuário.
        try {
            inscricaoDAO.inserir(inscricao);
            System.out.println("Inscrição realizada com sucesso para o evento: " + evento.getTitulo());
        } catch (DaoException e) {
            if (e.getMessage().contains("O usuário pode já estar inscrito")) {
                throw new RegraNegocioException("Este usuário já possui uma inscrição ativa para este evento.");
            } else {
                throw e; // Repassa se for outro tipo de erro de banco
            }
        }
    }
}