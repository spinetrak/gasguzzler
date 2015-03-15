CREATE TABLE st_metrics
(
  m_timestamp BIGINT       NOT NULL,
  m_name      VARCHAR(200) NOT NULL,
  m_count     INTEGER      NOT NULL,
  m_rate      FLOAT
);

CREATE INDEX st_metrics_timestamp_key ON st_metrics (m_timestamp);
CREATE INDEX st_metrics_name_key ON st_metrics (m_name);