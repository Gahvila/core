package net.gahvila.gahvilacore.Profiles.Prefix;

public enum Ranks {
    //default
    DEFAULT(""),
    //legacy ranks
    VIP("VIP"),
    MVP("MVP"),
    PRO("Pro"),
    OG("OG"),
    //new ranks from cheapest to best
    MOCHA("Mocha"),
    LATTE("Latte"),
    CAPPUCCINO("Cappuccino"),
    CORTADO("Cortado"),
    ESPRESSO("Espresso");

    private final String displayName;

    Ranks(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
