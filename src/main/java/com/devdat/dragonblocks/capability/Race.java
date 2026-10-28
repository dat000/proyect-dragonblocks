package com.devdat.dragonblocks.capability;

public enum Race {
    // -1: sin raza, se dibuja el modelo vanilla de Minecraft
    VANILLA(-1, "vanilla"),
    HUMAN(0, "humano"),
    SAIYAN(1, "saiyajin"),
    NAMEKIAN(2, "namekiano");

    private final int id;
    private final String name;

    Race(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public static Race fromId(int id) {
        for (Race race : values()) {
            if (race.id == id) return race;
        }
        return VANILLA;
    }

    public static Race byName(String name) {
        for (Race race : values()) {
            if (race.name.equalsIgnoreCase(name)) return race;
        }
        return null;
    }
}
