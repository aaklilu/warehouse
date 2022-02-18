CREATE TABLE article
(
    id          UUID         NOT NULL,
    article_id  VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    stock_level INTEGER      NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_article PRIMARY KEY (id)
);

CREATE TABLE line_item
(
    id           UUID         NOT NULL,
    product_id   VARCHAR(255) NOT NULL,
    product_name VARCHAR(255),
    quantity     INTEGER      NOT NULL,
    unit_price   DOUBLE PRECISION,
    order_id     UUID,
    CONSTRAINT pk_line_item PRIMARY KEY (id)
);

CREATE TABLE product
(
    id         UUID             NOT NULL,
    name       VARCHAR(255)     NOT NULL,
    price      DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE product_article
(
    id          UUID         NOT NULL,
    article_id  VARCHAR(255) NOT NULL,
    amount_of   INTEGER      NOT NULL,
    stock_level INTEGER      NOT NULL,
    product_id  UUID,
    CONSTRAINT pk_product_article PRIMARY KEY (id)
);

CREATE TABLE product_order
(
    id                 UUID         NOT NULL,
    name               VARCHAR(255) NOT NULL,
    status             VARCHAR(255) NOT NULL,
    status_message     VARCHAR(255),
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    o_customer_name    VARCHAR(255) NOT NULL,
    o_customer_phone   VARCHAR(255) NOT NULL,
    o_customer_address VARCHAR(255) NOT NULL,
    CONSTRAINT pk_product_order PRIMARY KEY (id)
);

CREATE TABLE product_order_article
(
    id         UUID         NOT NULL,
    article_id VARCHAR(255) NOT NULL,
    amount_of  INTEGER      NOT NULL,
    order_id   UUID,
    CONSTRAINT pk_product_order_article PRIMARY KEY (id)
);

ALTER TABLE article
    ADD CONSTRAINT uc_article_articleid UNIQUE (article_id);

CREATE INDEX idx_article__article_id_name ON article (article_id, name);

CREATE INDEX idx_line_item___product_id ON line_item (product_id);

CREATE INDEX idx_order___name ON product_order (name);

CREATE INDEX idx_order___o_customer_phone ON product_order (o_customer_phone);

CREATE UNIQUE INDEX idx_order_article__article_id ON product_order_article (article_id);

CREATE INDEX idx_product___name ON product (name);

CREATE INDEX idx_product_article__article_id ON product_article (article_id);

CREATE UNIQUE INDEX idx_product_article__product_article_id ON product_article (product_id, article_id);

ALTER TABLE line_item
    ADD CONSTRAINT FK_LINE_ITEM_ON_ORDER FOREIGN KEY (order_id) REFERENCES product_order (id);

CREATE INDEX idx_line_item___order_id ON line_item (order_id);

ALTER TABLE product_article
    ADD CONSTRAINT FK_PRODUCT_ARTICLE_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE product_order_article
    ADD CONSTRAINT FK_PRODUCT_ORDER_ARTICLE_ON_ORDER FOREIGN KEY (order_id) REFERENCES product_order (id);

CREATE INDEX idx_order_article__order_id ON product_order_article (order_id);