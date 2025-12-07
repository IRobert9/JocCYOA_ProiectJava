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
        //  Se creeaza rata de reducere a daunelor (%)
        double damageReductionRate = this.activeDefense / 100.0;
        // Evitam vindecarea prin depasirea apararii (ex:110%)
        if(damageReductionRate > 0.95) {
            damageReductionRate = 0.90;
        }
        // Se calculeaza dauna finala dupa reducere
        double finalDamageDouble = rawDamage * (1 - damageReductionRate);
        // Se rotunjeste dauna finala (INT)
        int damageTaken = (int) Math.round(finalDamageDouble);
        // Se scad punctele de viata
        this.healthPoints -= damageTaken;
        // Verificam daca punctele de viata au scazut sub 0
        if(this.healthPoints <= 0) {
            this.healthPoints = 0;
            System.out.println(this.name + " has been defeated!");
        } else {
            System.out.println(this.name + " takes " + damageTaken + " damage, remaining HP: " + this.healthPoints);}
        
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
