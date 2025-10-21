CREATE TABLE IF NOT EXISTS franchises (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name          VARCHAR(80)     NOT NULL,
  created_at    TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at    TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT pk_franchises PRIMARY KEY (id),
  CONSTRAINT uq_franchises_name UNIQUE (name),
  CONSTRAINT ck_franchises_name CHECK (CHAR_LENGTH(name) > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- BRANCHES (una franquicia tiene muchas sucursales)
CREATE TABLE IF NOT EXISTS branches (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  franchise_id  BIGINT UNSIGNED NOT NULL,
  name          VARCHAR(80)     NOT NULL,
  created_at    TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at    TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT pk_branches PRIMARY KEY (id),
  CONSTRAINT fk_branches_franchise
    FOREIGN KEY (franchise_id) REFERENCES franchises (id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT uq_branches_franchise_name UNIQUE (franchise_id, name),
  CONSTRAINT ck_branches_name CHECK (CHAR_LENGTH(name) > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PRODUCTS (una sucursal tiene muchos productos)
CREATE TABLE IF NOT EXISTS products (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  branch_id     BIGINT UNSIGNED NOT NULL,
  name          VARCHAR(80)     NOT NULL,
  stock         INT UNSIGNED    NOT NULL DEFAULT 0,
  created_at    TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at    TIMESTAMP(3)    NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT pk_products PRIMARY KEY (id),
  CONSTRAINT fk_products_branch
    FOREIGN KEY (branch_id) REFERENCES branches (id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT uq_products_branch_name UNIQUE (branch_id, name),
  CONSTRAINT ck_products_name CHECK (CHAR_LENGTH(name) > 0),
  CONSTRAINT ck_products_stock CHECK (stock >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ÍNDICES (sin IF NOT EXISTS por compatibilidad)
CREATE INDEX idx_branches_franchise_id ON branches (franchise_id);
CREATE INDEX idx_products_branch_id    ON products (branch_id);
CREATE INDEX idx_products_stock        ON products (stock);

-- (Opcional) Vista para HU06: top product por sucursal
DROP VIEW IF EXISTS v_top_products_per_branch;
CREATE VIEW v_top_products_per_branch AS
SELECT p.*
FROM (
  SELECT
    pr.*,
    ROW_NUMBER() OVER (PARTITION BY pr.branch_id ORDER BY pr.stock DESC, pr.name ASC) AS rn
  FROM products pr
) p
WHERE p.rn = 1;
