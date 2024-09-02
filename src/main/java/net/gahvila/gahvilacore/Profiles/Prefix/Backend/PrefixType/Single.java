package net.gahvila.gahvilacore.Profiles.Prefix.Backend.PrefixType;

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
    KIRKKAANPUNAINEN("Kirkkaanpunainen", "#ff0000"),
    PUNAINEN("Punainen", "red"),
    PINKKI("Pinkki", "light_purple"),
    KELTAINEN("Keltainen", "yellow"),
    VALKOINEN("Valkoinen", "white"),
    GAHVILA("Gahvila", "#96613d"),
    GAHVILA2("Gahvila 2", "#d2a56d"),
    LUU("Luu", "#E3DAC9"),
    BRONSSI("Bronssi", "#CD7F32"),
    KAHVI("Kahvi", "#6F4E37"),
    AERO("Aero", "#7CB9E8"),
    MERIPIHKA("Meripihka", "#FFBF00"),
    VAUVANSININEN("Vauvansininen", "#89CFF0"),
    ANDROID("Android", "#3DDC84"),
    VANHAANDROID("Vanha Android", "#A4C639"),
    KUPARI("Kupari", "#DA8A67"),
    KERMA("Kerma", "#FFFDD0"),
    RUSKEA("Ruskea", "#964B00"),
    TUMMANRUSKEA("Tummanruskea", "#654321"),
    PORKKANA("Porkkana", "#ED9121"),
    DENIM("Denim", "#1560BD"),
    HALFLIFE("Half-Life", "#fb7e14"),
    MUNAKOISO("Munakoiso", "#614051");



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

    public String getPermissionNode() {
        return "gahvilacore.prefixtype.single." + this.name().toLowerCase();
    }
}
