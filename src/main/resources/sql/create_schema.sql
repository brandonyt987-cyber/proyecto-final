-- ==========================================
-- 0. CONFIGURACIÓN INICIAL
-- ==========================================
CREATE DATABASE IF NOT EXISTS sistemaintegralsena;
USE sistemaintegralsena;

SET FOREIGN_KEY_CHECKS = 0;

-- BORRADO TOTAL (Limpieza)
DROP TABLE IF EXISTS comites;
DROP TABLE IF EXISTS atenciones;
DROP TABLE IF EXISTS talleres;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS aprendices;
DROP TABLE IF EXISTS instructores;
DROP TABLE IF EXISTS fichas;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS coordinaciones;
DROP TABLE IF EXISTS roles;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 1. TABLAS MAESTRAS (Sin Dependencias)
-- ==========================================

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50)
);

CREATE TABLE coordinaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    activo BIT(1) DEFAULT 1
);

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    rol VARCHAR(50), 
    enabled BIT(1) DEFAULT 1
);

-- ==========================================
-- 2. TABLAS DE SEGURIDAD
-- ==========================================

CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    fecha_expiracion DATETIME NOT NULL,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_token_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- ==========================================
-- 3. TABLAS NIVEL 1 (Dependen de Maestras)
-- ==========================================

CREATE TABLE fichas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(12) NOT NULL UNIQUE,
    programa VARCHAR(100) NOT NULL,
    jornada VARCHAR(50) NOT NULL,
    modalidad VARCHAR(50) NOT NULL,
    activo BIT(1) DEFAULT 1,
    coordinacion_id BIGINT NOT NULL,
    CONSTRAINT fk_fichas_coordinacion FOREIGN KEY (coordinacion_id) REFERENCES coordinaciones(id)
);

CREATE TABLE instructores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- Identificación
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(12) NOT NULL UNIQUE,
    -- Datos Personales
    nombres VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    profesion VARCHAR(70) NOT NULL,
    -- Contacto
    correo VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    -- Estado y Relación
    activo BIT(1) DEFAULT 1,
    coordinacion_id BIGINT NOT NULL,
    CONSTRAINT fk_instructores_coordinacion FOREIGN KEY (coordinacion_id) REFERENCES coordinaciones(id)
);

-- ==========================================
-- 4. TABLAS NIVEL 2 (Aprendices y Talleres)
-- ==========================================

CREATE TABLE aprendices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- Identificación
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    -- Datos Personales
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    -- Formación y Contacto
    etapa_formacion VARCHAR(50) NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    celular VARCHAR(15) NOT NULL,
    -- Estado
    es_vocero BIT(1) DEFAULT 0,
    activo BIT(1) DEFAULT 1,
    -- Relación
    ficha_id BIGINT NOT NULL,
    CONSTRAINT fk_aprendices_ficha FOREIGN KEY (ficha_id) REFERENCES fichas(id)
);

CREATE TABLE talleres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_taller VARCHAR(255) NOT NULL,
    cupo INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    activo BIT(1) DEFAULT 1,
    usuario_id BIGINT NOT NULL,
    ficha_id BIGINT NOT NULL,
    CONSTRAINT fk_talleres_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_talleres_ficha FOREIGN KEY (ficha_id) REFERENCES fichas(id)
);

-- ==========================================
-- 5. TABLAS DE GESTIÓN (Atenciones y Comités)
-- ==========================================

CREATE TABLE atenciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- Datos del Caso
    estado_caso VARCHAR(50) NOT NULL,
    categoria_desercion VARCHAR(100) NOT NULL,
    remitido_por VARCHAR(100) NOT NULL,
    atencion_familiar VARCHAR(255),
    fecha_creacion_registro DATETIME,
    
    -- Seguimientos (1, 2 y 3)
    fecha_consulta1 DATE,
    observaciones1 TEXT,
    fecha_consulta2 DATE,
    observaciones2 TEXT,
    fecha_consulta3 DATE,
    observaciones3 TEXT,
    
    activo BIT(1) DEFAULT 1,
    
    -- Relaciones
    aprendiz_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_atenciones_aprendiz FOREIGN KEY (aprendiz_id) REFERENCES aprendices(id),
    CONSTRAINT fk_atenciones_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE comites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Cita
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    
    -- Ubicación
    sede VARCHAR(50) NOT NULL,
    piso VARCHAR(50) NOT NULL,
    ambiente VARCHAR(50) NOT NULL,
    
    -- Detalles Falta
    tipo_falta VARCHAR(50) NOT NULL,
    motivo TEXT NOT NULL,
    
    -- Asistentes
    profesional_bienestar VARCHAR(255) NOT NULL,
    representante_aprendices VARCHAR(255) NOT NULL,
    
    -- Resultados
    recomendacion TEXT NOT NULL,
    plan_mejoramiento TEXT NOT NULL,
    fecha_plazo DATE NOT NULL,
    observaciones TEXT NOT NULL,
    paz_salvo BIT(1) NOT NULL,
    
    -- Auditoría (IMPORTANTE PARA REPORTES)
    fecha_creacion DATE,
    activo BIT(1) DEFAULT 1,
    
    -- Relaciones
    aprendiz_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    coordinacion_id BIGINT NOT NULL,
    
    CONSTRAINT fk_comites_aprendiz FOREIGN KEY (aprendiz_id) REFERENCES aprendices(id),
    CONSTRAINT fk_comites_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_comites_instructor FOREIGN KEY (instructor_id) REFERENCES instructores(id),
    CONSTRAINT fk_comites_coordinacion FOREIGN KEY (coordinacion_id) REFERENCES coordinaciones(id)
);

--Inserts por si los XD --
/*

USE sistemaintegralsena;

-- 1. COORDINACIONES
INSERT INTO coordinaciones (id, nombre, activo) VALUES 
(1, 'Teleinformática y Sistemas', 1),
(2, 'Gestión Administrativa', 1),
(3, 'Financiera y Contable', 1),
(4, 'Talento Humano', 1),
(5, 'Mercadeo y Logística', 1);

-- 2. INSTRUCTORES
INSERT INTO instructores (id, nombres, apellidos, tipo_documento, numero_documento, profesion, correo, telefono, coordinacion_id, activo) VALUES 
(1, 'Andrés Felipe', 'Ramírez Pérez', 'CC', '7000121', 'Ingeniero de Sistemas', 'instructor.ramirez@sena.edu.co', '3104528974', 1, 1),
(2, 'Claudia María', 'Ospina Giraldo', 'CC', '7000221', 'Administradora de Empresas', 'instructor.ospina@sena.edu.co', '3115671234', 2, 1),
(3, 'Jorge Enrique', 'Martínez López', 'CC', '7000321', 'Contador Público', 'instructor.martinez@sena.edu.co', '3009876543', 3, 1),
(4, 'Diana Marcela', 'Gutiérrez Vega', 'CC', '7000421', 'Psicóloga Organizacional', 'instructor.gutierrez@sena.edu.co', '3152345678', 4, 1),
(5, 'Ricardo José', 'Salazar Ríos', 'CC', '7000521', 'Especialista en Marketing', 'instructor.salazar@sena.edu.co', '3208765432', 5, 1);

-- 3. FICHAS
INSERT INTO fichas (id, codigo, programa, jornada, modalidad, coordinacion_id, activo) VALUES 
(1, '290001', 'Tecnólogo en Teleinformática', 'Diurna', 'Presencial', 1, 1),
(2, '290002', 'Tecnólogo en Gestión Administrativa', 'Mixta', 'Presencial', 2, 1),
(3, '290003', 'Tecnólogo en Gestión Contable', 'Nocturna', 'Virtual', 3, 1),
(4, '290004', 'Tecnólogo en Talento Humano', 'Diurna', 'Presencial', 4, 1),
(5, '290005', 'Tecnólogo en Logística', 'FDS', 'Distancia', 5, 1);

-- 4. APRENDICES
INSERT INTO aprendices (nombres, apellidos, tipo_documento, numero_documento, fecha_nacimiento, correo, celular, etapa_formacion, es_vocero, ficha_id, activo) VALUES 
-- Ficha 1
('Juan Camilo', 'Gómez Hernández', 'CC', '1001051', '2004-03-15', 'juan.gomez@sena.edu.co', '3124567890', 'Lectiva', 1, 1, 1),
('Valentina', 'Rojas Díaz', 'TI', '1001151', '2009-02-20', 'valentina.rojas@sena.edu.co', '3105678901', 'Lectiva', 0, 1, 1),
-- Ficha 2
('Daniela', 'Vargas Suárez', 'CC', '1002051', '2003-11-10', 'daniela.vargas@sena.edu.co', '3156789012', 'Productiva', 1, 2, 1),
('Mateo', 'López Pinto', 'CC', '1002151', '2005-06-05', 'mateo.lopez@sena.edu.co', '3201234567', 'Productiva', 0, 2, 1),
-- Ficha 3
('Samuel', 'Medina Herrera', 'CC', '1003051', '2002-01-30', 'samuel.medina@sena.edu.co', '3004567890', 'Lectiva', 1, 3, 1),
('Isabella', 'Jiménez Silva', 'TI', '1003151', '2008-08-12', 'isabella.jimenez@sena.edu.co', '3198765432', 'Lectiva', 0, 3, 1),
-- Ficha 4
('Laura Sofía', 'Aguilar Delgado', 'CC', '1004051', '2004-12-25', 'laura.aguilar@sena.edu.co', '3161239876', 'Lectiva', 1, 4, 1),
('Alejandro', 'Peña Rivas', 'CC', '1004151', '2001-07-14', 'alejandro.pena@sena.edu.co', '3187654321', 'Lectiva', 0, 4, 1),
-- Ficha 5
('Nicolás', 'Ortiz Valencia', 'CC', '1005051', '2000-09-09', 'nicolas.ortiz@sena.edu.co', '3176543210', 'Productiva', 1, 5, 1),
('Gabriela', 'Muñoz Restrepo', 'CC', '1005151', '2003-04-18', 'gabriela.munoz@sena.edu.co', '3112345678', 'Productiva', 0, 5, 1);

*/