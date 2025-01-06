package net.gahvila.gahvilacore.Music;

public enum MusicSorting {
    ALPHABETICAL("Aakkosjärjestys"),
    ARTIST("Artisti"),
    LENGTH("Pituus");

    private final String displayName;

    MusicSorting(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

