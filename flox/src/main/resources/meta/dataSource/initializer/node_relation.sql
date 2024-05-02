CREATE TABLE node_relation
(
    id                 SERIAL PRIMARY KEY,
    code               VARCHAR(255) NOT NULL,
    sub_flox_code      VARCHAR(255) NOT NULL,
    pre_node_code_list TEXT         NOT NULL,
    status             BOOLEAN      NOT NULL,
    create_time        TIMESTAMPTZ DEFAULT current_timestamp,
    update_time        TIMESTAMPTZ DEFAULT current_timestamp
);

CREATE UNIQUE INDEX node_relation_code_index ON node_relation (code, sub_flox_code);

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
    ON node_relation
    FOR EACH ROW
EXECUTE FUNCTION update_update_time();