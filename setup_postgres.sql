DROP TABLE IF EXISTS "st_user" CASCADE;
CREATE TABLE "st_user" (
  "userid" SERIAL PRIMARY KEY,
  "username" varchar(100) NOT NULL,
  "password" varchar(100) NOT NULL,
  "email" varchar(100) NOT NULL,
  "salt" varchar(100) NOT NULL,
  "role" varchar(100) NOT NULL
);


DROP TABLE IF EXISTS "st_session" CASCADE;
CREATE TABLE "st_session" (
  "token" varchar(23) NOT NULL,
  "userid" int REFERENCES "st_user" ("userid"),
  "created" timestamp NOT NULL,
  PRIMARY KEY ("token")
);