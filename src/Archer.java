
public class Archer extends Warrior {

    // Constructorul pentru Archer
    public Archer(String name) {
        // 1. Apelăm constructorul clasei părinte (Warrior)
        // Setează HP=150, Strength=12, Defense=10, Speed=10, etc.
        super(name);

        // 2. Acum, MODIFICĂM statisticile moștenite pentru specializare
        
        // Scădem Apărarea (armură ușoară)
        this.baseDefense -= 4; // Total 6 (10 din Warrior - 4)
        this.activeDefense = this.baseDefense;

        // Creștem Viteza (agilitate)
        this.baseSpeed += 4; // Total 14 (10 din Warrior + 4)
        this.activeSpeed = this.baseSpeed;
        
        // Arcașii folosesc mai mult Stamina pentru abilități
        this.baseStamina += 10; // Total 40 (30 din Warrior + 10)
        this.activeStamina = this.baseStamina;
    }
    
    // La fel ca la Knight, Arcașul moștenește automat 
    // metodele 'calculatePhysicalDamage()' și 'calculateMagicDamage()'
    // de la Warrior. Putem să le suprascriem (override) mai târziu
    // dacă vrem ca daunele arcașului să depindă de Viteză, de exemplu.
}