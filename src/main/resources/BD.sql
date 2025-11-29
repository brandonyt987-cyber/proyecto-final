-- ==========================================
-- 0. CONFIGURACIÓN DEL ENTORNO
-- ==========================================
-- Crea la base de datos si no existe
CREATE DATABASE IF NOT EXISTS sistemaintegralsena;

-- Selecciona la base de datos para usarla en los siguientes comandos
USE sistemaintegralsena;

-- ==========================================
-- 1. LIMPIEZA TOTAL (Reseteo de la BD)
-- ==========================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS comites;
DROP TABLE IF EXISTS atenciones;
DROP TABLE IF EXISTS voceros;
DROP TABLE IF EXISTS talleres;
DROP TABLE IF EXISTS aprendices;
DROP TABLE IF EXISTS instructores;
DROP TABLE IF EXISTS fichas;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS coordinaciones;
DROP TABLE IF EXISTS roles;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 2. TABLAS INDEPENDIENTES (Sin FKs)
-- ==========================================

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE coordinaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    rol VARCHAR(50), 
    enabled BIT(1) DEFAULT 1
);

-- ==========================================
-- 3. TABLAS DE NIVEL 1 (Dependen de las anteriores)
-- ==========================================

CREATE TABLE fichas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(12) NOT NULL UNIQUE,
    programa VARCHAR(100) NOT NULL,
    jornada VARCHAR(50) NOT NULL,
    modalidad VARCHAR(50) NOT NULL,
    coordinacion_id BIGINT NOT NULL,
    CONSTRAINT fk_fichas_coordinacion FOREIGN KEY (coordinacion_id) REFERENCES coordinaciones(id)
);

CREATE TABLE instructores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(50) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    profesion VARCHAR(100) NOT NULL,
    correo VARCHAR(255) UNIQUE NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    coordinacion_id BIGINT NOT NULL,
    CONSTRAINT fk_instructores_coordinacion FOREIGN KEY (coordinacion_id) REFERENCES coordinaciones(id)
);

-- ==========================================
-- 4. TABLAS DE NIVEL 2 (Dependen de Fichas/Instructores)
-- ==========================================

CREATE TABLE aprendices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(50) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    etapa_formacion VARCHAR(50) NOT NULL,
    correo VARCHAR(255) UNIQUE NOT NULL,
    celular VARCHAR(20) NOT NULL,
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
    usuario_id BIGINT NOT NULL, 
    ficha_id BIGINT NOT NULL,
    CONSTRAINT fk_talleres_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_talleres_ficha FOREIGN KEY (ficha_id) REFERENCES fichas(id)
);

-- ==========================================
-- 5. TABLAS DE NIVEL 3 (Detalles específicos)
-- ==========================================

CREATE TABLE voceros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    razon_cambio TEXT,
    aprendiz_id BIGINT NOT NULL UNIQUE, 
    CONSTRAINT fk_voceros_aprendiz FOREIGN KEY (aprendiz_id) REFERENCES aprendices(id)
);

CREATE TABLE atenciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estado_caso VARCHAR(50) NOT NULL,
    categoria_desercion VARCHAR(100) NOT NULL,
    remitido_por VARCHAR(100) NOT NULL,
    atencion_familiar VARCHAR(255),
    fecha_creacion_registro DATETIME,
    
    -- Seguimientos
    fecha_consulta1 DATE,
    observaciones1 TEXT,
    fecha_consulta2 DATE,
    observaciones2 TEXT,
    fecha_consulta3 DATE,
    observaciones3 TEXT,
    
    -- FKs
    aprendiz_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_atenciones_aprendiz FOREIGN KEY (aprendiz_id) REFERENCES aprendices(id),
    CONSTRAINT fk_atenciones_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE comites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Datos Cita
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    enlace VARCHAR(255) NOT NULL,
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
    
    -- FKs
    aprendiz_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    coordinacion_id BIGINT NOT NULL,
    
    CONSTRAINT fk_comites_aprendiz FOREIGN KEY (aprendiz_id) REFERENCES aprendices(id),
    CONSTRAINT fk_comites_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_comites_instructor FOREIGN KEY (instructor_id) REFERENCES instructores(id),
    CONSTRAINT fk_comites_coordinacion FOREIGN KEY (coordinacion_id) REFERENCES coordinaciones(id)
);