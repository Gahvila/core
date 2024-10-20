package net.gahvila.gahvilacore.Profiles.Prefix.Backend.Enum.PrefixType;

public enum Gradient {
    VESIMELOONI("Vesimelooni", "#38b91c:#FF476F:#E23C2E:#E23C2E:#FF476F:#38b91c"),
    KAHVI("Kahvi", "#6f4d36:#634632:#583d2b:#4f3727:#402d20"),
    MAITOKAHVI("Maitokahvi", "#fffff1:#eadabc:#d4b588:#bf9053:#a96b1e"),
    PETERGRIFFIN("Peter Griffin", "#783f04:#fce5cd:#ffffff:#3abf00:#000000"),
    DRAGONLORE("Dragon Lore", "#fe620f:#f8d51c:#b5c244:#415d2a:#c9b362"),
    MERI("Meri", "#8aeed5:#63b9db:#4479e1:#2645e0:#071eed"),
    SATEENKAARI("Sateenkaari", "#e81416:#ffa500:#faeb36:#79c314:#487de7:#4b369d:#70369d"),
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
    TULENPUNAINEN("Tulenpunainen", "#cc0000:#ff1c00:#e2353c:#d10044:#ff3f34"),
    PIKKUPANDA("Pikkupanda", "#b13d14:#fce7d2:#340701:#9c1a04:#db8758"),
    NAAMIOINTI("Naamiointi", "#19270d:#25591f:#818c3c:#72601b:#593a0e"),
    KIRKASTAIVAS("Kirkas taivas", "#009fff:#19a8ff:#30b1ff:#48baff:#62c4ff"),
    AMERIKKA("Amerikka", "#ff0000:#ffffff:#0447fd:#ffffff:#ff0000"),
    DOOM("Doom", "#716a4e:#4e3b35:#6c4b36:#b45e33:#1c3b33"),
    RUUSUKULTA("Ruusukulta", "#daa4aa:#ffdfe1:#ffebe9:#fff5ee:#fffffb"),
    RUBIINI("Rubiini", "#850014:#ae001a:#e10531:#ae001a:#850014"),
    MYKISTETTYPUNAINEN("Mykistetty punainen", "#cbabab:#ab7e7d:#963831:#7c0a02:#590303"),
    RANSKA("Ranska", "#002654:#FFFFFF:#ED2939"),
    METSANVIHREA("Metsänvihreä", "#4a6741:#3f5a36:#374f2f:#304529:#22311d"),
    VAPORWAVE("Vaporwave", "#ff00c1:#9600ff:#4900ff:#00b8ff:#00fff9"),
    HEHKUVAKULLANRUSKEA("Hehkuva kullanruskea", "#fff1c5:#ffca42:#ed872d:#552b16:#2f1111"),
    MUSTAVALKOINEN("Mustavalkoinen", "#c0c0c0:#9b9b9b:#5b5b5b:#444444:#000000"),
    SUMUINENAURINGONLASKU("Sumuinen auringonlasku", "#a193c9:#d594c4:#e0a7b7:#e4bba8:#ddd1b2"),
    LEPAKKOMIES("Lepakkomies", "#fdff00:#988829:#242424:#282e3c:#505c7c"),
    KURPITSA("Kurpitsa", "#ff9a00:#ff8d00:#ff8100:#ff7400:#ff6700"),
    KESKIYONVIOLETTI("Keskiyön violetti", "#1f2386:#25177d:#310e66:#360a5d:#3f054d"),
    MYRSKY("Myrsky", "#344556:#314254:#2e3e4f:#2b3a4b:#243241"),
    REVONTULI("Revontuli", "#01efac:#01cbae:#2082a6:#524096:#5f2a84"),
    YOTAIVAS("Yötaivas", "#040409:#050e39:#061558:#0e267d:#2f4eae"),
    HATTARA("Hattara", "#ff9ee6:#ffa3e8:#ffaaeb:#ffb2ee:#ffbcee"),
    STEAMPUNK("Steampunk", "#ab7633:#ceae7d:#6d2316:#ceae7d:#ab7633"),
    TAHTIPOLY("Tähtipöly", "#003632:#002d3e:#001c3c:#08002b:#000000"),
    ZOMBI("Zombi", "#cdffb0:#a4cc8c:#7b9969:#526646:#293323"),
    LAVENTELI("Laventeli", "#9474f9:#a474f9:#b474f9:#c474f9:#d474f9"),
    OMENA("Omena", "#8a0109:#8a0109:#a96632:#8a0109:#8a0109"),
    KARAMELLI("Karamelli", "#80532d:#8c6028:#a0734d:#a97947:#c78752"),
    MUNAKOISO("Munakoiso", "#733542:#603049:#4d2a52:#391f5c:#0c076d"),
    LIMONADI("Limonadi", "#a4ffb8:#b8fdbe:#c8ffc0:#daffc4:#ecffc8");


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

    public String getPermissionNode() {
        return "gahvilacore.prefixtype.gradient." + this.name().toLowerCase();
    }
}
