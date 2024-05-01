CREATE TABLE data_type
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(255)  NOT NULL,
    path        VARCHAR(255)  NOT NULL,
    content     TEXT          NOT NULL,
    attribute   VARCHAR(4096) NOT NULL,
    create_time TIMESTAMPTZ DEFAULT current_timestamp,
    update_time TIMESTAMPTZ DEFAULT current_timestamp
);

CREATE UNIQUE INDEX data_type_code_index ON data_type (code);

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
    ON data_type
    FOR EACH ROW
EXECUTE FUNCTION update_update_time();