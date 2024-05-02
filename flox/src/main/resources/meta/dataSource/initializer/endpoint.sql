CREATE TABLE endpoint
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(255)  NOT NULL,
    path        VARCHAR(1024) NOT NULL,
    method      VARCHAR(255)  NOT NULL,
    flox_code   VARCHAR(255)  NOT NULL,
    attribute   VARCHAR(4096) NOT NULL,
    status      BOOLEAN       NOT NULL,
    create_time TIMESTAMPTZ DEFAULT current_timestamp,
    update_time TIMESTAMPTZ DEFAULT current_timestamp
);

CREATE UNIQUE INDEX endpoint_code_index ON endpoint (code);

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
    ON endpoint
    FOR EACH ROW
EXECUTE FUNCTION update_update_time();