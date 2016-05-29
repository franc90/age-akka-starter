import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

def bySecond = timestamp("yyyyMMdd'T'HHmmss")

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{40} - %msg%n'
    }
}

root(DEBUG, ["CONSOLE"])
logger("org.age.akka", TRACE)
logger("org.springframework", INFO)
logger("com.hazelcast", INFO)
