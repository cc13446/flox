CREATE TABLE data_source
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(255)  NOT NULL,
    type        VARCHAR(255)  NOT NULL,
    url         VARCHAR(1024) NOT NULL,
    username    VARCHAR(255)  NOT NULL,
    password    VARCHAR(255)  NOT NULL,
    status      BOOLEAN       NOT NULL,
    config      VARCHAR(4096) NOT NULL,
    attribute   VARCHAR(4096) NOT NULL,
    create_time TIMESTAMPTZ DEFAULT current_timestamp,
    update_time TIMESTAMPTZ DEFAULT current_timestamp
);

CREATE UNIQUE INDEX data_source_code_index ON data_source (code);

CREATE OR REPLACE FUNCTION update_update_time()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.update_time := current_timestamp;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_time_trigger
    BEFORE UPDATE
    ON data_source
    FOR EACH ROW
EXECUTE FUNCTION update_update_time();