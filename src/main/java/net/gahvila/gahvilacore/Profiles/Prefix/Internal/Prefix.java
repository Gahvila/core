package net.gahvila.gahvilacore.Profiles.Prefix.Internal;

public enum Prefix {
    //default
    DEFAULT(""),
    //new ranks from cheapest to best
    MOCHA("Mocha"),
    LATTE("Latte"),
    CAPPUCCINO("Cappuccino"),
    CORTADO("Cortado"),
    ESPRESSO("Espresso"),
    //legacy ranks
    VIP("VIP"),
    MVP("MVP"),
    PRO("Pro"),
    OG("OG"),
    //admin
    ADMIN("Admin");

    private final String displayName;

    Prefix(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
