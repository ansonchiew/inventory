DROP SEQUENCE IF EXISTS inventory_id_seq;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS sub_category;
DROP TABLE IF EXISTS category;

CREATE SEQUENCE inventory_id_seq start with 1 increment by 50;

CREATE TABLE category (
  category_id int not null,
  name varchar(50) not null UNIQUE,
  PRIMARY KEY (category_id)
 );

CREATE TABLE sub_category (
  sub_category_id int not null UNIQUE,
  category_id int not null,
  name varchar(50) not null UNIQUE,
  PRIMARY KEY (sub_category_id, category_id),
  FOREIGN KEY (category_id)
  	REFERENCES category(category_id)
    ON DELETE CASCADE 
);

CREATE TABLE inventory (
  inventory_id int not null,
  sub_category_id int not null,
  category_id int not null,
  name varchar(50) not null UNIQUE,
  quantity int DEFAULT 0,
  PRIMARY KEY (inventory_id),
  FOREIGN KEY (sub_category_id, category_id)
  	REFERENCES sub_category(sub_category_id, category_id)
    ON DELETE CASCADE
 );