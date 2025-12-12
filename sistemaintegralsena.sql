-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: sistemaintegralsena
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `aprendices`
--

DROP TABLE IF EXISTS `aprendices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aprendices` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tipo_documento` varchar(20) NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `etapa_formacion` varchar(50) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `celular` varchar(15) NOT NULL,
  `es_vocero` bit(1) DEFAULT b'0',
  `activo` bit(1) DEFAULT b'1',
  `ficha_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `correo` (`correo`),
  KEY `fk_aprendices_ficha` (`ficha_id`),
  CONSTRAINT `fk_aprendices_ficha` FOREIGN KEY (`ficha_id`) REFERENCES `fichas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aprendices`
--

LOCK TABLES `aprendices` WRITE;
/*!40000 ALTER TABLE `aprendices` DISABLE KEYS */;
INSERT INTO `aprendices` VALUES (1,'CC','1001051','Juan Camilo','Gómez Hernández','2004-03-15','Lectiva','juan.gomez@sena.edu.co','3124567890',_binary '',_binary '',1),(2,'TI','1001151','Valentina','Rojas Díaz','2009-02-20','Lectiva','valentina.rojas@sena.edu.co','3105678901',_binary '\0',_binary '',1),(3,'CC','1002051','Daniela','Vargas Suárez','2003-11-10','Productiva','daniela.vargas@sena.edu.co','3156789012',_binary '',_binary '',2),(4,'CC','1002151','Mateo','López Pinto','2005-06-05','Productiva','mateo.lopez@sena.edu.co','3201234567',_binary '\0',_binary '',2),(5,'CC','1003051','Samuel','Medina Herrera','2002-01-30','Lectiva','samuel.medina@sena.edu.co','3004567890',_binary '',_binary '',3),(6,'TI','1003151','Isabella','Jiménez Silva','2008-08-12','Lectiva','isabella.jimenez@sena.edu.co','3198765432',_binary '\0',_binary '',3),(7,'CC','1004051','Laura Sofía','Aguilar Delgado','2004-12-25','Lectiva','laura.aguilar@sena.edu.co','3161239876',_binary '',_binary '',4),(8,'CC','1004151','Alejandro','Peña Rivas','2001-07-14','Lectiva','alejandro.pena@sena.edu.co','3187654321',_binary '\0',_binary '',4),(9,'CC','1005051','Nicolás','Ortiz Valencia','2000-09-09','Productiva','nicolas.ortiz@sena.edu.co','3176543210',_binary '',_binary '',5),(10,'CC','1005151','Gabriela','Muñoz Restrepo','2003-04-18','Productiva','gabriela.munoz@sena.edu.co','3112345678',_binary '\0',_binary '',5);
/*!40000 ALTER TABLE `aprendices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `atenciones`
--

DROP TABLE IF EXISTS `atenciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `atenciones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `estado_caso` varchar(50) NOT NULL,
  `categoria_desercion` varchar(100) NOT NULL,
  `remitido_por` varchar(100) NOT NULL,
  `atencion_familiar` varchar(255) DEFAULT NULL,
  `fecha_creacion_registro` datetime DEFAULT NULL,
  `fecha_consulta1` date DEFAULT NULL,
  `observaciones1` text DEFAULT NULL,
  `fecha_consulta2` date DEFAULT NULL,
  `observaciones2` text DEFAULT NULL,
  `fecha_consulta3` date DEFAULT NULL,
  `observaciones3` text DEFAULT NULL,
  `activo` bit(1) DEFAULT b'1',
  `aprendiz_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_atenciones_aprendiz` (`aprendiz_id`),
  KEY `fk_atenciones_usuario` (`usuario_id`),
  CONSTRAINT `fk_atenciones_aprendiz` FOREIGN KEY (`aprendiz_id`) REFERENCES `aprendices` (`id`),
  CONSTRAINT `fk_atenciones_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `atenciones`
--

LOCK TABLES `atenciones` WRITE;
/*!40000 ALTER TABLE `atenciones` DISABLE KEYS */;
INSERT INTO `atenciones` VALUES (1,'Abierto','Problemas de Salud','Instructor',NULL,'2025-12-06 17:17:49','2025-12-09','Primer ingreso se reciben documentos medicos','2025-12-12','Segundo analisis',NULL,'',_binary '',1,2),(2,'En Seguimiento','Riesgo Económico','Instructor',NULL,'2025-12-06 17:21:53','2025-12-16','Primer revision',NULL,'',NULL,'',_binary '',8,3),(3,'Cerrado','Riesgo Académico','Instructor',NULL,'2025-12-06 17:31:55','2025-12-09','Aprendiz no desea continuar con el curso, se retira voluntariamente.',NULL,'',NULL,'',_binary '',10,3);
/*!40000 ALTER TABLE `atenciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comites`
--

DROP TABLE IF EXISTS `comites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comites` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `hora` time NOT NULL,
  `sede` varchar(50) NOT NULL,
  `piso` varchar(50) NOT NULL,
  `ambiente` varchar(50) NOT NULL,
  `tipo_falta` varchar(50) NOT NULL,
  `motivo` text NOT NULL,
  `profesional_bienestar` varchar(255) NOT NULL,
  `representante_aprendices` varchar(255) NOT NULL,
  `recomendacion` text NOT NULL,
  `plan_mejoramiento` text NOT NULL,
  `fecha_plazo` date NOT NULL,
  `observaciones` text NOT NULL,
  `paz_salvo` bit(1) NOT NULL,
  `fecha_creacion` date DEFAULT NULL,
  `activo` bit(1) DEFAULT b'1',
  `aprendiz_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `instructor_id` bigint(20) NOT NULL,
  `coordinacion_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_comites_aprendiz` (`aprendiz_id`),
  KEY `fk_comites_usuario` (`usuario_id`),
  KEY `fk_comites_instructor` (`instructor_id`),
  KEY `fk_comites_coordinacion` (`coordinacion_id`),
  CONSTRAINT `fk_comites_aprendiz` FOREIGN KEY (`aprendiz_id`) REFERENCES `aprendices` (`id`),
  CONSTRAINT `fk_comites_coordinacion` FOREIGN KEY (`coordinacion_id`) REFERENCES `coordinaciones` (`id`),
  CONSTRAINT `fk_comites_instructor` FOREIGN KEY (`instructor_id`) REFERENCES `instructores` (`id`),
  CONSTRAINT `fk_comites_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- TRIGGER para bloquear edicion de fecha_creacion
--
DELIMITER ;;
/*!50003 CREATE TRIGGER `proteger_fecha_creacion_comites` BEFORE UPDATE ON `comites` FOR EACH ROW
BEGIN
    SET NEW.fecha_creacion = OLD.fecha_creacion;
END */;;
DELIMITER ;
--
-- Fin del Trigger
--

--
-- Dumping data for table `comites`
--

LOCK TABLES `comites` WRITE;
/*!40000 ALTER TABLE `comites` DISABLE KEYS */;
INSERT INTO `comites` VALUES (1,'2025-12-09','12:32:00','CIDE Sur','Piso 4','301','Académica','Aprendiz no cumple talleres del curso','Laura Gómez','Nicolás Ortiz Valencia','N/a','N/a','2025-12-17','N/a',_binary '\0','2025-12-06',_binary '',10,1,1,5),(2,'2025-12-19','14:33:00','CIDE Sur','Piso 4','302','Disciplinaria','Aprendiz con falta disciplinaria.','María Torres','Laura Sofía Aguilar Delgado','N/a','N/a','2025-12-17','N/a',_binary '','2025-12-06',_binary '',8,1,4,4);
/*!40000 ALTER TABLE `comites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coordinaciones`
--

DROP TABLE IF EXISTS `coordinaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coordinaciones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `activo` bit(1) DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coordinaciones`
--

LOCK TABLES `coordinaciones` WRITE;
/*!40000 ALTER TABLE `coordinaciones` DISABLE KEYS */;
INSERT INTO `coordinaciones` VALUES (1,'Teleinformática y Sistemas',_binary ''),(2,'Gestión Administrativa',_binary ''),(3,'Financiera y Contable',_binary ''),(4,'Talento Humano',_binary ''),(5,'Mercadeo y Logística',_binary '');
/*!40000 ALTER TABLE `coordinaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fichas`
--

DROP TABLE IF EXISTS `fichas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fichas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(12) NOT NULL,
  `programa` varchar(100) NOT NULL,
  `jornada` varchar(50) NOT NULL,
  `modalidad` varchar(50) NOT NULL,
  `activo` bit(1) DEFAULT b'1',
  `coordinacion_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `fk_fichas_coordinacion` (`coordinacion_id`),
  CONSTRAINT `fk_fichas_coordinacion` FOREIGN KEY (`coordinacion_id`) REFERENCES `coordinaciones` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fichas`
--

LOCK TABLES `fichas` WRITE;
/*!40000 ALTER TABLE `fichas` DISABLE KEYS */;
INSERT INTO `fichas` VALUES (1,'290001','Tecnólogo en Teleinformática','Diurna','Presencial',_binary '',1),(2,'290002','Tecnólogo en Gestión Administrativa','Mixta','Presencial',_binary '',2),(3,'290003','Tecnólogo en Gestión Contable','Nocturna','Virtual',_binary '',3),(4,'290004','Tecnólogo en Talento Humano','Diurna','Presencial',_binary '',4),(5,'290005','Tecnólogo en Logística','FDS','Distancia',_binary '',5);
/*!40000 ALTER TABLE `fichas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instructores`
--

DROP TABLE IF EXISTS `instructores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `instructores` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tipo_documento` varchar(20) NOT NULL,
  `numero_documento` varchar(12) NOT NULL,
  `nombres` varchar(50) NOT NULL,
  `apellidos` varchar(50) NOT NULL,
  `profesion` varchar(70) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `telefono` varchar(15) NOT NULL,
  `activo` bit(1) DEFAULT b'1',
  `coordinacion_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `correo` (`correo`),
  KEY `fk_instructores_coordinacion` (`coordinacion_id`),
  CONSTRAINT `fk_instructores_coordinacion` FOREIGN KEY (`coordinacion_id`) REFERENCES `coordinaciones` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instructores`
--

LOCK TABLES `instructores` WRITE;
/*!40000 ALTER TABLE `instructores` DISABLE KEYS */;
INSERT INTO `instructores` VALUES (1,'CC','7000121','Andrés Felipe','Ramírez Pérez','Ingeniero de Sistemas','instructor.ramirez@sena.edu.co','3104528974',_binary '',1),(2,'CC','7000221','Claudia María','Ospina Giraldo','Administradora de Empresas','instructor.ospina@sena.edu.co','3115671234',_binary '',2),(3,'CC','7000321','Jorge Enrique','Martínez López','Contador Público','instructor.martinez@sena.edu.co','3009876543',_binary '',3),(4,'CC','7000421','Diana Marcela','Gutiérrez Vega','Psicóloga Organizacional','instructor.gutierrez@sena.edu.co','3152345678',_binary '',4),(5,'CC','7000521','Ricardo José','Salazar Ríos','Especialista en Marketing','instructor.salazar@sena.edu.co','3208765432',_binary '',5);
/*!40000 ALTER TABLE `instructores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  `fecha_expiracion` datetime NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_token_usuario` (`usuario_id`),
  CONSTRAINT `fk_token_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMIN'),(2,'PSICOLOGA'),(3,'T_SOCIAL');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `talleres`
--

DROP TABLE IF EXISTS `talleres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `talleres` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre_taller` varchar(255) NOT NULL,
  `cupo` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `activo` bit(1) DEFAULT b'1',
  `usuario_id` bigint(20) NOT NULL,
  `ficha_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_talleres_usuario` (`usuario_id`),
  KEY `fk_talleres_ficha` (`ficha_id`),
  CONSTRAINT `fk_talleres_ficha` FOREIGN KEY (`ficha_id`) REFERENCES `fichas` (`id`),
  CONSTRAINT `fk_talleres_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `talleres`
--

LOCK TABLES `talleres` WRITE;
/*!40000 ALTER TABLE `talleres` DISABLE KEYS */;
/*!40000 ALTER TABLE `talleres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `rol` varchar(50) DEFAULT NULL,
  `enabled` bit(1) DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Administrador Principal','v64149378@gmail.com','$2a$10$RN3ilYlnAO1GAO2bA/A40eFDTZ/qf2HvSK0lx57DrDdwDyE9T8Hsa','ADMIN',_binary ''),(2,'Laura Gómez','psico@sena.edu.co','$2a$10$NRnXr2nUBXfXOElLT5O6d.sk2zYJnrm/HdyVAN4iQV1LYoPrMMSHW','PSICOLOGA',_binary ''),(3,'María Torres','social@sena.edu.co','$2a$10$knJsplLFliOyTGSf543cIug4DgzUGL9adLzXi/EZeuzWKcB.n/yl.','T_SOCIAL',_binary '');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-06 13:15:26