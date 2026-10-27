package com.devdat.dragonblocks.capability;

import net.minecraft.nbt.CompoundTag;

public class PlayerStats {
    private float ki = 0.0f;
    private float maxKi = 100.0f;
    private float stamina = 100.0f;
    private float maxStamina = 100.0f;

    // 0: Humano, 1: Saiyajin, 2: Namekiano (según el GDD)
    private int race = 0;

    // 1: Infancia, 2: Adolescencia, 3: Adultez
    private int lifeStage = 1;

    // Nuevo: Estado de carga de Ki
    private boolean isCharging = false;

    public float getKi() { return ki; }
    public void setKi(float ki) { this.ki = Math.max(0.0f, Math.min(ki, this.maxKi)); }

    public float getMaxKi() { return maxKi; }
    public void setMaxKi(float maxKi) { this.maxKi = maxKi; }

    public float getStamina() { return stamina; }
    public void setStamina(float stamina) { this.stamina = Math.max(0.0f, Math.min(stamina, this.maxStamina)); }

    public float getMaxStamina() { return maxStamina; }
    public void setMaxStamina(float maxStamina) { this.maxStamina = maxStamina; }

    public int getRace() { return race; }
    public void setRace(int race) { this.race = race; }

    public int getLifeStage() { return lifeStage; }
    public void setLifeStage(int lifeStage) { this.lifeStage = lifeStage; }

    public boolean isCharging() { return isCharging; }
    public void setCharging(boolean charging) { isCharging = charging; }

    public void addKi(float amount) {
        setKi(this.ki + amount);
    }

    // Guardar datos en el disco (NBT)
    public void saveNBTData(CompoundTag nbt) {
        nbt.putFloat("Ki", ki);
        nbt.putFloat("MaxKi", maxKi);
        nbt.putFloat("Stamina", stamina);
        nbt.putFloat("MaxStamina", maxStamina);
        nbt.putInt("Race", race);
        nbt.putInt("LifeStage", lifeStage);
        nbt.putBoolean("IsCharging", isCharging); // Guardamos el estado de carga
    }

    // Cargar datos desde el disco (NBT)
    public void loadNBTData(CompoundTag nbt) {
        ki = nbt.getFloat("Ki");
        maxKi = nbt.getFloat("MaxKi");
        stamina = nbt.getFloat("Stamina");
        maxStamina = nbt.getFloat("MaxStamina");
        race = nbt.getInt("Race");
        lifeStage = nbt.getInt("LifeStage");
        isCharging = nbt.getBoolean("IsCharging"); // Cargamos el estado de carga
    }
}