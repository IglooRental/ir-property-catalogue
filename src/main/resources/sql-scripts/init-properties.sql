--DROP TABLE IF EXISTS "properties";
--CREATE TABLE IF NOT EXISTS "properties" ("id" VARCHAR(128) PRIMARY KEY, "location" VARCHAR(256) NOT NULL, "owner_id" VARCHAR(128) NOT NULL);
INSERT INTO "properties" ("id", "location", "owner_id") VALUES ('0', 'location 1', '1');
INSERT INTO "properties" ("id", "location", "owner_id") VALUES ('1', 'location 2', '1');
INSERT INTO "properties" ("id", "location", "owner_id") VALUES ('2', 'location 3', '2');
