public class Warrior extends PlayerCharacter {

    public Warrior(String name){
        super(name);
        this.maxHealth = 150;
        this.healthPoints = this.maxHealth;
        this.baseStrength = 12;
        this.activeStrength = this.baseStrength;
        this.baseInteligence = 0;
        this.activeInteligence = this.baseInteligence;
        this.baseDefense = 10;
        this.activeDefense = this.baseDefense;
        this.baseSpeed = 10;
        this.activeSpeed = this.baseSpeed;
        this.baseMana = 0;
        this.activeMana = this.baseMana;
        this.baseStamina = 30;
        this.activeStamina = this.baseStamina;
    }

    // Implementarea logicii de atact pentru daune fizice
    @Override
    public int calculatePhysicalDamage() {
        // Razboinicul foloseste forta
        int damage = (this.activeStrength * 3) + 5;
        // Logica de consumare a Staminei - mai tarziu
        return damage;
    }

    // Implementarea logicii de atac pentru daune magice
    @Override
    public int calculateMagicDamage() {
        // Razboinicul nu are abilitati magice
        // Chiar daca ar avea inteligenta, atacul magic ramane slab
        return this.activeInteligence / 2;
    }
}
