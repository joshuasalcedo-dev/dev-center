package io.joshuasalcedo.commandcenter.artifacts;

import java.util.Locale;

record Hash(Algorithm algorithm, String value) {

    enum Algorithm { SHA256, SHA512, MD5 }

    public Hash {
        if (algorithm == null) throw new IllegalArgumentException("algorithm required");
        if (value == null || value.isBlank()) throw new IllegalArgumentException("value required");
        // normalize to lowercase hex, validate expected length
        value = value.toLowerCase(Locale.ROOT);
        int expected = switch (algorithm) {
            case SHA256 -> 64;
            case SHA512 -> 128;
            case MD5    -> 32;
        };
        if (value.length() != expected || !value.matches("[0-9a-f]+")) {
            throw new IllegalArgumentException(
                    algorithm + " hash must be " + expected + " lowercase hex chars");
        }
    }

    public static Hash sha256(String hex) { return new Hash(Algorithm.SHA256, hex); }
    public static Hash sha512(String hex) { return new Hash(Algorithm.SHA512, hex); }

    @Override
    public String toString() {
        return algorithm.name().toLowerCase(Locale.ROOT) + ":" + value;
    }
}