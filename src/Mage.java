public class Mage extends PlayerCharacter {

    public Mage(String name){
        super(name);
        this.maxHealth = 100;
        this.healthPoints = this.maxHealth;
        this.baseStrength = 2;
        this.activeStrength = this.baseStrength;
        this.baseInteligence = 14;
        this.activeInteligence = this.baseInteligence;
        this.baseDefense = 4;
        this.activeDefense = this.baseDefense;
        this.baseSpeed = 8;
        this.activeSpeed = this.baseSpeed;
        this.baseMana = 30;
        this.activeMana = this.baseMana;
        this.baseStamina = 10;
        this.activeStamina = this.baseStamina;
    }

    // Implementarea logicii de atact pentru daune fizice
    @Override
    public int calculatePhysicalDamage() {
        // Chiar daca ar avea strenght, atacul ramane slab
        // Logica de consumare a Staminei - mai tarziu
        return activeStrength / 2;
    }

    // Implementarea logicii de atac pentru daune magice
    @Override
    public int calculateMagicDamage() {
        // Magul are abilitati magice
        // Chiar daca ar avea inteligenta, atacul magic ramane slab
        int damage = (this.activeInteligence * 3) + 5;
        return damage;
    }
}
