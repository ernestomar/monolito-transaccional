# Proyecto monolito que gestiona la transacción en multiples BBDD

## Bases de Datos

### Inventarios:

1. Creamos la BBDD

```sh
docker run --name mysql-warehouse -e MYSQL_ROOT_PASSWORD=123456 -p 13306:3306 -d mysql:9
```

2. Para conectarse al Mysql usamos:

```sh
docker exec -it mysql-warehouse mysql -u root -p
```

3. Creamos la Tabla.

```sql
-- CREAMOS LA BBDD

CREATE DATABASE warehouse;
use warehouse;

-- Crear tabla product con información de stock
CREATE TABLE product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    cost DECIMAL(10,2),
    sku VARCHAR(50) UNIQUE,
    stock_quantity INT NOT NULL DEFAULT 0,
    min_stock_level INT DEFAULT 0,
    max_stock_level INT DEFAULT 1000,
    supplier VARCHAR(255),
    brand VARCHAR(100),
    weight DECIMAL(8,2),
    dimensions VARCHAR(50),
    status ENUM('active', 'inactive', 'discontinued') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insertar 10 filas de ejemplo
INSERT INTO product (
    name, 
    description, 
    category, 
    price, 
    cost, 
    sku, 
    stock_quantity, 
    min_stock_level, 
    max_stock_level, 
    supplier, 
    brand, 
    weight, 
    dimensions, 
    status
) VALUES 
(
    'Laptop Dell Inspiron 15', 
    'Laptop con procesador Intel i5, 8GB RAM, 256GB SSD', 
    'Electrónicos', 
    899.99, 
    650.00, 
    'DELL-INS15-001', 
    25, 
    5, 
    100, 
    'Dell Technologies', 
    'Dell', 
    2.10, 
    '35.8 x 24.2 x 2.0 cm', 
    'active'
),
(
    'Smartphone Samsung Galaxy A54', 
    'Smartphone con pantalla 6.4", 128GB almacenamiento, cámara 50MP', 
    'Electrónicos', 
    449.99, 
    320.00, 
    'SAM-GAL-A54-128', 
    45, 
    10, 
    200, 
    'Samsung Electronics', 
    'Samsung', 
    0.20, 
    '15.8 x 7.7 x 0.8 cm', 
    'active'
),
(
    'Cafetera Nespresso Vertuo', 
    'Cafetera de cápsulas con tecnología Centrifusion', 
    'Electrodomésticos', 
    199.99, 
    120.00, 
    'NES-VERT-BLK', 
    12, 
    3, 
    50, 
    'Nespresso', 
    'Nespresso', 
    4.20, 
    '31.7 x 25.4 x 30.5 cm', 
    'active'
),
(
    'Mesa de Oficina Ergonómica', 
    'Mesa ajustable en altura con superficie de roble', 
    'Muebles', 
    299.99, 
    180.00, 
    'DESK-ERG-OAK-120', 
    8, 
    2, 
    30, 
    'Oficina Plus', 
    'ErgoDesk', 
    25.50, 
    '120 x 80 x 75 cm', 
    'active'
),
(
    'Auriculares Sony WH-1000XM5', 
    'Auriculares inalámbricos con cancelación de ruido', 
    'Electrónicos', 
    349.99, 
    250.00, 
    'SONY-WH1000XM5', 
    18, 
    5, 
    80, 
    'Sony Corporation', 
    'Sony', 
    0.25, 
    '19.4 x 17.2 x 8.2 cm', 
    'active'
),
(
    'Zapatillas Running Nike Air Zoom', 
    'Zapatillas deportivas para running con amortiguación Air Zoom', 
    'Deportes', 
    129.99, 
    75.00, 
    'NIKE-AIR-ZOOM-42', 
    35, 
    8, 
    150, 
    'Nike Inc.', 
    'Nike', 
    0.32, 
    'Talla 42', 
    'active'
),
(
    'Libro "Cien Años de Soledad"', 
    'Novela clásica de Gabriel García Márquez', 
    'Libros', 
    15.99, 
    8.00, 
    'BOOK-CIEN-ANOS', 
    120, 
    20, 
    500, 
    'Editorial Sudamericana', 
    'Sudamericana', 
    0.40, 
    '19 x 12 x 3 cm', 
    'active'
),
(
    'Silla Gaming RGB', 
    'Silla ergonómica para gaming con iluminación LED RGB', 
    'Muebles', 
    399.99, 
    220.00, 
    'CHAIR-GAME-RGB', 
    6, 
    2, 
    25, 
    'GameChair Pro', 
    'ProGamer', 
    18.00, 
    '68 x 68 x 132 cm', 
    'active'
),
(
    'Tablet iPad Air 10.9"', 
    'Tablet con chip M1, 64GB, WiFi, pantalla Liquid Retina', 
    'Electrónicos', 
    599.99, 
    450.00, 
    'APPLE-IPAD-AIR-64', 
    15, 
    3, 
    75, 
    'Apple Inc.', 
    'Apple', 
    0.46, 
    '24.8 x 17.9 x 0.6 cm', 
    'active'
),
(
    'Set de Herramientas 120 piezas', 
    'Kit completo de herramientas para hogar con maletín', 
    'Herramientas', 
    89.99, 
    45.00, 
    'TOOLS-SET-120', 
    22, 
    5, 
    100, 
    'Tools & More', 
    'MasterTool', 
    5.20, 
    '45 x 35 x 8 cm', 
    'active'
);

-- Consulta para verificar los datos insertados
SELECT 
    id,
    name,
    category,
    price,
    stock_quantity,
    min_stock_level,
    status
FROM product 
ORDER BY id;
```

### Contabilidad:

Iniciamos el contenedor

```sh
docker run --name postgres-db -e POSTGRES_PASSWORD=123456 -p 15432:5432 -d postgres
```
Conectamos al postgres:

```sh
docker exec -it postgres-db  bash
# Dentro el contenedor ejecutamos

su - postgres
psql
```

Base de datos de contabilidad

```sql
CREATE DATABASE accounting;

\c accounting

-- Crear tabla JOURNAL para registro de diario contable en PostgreSQL
CREATE TABLE JOURNAL (
    id SERIAL PRIMARY KEY,
    journal_entry_number VARCHAR(20) UNIQUE NOT NULL,
    transaction_date DATE NOT NULL,
    posting_date DATE DEFAULT CURRENT_DATE,
    account_code VARCHAR(20) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    reference_number VARCHAR(50),
    debit_amount DECIMAL(15,2) DEFAULT 0.00,
    credit_amount DECIMAL(15,2) DEFAULT 0.00,
    balance_type CHAR(1) CHECK (balance_type IN ('D', 'C')),
    department VARCHAR(100),
    cost_center VARCHAR(50),
    project_code VARCHAR(50),
    currency_code CHAR(3) DEFAULT 'USD',
    exchange_rate DECIMAL(10,6) DEFAULT 1.000000,
    source_document VARCHAR(100),
    created_by VARCHAR(100) NOT NULL,
    approved_by VARCHAR(100),
    approval_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'draft' CHECK (status IN ('draft', 'posted', 'reversed')),
    reversed_by_entry VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_journal_transaction_date ON JOURNAL(transaction_date);
CREATE INDEX idx_journal_account_code ON JOURNAL(account_code);
CREATE INDEX idx_journal_entry_number ON JOURNAL(journal_entry_number);
CREATE INDEX idx_journal_status ON JOURNAL(status);
CREATE INDEX idx_journal_posting_date ON JOURNAL(posting_date);

-- Crear trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_journal_updated_at 
    BEFORE UPDATE ON JOURNAL 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Constraint para asegurar que cada entrada tenga débito O crédito (no ambos cero)
ALTER TABLE JOURNAL ADD CONSTRAINT chk_debit_or_credit 
    CHECK (debit_amount > 0 OR credit_amount > 0);

-- Constraint para asegurar que no se tenga débito Y crédito al mismo tiempo
ALTER TABLE JOURNAL ADD CONSTRAINT chk_not_both_debit_credit 
    CHECK (NOT (debit_amount > 0 AND credit_amount > 0));

-- Comentarios en la tabla y columnas principales
COMMENT ON TABLE JOURNAL IS 'Tabla principal del diario contable para registrar todas las transacciones';
COMMENT ON COLUMN JOURNAL.journal_entry_number IS 'Número único de la entrada del diario';
COMMENT ON COLUMN JOURNAL.transaction_date IS 'Fecha de la transacción';
COMMENT ON COLUMN JOURNAL.posting_date IS 'Fecha de contabilización';
COMMENT ON COLUMN JOURNAL.account_code IS 'Código de la cuenta contable';
COMMENT ON COLUMN JOURNAL.debit_amount IS 'Monto del débito';
COMMENT ON COLUMN JOURNAL.credit_amount IS 'Monto del crédito';
COMMENT ON COLUMN JOURNAL.balance_type IS 'Tipo de saldo: D=Débito, C=Crédito';
``` 

### Ventas

En el mismo postgres  cramos las BBDD de ventas

```sql
CREATE DATABASE sales;
\c sales

-- Crear tabla Sale con referencia a productos
CREATE TABLE Sale (
    id SERIAL PRIMARY KEY,
    sale_number VARCHAR(20) UNIQUE NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    total_amount DECIMAL(12,2),
    discount_percentage DECIMAL(5,2) DEFAULT 0.00 CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    final_amount DECIMAL(12,2) GENERATED ALWAYS AS (total_amount - discount_amount) STORED,
    sale_date DATE NOT NULL DEFAULT CURRENT_DATE,
    customer_id INT,
    customer_name VARCHAR(255),
    salesperson VARCHAR(100),
    payment_method VARCHAR(50) DEFAULT 'cash',
    payment_status VARCHAR(20) DEFAULT 'pending' CHECK (payment_status IN ('pending', 'paid', 'partial', 'cancelled')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para optimizar consultas
CREATE INDEX idx_sale_product_id ON Sale(product_id);
CREATE INDEX idx_sale_date ON Sale(sale_date);
CREATE INDEX idx_sale_customer_id ON Sale(customer_id);
CREATE INDEX idx_sale_payment_status ON Sale(payment_status);
CREATE INDEX idx_sale_number ON Sale(sale_number);

-- Crear trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_sale_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_sale_updated_at 
    BEFORE UPDATE ON Sale 
    FOR EACH ROW 
    EXECUTE FUNCTION update_sale_updated_at();

-- Comentarios en la tabla y columnas principales
COMMENT ON TABLE Sale IS 'Tabla de ventas que registra cada transacción de venta';
COMMENT ON COLUMN Sale.sale_number IS 'Número único de la venta';
COMMENT ON COLUMN Sale.product_id IS 'ID del producto vendido (FK a tabla product)';
COMMENT ON COLUMN Sale.quantity IS 'Cantidad vendida del producto';
COMMENT ON COLUMN Sale.unit_price IS 'Precio unitario al momento de la venta';
COMMENT ON COLUMN Sale.total_amount IS 'Monto total calculado automáticamente (cantidad × precio)';
COMMENT ON COLUMN Sale.final_amount IS 'Monto final después de descuentos';
```