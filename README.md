# 🏥 Centro de Salud Universitario – Sistema ASIS

Sistema de Análisis de Situación Integral de Salud (ASIS) desarrollado en el marco de la materia **Java y Aplicaciones Avanzadas sobre Internet (UNLP – 2025)**.

El sistema permite gestionar, procesar y analizar información proveniente de encuestas socio–económicas y sanitarias con el objetivo de apoyar la planificación de intervenciones en el territorio.

---

## 🎯 Objetivo

Brindar una herramienta informática que permita:

- Centralizar datos recolectados en campañas sanitarias.
- Analizar información mediante filtros personalizados.
- Visualizar datos geolocalizados.
- Importar reportes para la toma de decisiones.
- Facilitar el diagnóstico territorial en salud.

El proyecto se vincula con la implementación del **Centro de Salud Universitario (CSU)** en el barrio Villa Argüello (Berisso).

---

## 🧩 Funcionalidades Principales

- Gestión de usuarios y autenticación.
- Administración de campañas y jornadas.
- Importación de encuestas desde archivos CSV.
- Visualización de datos filtrados.
- Importación de reportes.
- Visualización territorial de información sanitaria.
- Solicitud de reportes entre perfiles.

---

## 👥 Perfiles del Sistema

- **Administrador**: gestión de usuarios, campañas, barrios y encuestadores.
- **Personal de Salud**: análisis de datos, visualización territorial e importación de reportes.
- **Referente de Organización Social**: solicitud de reportes.
- **Visitante**: acceso público y registro.

---

## 🛠 Tecnologías Utilizadas

### Backend
- Java
- Maven
- API REST / Servlets
- SQL

### Frontend
- Angular
- TypeScript
- SCSS

### Infraestructura
- Docker
- Docker Compose

---

## 🗂 Estructura del Proyecto

CSU_System/            → Backend (Java)  
csu-system-frontend/   → Frontend (Angular)  
Dockerfile  
docker-compose.yml  

---

## 🚀 Ejecución (Docker)

Para levantar el sistema:

docker-compose up --build

---

## 📚 Contexto Académico

Compañero: Juan Ignacio Torres

Trabajo final correspondiente a la cursada 2025 de  
**Java y Aplicaciones Avanzadas sobre Internet – UNLP**
