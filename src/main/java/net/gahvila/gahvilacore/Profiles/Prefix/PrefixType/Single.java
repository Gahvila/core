package net.gahvila.gahvilacore.Profiles.Prefix.PrefixType;

public enum Single {
    MUSTA("Musta", "black"),
    TUMMANSININEN("Tummansininen", "dark_blue"),
    TUMMANVIHREA("Tummanvihreä", "dark_green"),
    TUMMANTURKOOSI("Tummanturkoosi", "dark_aqua"),
    TUMMANPUNAINEN("Tummanpunainen", "dark_red"),
    TUMMANVIOLETTI("Tummanvioletti", "dark_purple"),
    KULTA("Kulta", "gold"),
    HARMAA("Harmaa", "gray"),
    TUMMANHARMAA("Tummanharmaa", "dark_gray"),
    SININEN("Sininen", "blue"),
    VIHREA("Vihreä", "green"),
    TURKOOSI("Turkoosi", "aqua"),
    PUNAINEN("Punainen", "red"),
    PINKKI("Pinkki", "light_purple"),
    KELTAINEN("Keltainen", "yellow"),
    VALKOINEN("Valkoinen", "white");

    private final String displayName;
    private final String color;

    Single(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}
