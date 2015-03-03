DROP TABLE IF EXISTS gasguzzlerdb.public.st_user CASCADE;
CREATE TABLE gasguzzlerdb.public.st_user
(
  userid   SERIAL PRIMARY KEY NOT NULL,
  username VARCHAR(100)       NOT NULL,
  password VARCHAR(100)       NOT NULL,
  email    VARCHAR(100)       NOT NULL,
  salt     VARCHAR(100)       NOT NULL,
  role     VARCHAR(100)       NOT NULL
);


DROP TABLE IF EXISTS gasguzzlerdb.public.st_session CASCADE;
CREATE TABLE gasguzzlerdb.public.st_session
(
  token   VARCHAR(23) PRIMARY KEY NOT NULL,
  userid  INT,
  created TIMESTAMP               NOT NULL,
  FOREIGN KEY (userid) REFERENCES st_user (userid)
);
CREATE UNIQUE INDEX st_session_userid_key ON st_session (userid);