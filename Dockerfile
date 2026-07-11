# Etapa 1: Compilación (Build)
FROM maven:3.9.5-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Copiamos solo los archivos necesarios
COPY pom.xml .
COPY src ./src
# Compilamos saltando los tests para mayor velocidad
RUN mvn clean package -DskipTests

# Etapa 2: Producción (Ejecución Segura)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 1. SEGURIDAD: Crear un usuario y grupo sin privilegios (non-root)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 2. Copiar solo el ejecutable (.jar) de la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# 3. Asignar los permisos del archivo al nuevo usuario seguro
RUN chown appuser:appgroup app.jar

# 4. Cambiar al usuario seguro (Todo lo que siga se ejecuta sin permisos de administrador)
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]