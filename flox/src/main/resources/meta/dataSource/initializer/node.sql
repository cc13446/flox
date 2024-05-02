CREATE TABLE node
(
    id               SERIAL PRIMARY KEY,
    code             VARCHAR(255)  NOT NULL,
    type             VARCHAR(255)  NOT NULL,
    content          TEXT          NOT NULL,
    attribute        VARCHAR(4096) NOT NULL,
    param_class_list VARCHAR(4096) NOT NULL,
    result_class     VARCHAR(4096) NOT NULL,
    status           BOOLEAN       NOT NULL,
    create_time      TIMESTAMPTZ DEFAULT current_timestamp,
    update_time      TIMESTAMPTZ DEFAULT current_timestamp
);

CREATE UNIQUE INDEX node_code_index ON node (code);

CREATE
    OR
    REPLACE FUNCTION update_update_time()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.update_time := current_timestamp;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_time_trigger
    BEFORE UPDATE
    ON node
    FOR EACH ROW
EXECUTE FUNCTION update_update_time();