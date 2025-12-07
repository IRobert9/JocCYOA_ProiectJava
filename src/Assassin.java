
public class Assassin extends Warrior {

    // Constructorul pentru Assassin
    public Assassin(String name) {
        // 1. Apelăm constructorul clasei părinte (Warrior)
        // Setează HP=150, Strength=12, Defense=10, Speed=10, etc.
        super(name);

        // 2. Acum, MODIFICĂM statisticile pentru specializare (Glass Cannon)
        
        // Scădem Apărarea (fără armură grea)
        this.baseDefense -= 6; // Total 4 (10 din Warrior - 6)
        this.activeDefense = this.baseDefense;

        // Scădem Viața (mai fragil)
        this.maxHealth -= 20; // Total 130 (150 din Warrior - 20)
        this.healthPoints = this.maxHealth;

        // Creștem Viteza (agilitate)
        this.baseSpeed += 5; // Total 15 (10 din Warrior + 5)
        this.activeSpeed = this.baseSpeed;
        
        // Creștem Forța (daune brute)
        this.baseStrength += 3; // Total 15 (12 din Warrior + 3)
        this.activeStrength = this.baseStrength;
    }

    // --- Metodă de Atac Unică (Opțional) ---
    // Deoarece Asasinul este special, am putea suprascrie (override)
    // metoda de atac fizic pentru a include o șansă de "critical hit"
    
    @Override
    public int calculatePhysicalDamage() {
        // Folosim logica de bază din Warrior
        int damage = super.calculatePhysicalDamage(); 
        
        // Adăugăm o șansă de 20% să dublăm daunele (Critical Hit)
        if (Math.random() < 0.20) { // 20% șansă
            System.out.println(this.name + " lovește un punct vital! (Critical Hit!)");
            return damage * 2;
        }
        
        return damage;
    }
}