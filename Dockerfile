FROM sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.2_13_1.9.9_3.3.3 AS builder
WORKDIR /app
COPY . .
RUN sbt stage

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/universal/stage /app

CMD ["sh", "-c", "bin/tic-tac-toe -Dhttp.port=${PORT:-9000} -Dplay.http.secret.key=${APPLICATION_SECRET} -J-Xmx320m -J-Xms128m -J-XX:+UseSerialGC"]
