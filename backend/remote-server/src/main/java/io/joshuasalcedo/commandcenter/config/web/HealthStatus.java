package io.joshuasalcedo.commandcenter.config.web;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;

/**
 * Snapshot of the application's runtime health.
 *
 * @param status         simple status string ("OK", "DEGRADED", etc.)
 * @param timestamp      when this snapshot was taken
 * @param uptime         human-readable uptime since JVM start
 * @param uptimeMillis   raw uptime in milliseconds
 * @param memory         JVM heap memory stats in megabytes
 * @param availableCores number of processors available to the JVM
 * @param javaVersion    running Java runtime version
 *
 * @author JoshuaSalcedo
 * @since 4/13/2026
 */
 record HealthStatus(
        String status,
        Instant timestamp,
        String uptime,
        long uptimeMillis,
        MemoryInfo memory,
        int availableCores,
        String javaVersion
) {

    public record MemoryInfo(
            long usedMb,
            long freeMb,
            long totalMb,
            long maxMb,
            double usagePercent
    ) {
        public static MemoryInfo capture() {
            Runtime runtime = Runtime.getRuntime();
            long total = runtime.totalMemory();
            long free = runtime.freeMemory();
            long used = total - free;
            long max = runtime.maxMemory();

            return new MemoryInfo(
                    toMb(used),
                    toMb(free),
                    toMb(total),
                    toMb(max),
                    Math.round((used * 10000.0) / max) / 100.0
            );
        }

        private static long toMb(long bytes) {
            return bytes / (1024 * 1024);
        }
    }

    public static HealthStatus capture() {
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();

        return new HealthStatus(
                "OK",
                Instant.now(),
                formatUptime(uptimeMillis),
                uptimeMillis,
                MemoryInfo.capture(),
                Runtime.getRuntime().availableProcessors(),
                System.getProperty("java.version")
        );
    }

    private static String formatUptime(long millis) {
        Duration d = Duration.ofMillis(millis);
        long days = d.toDays();
        long hours = d.toHoursPart();
        long minutes = d.toMinutesPart();
        long seconds = d.toSecondsPart();

        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        }
        return String.format("%ds", seconds);
    }
}