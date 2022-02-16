CREATE TABLE article
(
    id           UUID         NOT NULL,
    article_id   VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    stock_level  INTEGER      NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_article PRIMARY KEY (id)
);

ALTER TABLE article
    ADD CONSTRAINT uc_article_articleid UNIQUE (article_id);

CREATE INDEX idx_article__article_id_name ON article (article_id, name);