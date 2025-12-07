public class Equipment extends Item{
    protected String slotType;
    protected String handType;// "MAINHAND", "OFFHAND", "HELMET", "CHEST", "BOOTS", "ACCESSORY"

    // Atribute care imbunatatesc stats
    protected int bonusHealth;
    protected int bonusMana;
    protected int bonusStamina;
    protected int bonusStrength;
    protected int bonusIntelligence;
    protected int bonusDefense;
    protected int bonusSpeed;

    public Equipment(String name, String description, int value, String slotType, String handType,
                     int bonusHealth, int bonusMana, int bonusStamina,
                     int bonusStrength, int bonusIntelligence,
                     int bonusDefense, int bonusSpeed) {
        // Apelam constructorul clasei parinte (Item)
        super(name, description, value);

        this.slotType = slotType;
        this.handType = handType; // <-- AM ADĂUGAT ASTA
        this.bonusHealth = bonusHealth;
        this.bonusMana = bonusMana;
        this.bonusStamina = bonusStamina;
        this.bonusStrength = bonusStrength;
        this.bonusIntelligence = bonusIntelligence;
        this.bonusDefense = bonusDefense;
        this.bonusSpeed = bonusSpeed;
    }

    // Implementam metoda abstracta "useItem"
    @Override
    public void useItem(PlayerCharacter target) {
        // Acum, 'useItem' doar pasează obiectul către metoda 'equipItem'
        // 'equipItem' (din PlayerCharacter) va decide dacă să echipeze SAU să dezechipeze
        target.equipItem(this);
        // Am șters System.out.println de aici, pentru că e confuz.
    }

    // Getters pentru a accesa atributele bonus
    public String getSlotType() { return slotType; }
    public int getHealthBonus() { return bonusHealth; }
    public int getStrengthBonus() { return bonusStrength; }
    public int getIntelligenceBonus() { return bonusIntelligence; }
    public int getDefenseBonus() { return bonusDefense; }
    public int getSpeedBonus() { return bonusSpeed; }
    public int getManaBonus() { return bonusMana; }
    public int getStaminaBonus() { return bonusStamina; }
    public String getHandType() { return this.handType; }
}
