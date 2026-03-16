CREATE DATABASE BTG;

use btg;

CREATE TABLE Cliente (
    id INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    ciudad VARCHAR(50) NOT NULL
);

CREATE TABLE Sucursal (
    id INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    ciudad VARCHAR(50) NOT NULL
);

CREATE TABLE Producto (
    id INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    tipoProducto VARCHAR(50) NOT NULL
);

CREATE TABLE Inscripcion (
    idProducto INT,
    idCliente INT,
    PRIMARY KEY (idProducto, idCliente),
    FOREIGN KEY (idProducto) REFERENCES Producto(id),
    FOREIGN KEY (idCliente) REFERENCES Cliente(id)
);

CREATE TABLE Disponibilidad (
    idSucursal INT,
    idProducto INT,
    PRIMARY KEY (idSucursal, idProducto),
    FOREIGN KEY (idSucursal) REFERENCES Sucursal(id),
    FOREIGN KEY (idProducto) REFERENCES Producto(id)
);


CREATE TABLE Visitan (
    idSucursal INT,
    idCliente INT,
    fechaVisita DATE NOT NULL,
    PRIMARY KEY (idSucursal, idCliente),
    FOREIGN KEY (idSucursal) REFERENCES Sucursal(id),
    FOREIGN KEY (idCliente) REFERENCES Cliente(id)
);


INSERT INTO Cliente VALUES
(1,'Juan','Perez','Bogota'),
(2,'Maria','Lopez','Medellin'),
(3,'Carlos','Ramirez','Cali');

INSERT INTO Sucursal VALUES
(1,'Sucursal Norte','Bogota'),
(2,'Sucursal Centro','Medellin');


INSERT INTO Producto VALUES
(1,'Fondo Inversion','Financiero'),
(2,'Cuenta Premium','Bancario');

INSERT INTO Inscripcion VALUES
(1,1),
(2,2),
(1,3);

INSERT INTO Disponibilidad VALUES
(1,1),
(2,2);


INSERT INTO Visitan VALUES
(1,1,'2025-03-01'),
(2,2,'2025-03-02'),
(1,3,'2025-03-03');

SELECT * FROM Producto;
SELECT * FROM Sucursal;

SELECT
c.nombre AS cliente,
p.nombre AS producto,
s.nombre AS sucursal,
v.fechaVisita
FROM Cliente c
JOIN Inscripcion i ON c.id = i.idCliente
JOIN Producto p ON p.id = i.idProducto
JOIN Disponibilidad d ON d.idProducto = p.id
JOIN Sucursal s ON s.id = d.idSucursal
JOIN Visitan v ON v.idSucursal = s.id AND v.idCliente = c.id;



#Consulta Requerida

SELECT DISTINCT c.nombre
FROM Cliente c
JOIN Inscripcion i
    ON c.id = i.idCliente
JOIN Producto p
    ON p.id = i.idProducto
JOIN Disponibilidad d
    ON d.idProducto = p.id
JOIN Visitan v
    ON v.idSucursal = d.idSucursal
    AND v.idCliente = c.id;



