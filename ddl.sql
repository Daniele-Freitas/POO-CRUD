-- 1. Criação da tabela de Locais [cite: 49-51, 58-61]
CREATE TABLE local_evento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    capacidade INT NOT NULL,
    descricao VARCHAR(255),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Criação da tabela de Usuários [cite: 68-75]
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('ORGANIZADOR', 'VOLUNTARIO', 'PUBLICO')),
    telefone VARCHAR(30),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Criação da tabela de Eventos [cite: 52-65]
CREATE TABLE evento (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    local_id BIGINT,
    capacidade INT NOT NULL,
    categoria VARCHAR(100),
    organizador_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'AGENDADO' CHECK (status IN ('AGENDADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO')),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evento_local FOREIGN KEY (local_id) REFERENCES local_evento(id) ON DELETE SET NULL,
    CONSTRAINT fk_evento_organizador FOREIGN KEY (organizador_id) REFERENCES usuario(id)
);

-- 4. Criação da tabela de Recursos [cite: 31-32, 35-38]
CREATE TABLE recurso (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    tipo VARCHAR(100),
    capacidade INT,
    descricao VARCHAR(255)
);

-- 5. Criação da tabela associativa Evento_Recurso [cite: 33-34, 39-40]
CREATE TABLE evento_recurso (
    evento_id BIGINT,
    recurso_id BIGINT,
    quantidade INT DEFAULT 1,
    PRIMARY KEY (evento_id, recurso_id),
    CONSTRAINT fk_er_evento FOREIGN KEY (evento_id) REFERENCES evento(id) ON DELETE CASCADE,
    CONSTRAINT fk_er_recurso FOREIGN KEY (recurso_id) REFERENCES recurso(id) ON DELETE CASCADE
);

-- 6. Criação da tabela de Inscrições [cite: 42-48]
CREATE TABLE inscricao (
    id BIGSERIAL PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data_inscricao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDENTE' CHECK (status IN ('PENDENTE', 'CONFIRMADA', 'CANCELADA')),
    observacao VARCHAR(255),
    CONSTRAINT fk_inscricao_evento FOREIGN KEY (evento_id) REFERENCES evento(id) ON DELETE CASCADE,
    CONSTRAINT fk_inscricao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT uk_inscricao_unica UNIQUE (evento_id, usuario_id)
);