public class Potion extends Item  {
    protected String effectType; // "HP", "MANA", "STAMINA"
    protected int effectValue;

    public Potion(String name, String description, int value, String effectType, int effectValue) {
        // Apelam constructorul clasei parinte (Item)
        super(name, description, value);

        this.effectType = effectType;
        this.effectValue = effectValue;
    }

    // Implementam metoda abstracta "useItem"
    @Override
    public void useItem(PlayerCharacter target) {
        switch (this.effectType.toUpperCase()) {
            case "HP":
            // Adaugare metoda "heal" in PlayerCharacter
                target.heal(this.effectValue);
                System.out.println(target.getName() + " restored " + this.effectValue + " HP using " + this.name + ".");
                break;
            case "MANA":
            // Adaugare metoda "restoreMana" in PlayerCharacter
                target.restoreMana(this.effectValue);
                System.out.println(target.getName() + " restored " + this.effectValue + " Mana using " + this.name + ".");
                break;
            case "STAMINA":
            // Adaugare metoda "restoreStamina" in PlayerCharacter
                target.restoreStamina(this.effectValue);
                System.out.println(target.getName() + " restored " + this.effectValue + " Stamina using " + this.name + ".");
                break;
            default:
                System.out.println("Unknown potion effect type: " + this.effectType);
        }
        target.removeItem(this); // Eliminam potiunea din inventar dupa utilizare
    }

    // In Potion.java, add these getters:
    public String getEffectType() { return this.effectType; }
    public int getEffectValue() { return this.effectValue; }


}
