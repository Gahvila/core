package net.gahvila.gahvilacore.Profiles.Prefix.Internal.PrefixType;

public enum Gradient {
    VESIMELOONI("Vesimelooni", "#38b91c:#FF476F:#E23C2E:#E23C2E:#FF476F:#38b91c"),
    KAHVI("Kahvi", "#6f4d36:#634632:#583d2b:#4f3727:#402d20"),
    MAITOKAHVI("Maitokahvi", "#fffff1:#eadabc:#d4b588:#bf9053:#a96b1e"),
    PETERGRIFFIN("Peter Griffin", "#783f04:#fce5cd:#ffffff:#3abf00:#000000"),
    DRAGONLORE("Dragon Lore", "#fe620f:#f8d51c:#b5c244:#415d2a:#c9b362"),
    MERI("Meri", "#8aeed5:#63b9db:#4479e1:#2645e0:#071eed"),
    SUOMI1("Suomi 1", "#004bde:#ffffff:#ffffff:#004bde"),
    SUOMI2("Suomi 2", "#004bde:#ffffff"),
    KULTA("Kulta", "#a67c00:#bf9b30:#ffbf00:#ffcf40:#ffdc73"),
    SMARAGDI("Smaragdi", "#004d24:#058743:#00d062:#009c4a:#004721"),
    IKEA("Ikea", "#ffcc00:#003399:#003399:#ffcc00"),
    LIEKKI("Liekki", "#ffc100:#ff9a00:#ff7400:#ff4d00:#ff0000"),
    SIGNAALITON("Signaaliton", "#ffffff:#f9fb00:#02feff:#01ff00:#fd00fb:#fb0102:#0301fc:#000000"),
    PAHAPOSSU("Pahapossu", "#6DE249:#A5E900:#379D1A:#A5E900:#6DE249"),
    TITANHOLO("Titan Holo", "#0C246A:#1356DC:#538DF5:#1356DC:#0C246A"),
    PEHMEAKELTAINEN("Pehmeä keltainen", "#fffdcc:#fffcbc:#fffcb1:#fffb9d:#fffa8e"),
    TULENPUNAINEN("Tulenpunainen", "#ff2a04:#ff0000:#f20000:#e50000:#d60000"),
    PIKKUPANDA("Pikkupanda", "#b13d14:#fce7d2:#340701:#9c1a04:#db8758"),
    NAAMIOINTI("Naamiointi", "#19270d:#25591f:#818c3c:#72601b:#593a0e"),
    KIRKASTAIVAS("Kirkas taivas", "#009fff:#19a8ff:#30b1ff:#48baff:#62c4ff"),
    AMERIKKA("Amerikka", "#ff0000:#ffffff:#0447fd:#ffffff:#ff0000");


    private final String displayName;
    private final String gradient;

    Gradient(String displayName, String gradient) {
        this.displayName = displayName;
        this.gradient = gradient;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGradient() {
        return gradient;
    }
}
