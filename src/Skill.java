public class Skill {
    private String name;
    private String description;
    private int manaCost;
    private int staminaCost;
    private int value; // Cât damage dă (sau cât vindecă)
    private String type; // "PHYSICAL", "MAGICAL", "HEAL"

    public Skill(String name, String description, int manaCost, int staminaCost, int value, String type) {
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
        this.staminaCost = staminaCost;
        this.value = value;
        this.type = type;
    }

    // --- Gettere ---
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getManaCost() { return manaCost; }
    public int getStaminaCost() { return staminaCost; }
    public int getValue() { return value; }
    public String getType() { return type; }
}