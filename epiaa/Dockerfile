# 1단계: 빌드용 Maven + JDK 이미지
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 의존성 캐시를 위해 먼저 pom만 복사
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# 나머지 소스 복사 후 빌드
COPY src ./src
RUN mvn -B -DskipTests clean package

# 2단계: 실행용 JRE 이미지
FROM eclipse-temurin:17-jre
WORKDIR /app

# 위에서 빌드된 jar 파일만 가져오기
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]