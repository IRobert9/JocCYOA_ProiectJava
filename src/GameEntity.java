public abstract class GameEntity {
    // Atribute comune tuturor entitatilor din joc
    protected String name;
    protected int healthPoints;
    protected int maxHealth;
    protected int baseDefense;
    protected int activeDefense;
    protected int baseSpeed;
    protected int activeSpeed;

    // Atribute de atac si magie
    protected int baseInteligence;
    protected int activeInteligence;
    protected int baseStrength;
    protected int activeStrength;

    public GameEntity(String name) {
    this.name = name;
    }

    // Metoda calcul activeAttackPower
    public abstract int calculatePhysicalDamage();
    public abstract int calculateMagicDamage();


    // Metoda pentru a primi daune
    public void takeDamage(int rawDamage) {
        // --- FORMULA NOUĂ: Diminishing Returns ---
        // Formula: 100 / (100 + Armură).
        // 0 Armură -> 1.0 (100% dmg primit)
        // 50 Armură -> 0.66 (66% dmg primit)
        // 100 Armură -> 0.50 (50% dmg primit)

        // Folosim 100.0 pentru a forța calculul cu zecimale (double)
        double defenseMultiplier = 100.0 / (100.0 + (double)this.activeDefense);

        // Calculăm dauna finală
        int damageTaken = (int) (rawDamage * defenseMultiplier);

        // REGULA DE AUR: Asigurăm minim 1 damage (ca să nu fii invincibil la atacuri slabe)
        if (damageTaken < 1 && rawDamage > 0) {
            damageTaken = 1;
        }

        // Se scad punctele de viață
        this.healthPoints -= damageTaken;

        // Verificăm viața (să nu fie negativă)
        if(this.healthPoints <= 0) {
            this.healthPoints = 0;
            System.out.println(this.name + " a fost învins!");
        } else {
            System.out.println(this.name + " primește " + damageTaken + " daune (Redus de la " + rawDamage + "). HP rămas: " + this.healthPoints);
        }
    }

    public int getActiveStrength() {
        return this.activeStrength;
    }
    
    public int getActiveDefense() {
        return this.activeDefense;
    }
    
    public int getActiveInteligence() {
        return this.activeInteligence;
    }

    public String getName() {
        return this.name;
    }

    public int getHealthPoints() {
        return this.healthPoints;
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public int getActiveSpeed() {
        return this.activeSpeed;
    }

}
