CREATE TABLE data_source_action
(
    id               SERIAL PRIMARY KEY,
    data_source_code VARCHAR(255)  NOT NULL,
    code             VARCHAR(255)  NOT NULL,
    type             VARCHAR(255)  NOT NULL,
    sql              TEXT          NOT NULL,
    status           BOOLEAN       NOT NULL,
    attribute        VARCHAR(4096) NOT NULL,
    create_time      TIMESTAMPTZ DEFAULT current_timestamp,
    update_time      TIMESTAMPTZ DEFAULT current_timestamp
);

CREATE UNIQUE INDEX data_source_action_code_index ON data_source_action (data_source_code, code);

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
    ON data_source_action
    FOR EACH ROW
EXECUTE FUNCTION update_update_time();