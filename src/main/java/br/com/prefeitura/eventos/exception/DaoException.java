package br.com.prefeitura.eventos.exception;

// Estendendo 'Exception' em vez de 'RuntimeException' para forçar o tratamento (Checked Exception)
public class DaoException extends Exception {
    
    public DaoException(String mensagem) {
        super(mensagem);
    }

    public DaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}