-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: inventails
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `animales`
--

DROP TABLE IF EXISTS `animales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `animales` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) DEFAULT NULL,
  `tipo_animal` varchar(20) DEFAULT NULL,
  `Genero` varchar(10) DEFAULT NULL,
  `ubicacion` varchar(30) DEFAULT NULL,
  `estado` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `estado` (`estado`),
  CONSTRAINT `animales_ibfk_1` FOREIGN KEY (`estado`) REFERENCES `estados_animales` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animales`
--

LOCK TABLES `animales` WRITE;
/*!40000 ALTER TABLE `animales` DISABLE KEYS */;
INSERT INTO `animales` VALUES (1,'CER183','Cerdos','Macho','chiquero espacio: 21',2),(2,'BOV362','Bovinos','Macho','establo espacio: 1',1),(3,'GAL594','Gallinas','Macho','gallinero espacio: 7',1);
/*!40000 ALTER TABLE `animales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencia`
--

DROP TABLE IF EXISTS `asistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `asistencia` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `ID_usuario` int(10) DEFAULT NULL,
  `Accion` varchar(30) DEFAULT NULL,
  `FechaAccion` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `ID_usuario` (`ID_usuario`),
  CONSTRAINT `asistencia_ibfk_1` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencia`
--

LOCK TABLES `asistencia` WRITE;
/*!40000 ALTER TABLE `asistencia` DISABLE KEYS */;
INSERT INTO `asistencia` VALUES (1,1,'Autenticacion','2025-09-05 00:15:50'),(2,1,'Desconexion','2025-09-05 00:23:47'),(3,1,'Autenticacion','2025-09-05 00:29:22'),(4,1,'Desconexion','2025-09-05 01:15:18'),(5,1,'Autenticacion','2025-09-10 00:49:37'),(6,1,'Desconexion','2025-09-10 01:19:47'),(7,1,'Autenticacion','2025-09-12 20:35:19'),(8,1,'Desconexion','2025-09-12 20:36:05'),(9,1,'Autenticacion','2025-09-12 20:36:22'),(10,1,'Desconexion','2025-09-12 20:44:10'),(11,1,'Autenticacion','2025-09-12 20:44:23'),(12,1,'Desconexion','2025-09-12 20:44:49'),(13,1,'Autenticacion','2025-09-21 21:54:24'),(14,1,'Autenticacion','2025-09-21 21:58:02'),(15,1,'Desconexion','2025-09-21 22:05:57'),(16,1,'Autenticacion','2025-09-21 22:06:08'),(17,1,'Desconexion','2025-09-21 22:07:46'),(18,1,'Autenticacion','2025-09-21 22:07:55'),(19,2,'Autenticacion','2025-09-21 22:09:01'),(20,2,'Desconexion','2025-09-21 22:09:08'),(21,1,'Autenticacion','2025-09-25 20:20:51'),(22,1,'Desconexion','2025-09-25 23:11:02'),(23,1,'Autenticacion','2025-10-01 22:18:32'),(24,1,'Autenticacion','2025-10-02 00:28:33'),(25,2,'Autenticacion','2025-10-02 00:29:35'),(26,1,'Autenticacion','2025-10-02 00:30:28'),(27,2,'Autenticacion','2025-10-02 00:30:49'),(28,1,'Autenticacion','2025-10-02 00:32:24'),(29,1,'Autenticacion','2025-10-02 01:09:58'),(30,1,'Desconexion','2025-10-02 01:10:03'),(31,1,'Autenticacion','2025-10-02 01:10:12'),(32,1,'Autenticacion','2025-10-06 22:57:24'),(33,1,'Desconexion','2025-10-06 23:07:50');
/*!40000 ALTER TABLE `asistencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compras`
--

DROP TABLE IF EXISTS `compras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `compras` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_compra` timestamp NOT NULL DEFAULT current_timestamp(),
  `v_compra` decimal(20,2) NOT NULL,
  `Edad_inicial` varchar(20) DEFAULT NULL,
  `Peso_inicial_kg` decimal(10,2) NOT NULL,
  `ID_animal` int(10) DEFAULT NULL,
  `ID_usuario` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ID_usuario` (`ID_usuario`),
  KEY `ID_animal` (`ID_animal`),
  CONSTRAINT `compras_ibfk_1` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `compras_ibfk_2` FOREIGN KEY (`ID_animal`) REFERENCES `animales` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compras`
--

LOCK TABLES `compras` WRITE;
/*!40000 ALTER TABLE `compras` DISABLE KEYS */;
INSERT INTO `compras` VALUES (1,'2025-09-05 00:29:27',-200000.00,'Ternero',12.00,2,1),(2,'2025-10-06 23:04:03',-50000.00,'Pollito',2.00,3,1);
/*!40000 ALTER TABLE `compras` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creacionusuarios`
--

DROP TABLE IF EXISTS `creacionusuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `creacionusuarios` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `ID_usuario` int(10) DEFAULT NULL,
  `ID_CreadoPor` int(10) DEFAULT NULL,
  `FechaCreacion` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `ID_usuario` (`ID_usuario`),
  KEY `ID_CreadoPor` (`ID_CreadoPor`),
  CONSTRAINT `creacionusuarios_ibfk_1` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `creacionusuarios_ibfk_2` FOREIGN KEY (`ID_CreadoPor`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creacionusuarios`
--

LOCK TABLES `creacionusuarios` WRITE;
/*!40000 ALTER TABLE `creacionusuarios` DISABLE KEYS */;
INSERT INTO `creacionusuarios` VALUES (1,2,1,'2025-09-21 22:07:20');
/*!40000 ALTER TABLE `creacionusuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `defunciones`
--

DROP TABLE IF EXISTS `defunciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `defunciones` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_defuncion` timestamp NOT NULL DEFAULT current_timestamp(),
  `r_defuncion` varchar(50) DEFAULT NULL,
  `ID_animal` int(10) DEFAULT NULL,
  `ID_usuario` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ID_usuario` (`ID_usuario`),
  KEY `ID_animal` (`ID_animal`),
  CONSTRAINT `defunciones_ibfk_1` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `defunciones_ibfk_2` FOREIGN KEY (`ID_animal`) REFERENCES `animales` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `defunciones`
--

LOCK TABLES `defunciones` WRITE;
/*!40000 ALTER TABLE `defunciones` DISABLE KEYS */;
INSERT INTO `defunciones` VALUES (1,'2025-09-12 20:36:38','RABIA',NULL,1);
/*!40000 ALTER TABLE `defunciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `descargareportes`
--

DROP TABLE IF EXISTS `descargareportes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `descargareportes` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `Responsable` int(10) DEFAULT NULL,
  `ReporteDescargado` varchar(30) DEFAULT NULL,
  `FechaDescarga` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `Responsable` (`Responsable`),
  CONSTRAINT `descargareportes_ibfk_1` FOREIGN KEY (`Responsable`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `descargareportes`
--

LOCK TABLES `descargareportes` WRITE;
/*!40000 ALTER TABLE `descargareportes` DISABLE KEYS */;
/*!40000 ALTER TABLE `descargareportes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estados_animales`
--

DROP TABLE IF EXISTS `estados_animales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `estados_animales` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `Estado` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estados_animales`
--

LOCK TABLES `estados_animales` WRITE;
/*!40000 ALTER TABLE `estados_animales` DISABLE KEYS */;
INSERT INTO `estados_animales` VALUES (1,'Activo'),(2,'Inactivo');
/*!40000 ALTER TABLE `estados_animales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gastosmantenimiento`
--

DROP TABLE IF EXISTS `gastosmantenimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gastosmantenimiento` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `RazondeGasto` varchar(50) DEFAULT NULL,
  `ValorGasto` decimal(20,2) NOT NULL,
  `ID_usuario` int(10) DEFAULT NULL,
  `FechaGasto` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `ID_usuario` (`ID_usuario`),
  CONSTRAINT `gastosmantenimiento_ibfk_1` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gastosmantenimiento`
--

LOCK TABLES `gastosmantenimiento` WRITE;
/*!40000 ALTER TABLE `gastosmantenimiento` DISABLE KEYS */;
INSERT INTO `gastosmantenimiento` VALUES (1,'pago de agua',-1200000.00,1,'2025-09-05 00:37:13');
/*!40000 ALTER TABLE `gastosmantenimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicinasaplicadas`
--

DROP TABLE IF EXISTS `medicinasaplicadas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medicinasaplicadas` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `ID_animal` int(10) DEFAULT NULL,
  `MedicamentoAplicado` varchar(100) DEFAULT NULL,
  `ViaAplicacion` varchar(50) DEFAULT NULL,
  `Concentracion` varchar(50) DEFAULT NULL,
  `FechaAplicacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `ID_usuario` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ID_usuario` (`ID_usuario`),
  KEY `ID_animal` (`ID_animal`),
  CONSTRAINT `medicinasaplicadas_ibfk_1` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `medicinasaplicadas_ibfk_2` FOREIGN KEY (`ID_animal`) REFERENCES `animales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicinasaplicadas`
--

LOCK TABLES `medicinasaplicadas` WRITE;
/*!40000 ALTER TABLE `medicinasaplicadas` DISABLE KEYS */;
/*!40000 ALTER TABLE `medicinasaplicadas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modificacionacceso`
--

DROP TABLE IF EXISTS `modificacionacceso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `modificacionacceso` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `ID_UsuarioAfectado` int(10) DEFAULT NULL,
  `ID_ModificadoPor` int(10) DEFAULT NULL,
  `NivelAccesoPrevio` int(10) DEFAULT NULL,
  `NivelAccesoNuevo` int(10) DEFAULT NULL,
  `FechaModificacion` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `ID_UsuarioAfectado` (`ID_UsuarioAfectado`),
  KEY `NivelAccesoPrevio` (`NivelAccesoPrevio`),
  KEY `NivelAccesoNuevo` (`NivelAccesoNuevo`),
  CONSTRAINT `modificacionacceso_ibfk_1` FOREIGN KEY (`ID_UsuarioAfectado`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `modificacionacceso_ibfk_2` FOREIGN KEY (`NivelAccesoPrevio`) REFERENCES `roles` (`id`),
  CONSTRAINT `modificacionacceso_ibfk_3` FOREIGN KEY (`NivelAccesoNuevo`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `modificacionacceso`
--

LOCK TABLES `modificacionacceso` WRITE;
/*!40000 ALTER TABLE `modificacionacceso` DISABLE KEYS */;
INSERT INTO `modificacionacceso` VALUES (1,2,1,1,2,'2025-09-21 22:08:05'),(2,2,1,2,1,'2025-10-02 00:29:21'),(3,2,1,1,3,'2025-10-02 00:30:36');
/*!40000 ALTER TABLE `modificacionacceso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedigree`
--

DROP TABLE IF EXISTS `pedigree`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedigree` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `RazaAnimal` varchar(50) DEFAULT NULL,
  `PropositoAnimal` varchar(50) DEFAULT NULL,
  `RazaPadre` varchar(50) DEFAULT NULL,
  `RazaMadre` varchar(50) DEFAULT NULL,
  `ID_animal` int(10) DEFAULT NULL,
  `ID_usuario` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ID_usuario` (`ID_usuario`),
  KEY `ID_animal` (`ID_animal`),
  CONSTRAINT `pedigree_ibfk_1` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `pedigree_ibfk_2` FOREIGN KEY (`ID_animal`) REFERENCES `animales` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedigree`
--

LOCK TABLES `pedigree` WRITE;
/*!40000 ALTER TABLE `pedigree` DISABLE KEYS */;
INSERT INTO `pedigree` VALUES (1,'Gilt','Comercializacion','Gilt','Large White',1,1),(2,'Cebu Brahman','Produccion de carne','Holstein','Cebu Brahman',2,1),(3,'Rhode island red','Produccion de pollo','Lohmann Brown','Orpington',3,1);
/*!40000 ALTER TABLE `pedigree` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registroenfermedades`
--

DROP TABLE IF EXISTS `registroenfermedades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registroenfermedades` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `F_Diagnostico` timestamp NOT NULL DEFAULT current_timestamp(),
  `enfermedad` varchar(50) NOT NULL,
  `veterinario` varchar(30) NOT NULL,
  `comentario` varchar(50) DEFAULT NULL,
  `ID_animal` int(10) DEFAULT NULL,
  `ID_usuario` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ID_usuario` (`ID_usuario`),
  KEY `ID_animal` (`ID_animal`),
  CONSTRAINT `registroenfermedades_ibfk_1` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `registroenfermedades_ibfk_2` FOREIGN KEY (`ID_animal`) REFERENCES `animales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registroenfermedades`
--

LOCK TABLES `registroenfermedades` WRITE;
/*!40000 ALTER TABLE `registroenfermedades` DISABLE KEYS */;
/*!40000 ALTER TABLE `registroenfermedades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `Rol` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Administrador'),(2,'Gestor'),(3,'Bloqueado');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `saldo`
--

DROP TABLE IF EXISTS `saldo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saldo` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `saldo` decimal(20,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `saldo`
--

LOCK TABLES `saldo` WRITE;
/*!40000 ALTER TABLE `saldo` DISABLE KEYS */;
INSERT INTO `saldo` VALUES (1,-970000.00);
/*!40000 ALTER TABLE `saldo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transacciones`
--

DROP TABLE IF EXISTS `transacciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transacciones` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `TIPO_TRANSACCION` varchar(20) DEFAULT NULL,
  `id_compra` int(10) DEFAULT NULL,
  `id_venta` int(10) DEFAULT NULL,
  `id_GastoMantenimiento` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_compra` (`id_compra`),
  KEY `id_venta` (`id_venta`),
  KEY `id_GastoMantenimiento` (`id_GastoMantenimiento`),
  CONSTRAINT `transacciones_ibfk_1` FOREIGN KEY (`id_compra`) REFERENCES `compras` (`id`),
  CONSTRAINT `transacciones_ibfk_2` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id`),
  CONSTRAINT `transacciones_ibfk_3` FOREIGN KEY (`id_GastoMantenimiento`) REFERENCES `gastosmantenimiento` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transacciones`
--

LOCK TABLES `transacciones` WRITE;
/*!40000 ALTER TABLE `transacciones` DISABLE KEYS */;
INSERT INTO `transacciones` VALUES (1,'COMPRA',1,NULL,NULL),(2,'GASTO_MANTENIMIENTO',NULL,NULL,1),(3,'COMPRA',2,NULL,NULL);
/*!40000 ALTER TABLE `transacciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuarios` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `usuario` varchar(50) DEFAULT NULL,
  `Correo_Electronico` varchar(60) DEFAULT NULL,
  `VerificacionCorreo` varchar(6) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `NivelPermiso` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `NivelPermiso` (`NivelPermiso`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`NivelPermiso`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Jmamaya','amayaalvarezjuanmanuel@gmail.com','true','$2a$12$Q8iMGCKWDirGzf4Ukv3nLeL7Xxy8b.GZH4W3KCDTM6ENUAPGdTaou',1),(2,'Manuel27','anonitus56135@gmail.com','true','$2a$12$vs/Er9Ks41ldZjAiFE55PuNF7mCTUVH0.Iqx1pIKQznejbC8onuQW',3);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ventas`
--

DROP TABLE IF EXISTS `ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ventas` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_venta` timestamp NOT NULL DEFAULT current_timestamp(),
  `v_venta` decimal(20,2) NOT NULL,
  `Peso_final_kg` decimal(10,2) NOT NULL,
  `ID_animal` int(10) DEFAULT NULL,
  `ID_usuario` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ID_usuario` (`ID_usuario`),
  KEY `ID_animal` (`ID_animal`),
  CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`ID_usuario`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `ventas_ibfk_2` FOREIGN KEY (`ID_animal`) REFERENCES `animales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ventas`
--

LOCK TABLES `ventas` WRITE;
/*!40000 ALTER TABLE `ventas` DISABLE KEYS */;
/*!40000 ALTER TABLE `ventas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-07 10:06:00
