import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

def bySecond = timestamp("yyyyMMdd'T'HHmmss")

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{40} - %msg%n'
    }
}

appender("TEST_RESULTS", FileAppender) {
    file = "results/result-${bySecond}.txt"
    encoder(PatternLayoutEncoder) {
        pattern = '%msg%n'
    }
    filter(ThresholdFilter) {
        level = WARN
    }
}


root(DEBUG, ["CONSOLE"])
logger("org.age.akka", TRACE)
logger("org.age.akka", WARN, ["TEST_RESULTS"])
logger("org.springframework", INFO)
logger("com.hazelcast", INFO)
