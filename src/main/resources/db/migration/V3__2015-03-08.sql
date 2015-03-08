DROP INDEX IF EXISTS st_session_userid_key;
CREATE INDEX st_session_userid_key ON st_session (userid);