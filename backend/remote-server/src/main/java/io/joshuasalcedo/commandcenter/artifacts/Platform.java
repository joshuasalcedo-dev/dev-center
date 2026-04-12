package io.joshuasalcedo.commandcenter.artifacts;

import java.util.Optional;

public enum Platform {
    WINDOWS_X64("windows", "x86_64"),
    WINDOWS_ARM64("windows", "aarch64"),
    LINUX_X64_DEB("linux", "x86_64"),
    LINUX_ARM64_DEB("linux", "aarch64"),
    LINUX_X64_RPM("linux", "x86_64"),
    MACOS_X64("darwin", "x86_64"),
    MACOS_ARM64("darwin", "aarch64");

    private final String tauriTarget;
    private final String tauriArch;

    Platform(String tauriTarget, String tauriArch) {
        this.tauriTarget = tauriTarget;
        this.tauriArch = tauriArch;
    }

    public String tauriTarget() { return tauriTarget; }
    public String tauriArch()   { return tauriArch; }

    public static Optional<Platform> fromTauri(String target, String arch) {
        for (Platform p : values()) {
            if (p.tauriTarget.equals(target) && p.tauriArch.equals(arch)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }
}
