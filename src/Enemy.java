public class Enemy extends GameEntity {
    // Atribute unice tuturor inamicilor
    private int experienceReward;
    private int goldReward;
    private int dropReward;
    private int dropRate;

    public Enemy(String name, int maxHealth, int baseDefense, int baseSpeed, 
                int baseStrength, int baseIntelligence, // Parametrii pentru stats
                int xpReward, int goldReward, int dropReward, int dropRate) {
        super(name);
        this.maxHealth = maxHealth;
        this.healthPoints = maxHealth;
        this.baseDefense = baseDefense;
        this.activeDefense = baseDefense;
        this.baseSpeed = baseSpeed;
        this.activeSpeed = baseSpeed;
        this.baseStrength = baseStrength;
        this.activeStrength = baseStrength;
        this.baseInteligence = baseIntelligence;
        this.activeInteligence = baseIntelligence;

        this.experienceReward = xpReward;
        this.goldReward = goldReward;
        this.dropReward = dropReward;
        this.dropRate = dropRate;
    }
 
    @Override
    public int calculatePhysicalDamage() {
        // Inamicul foloseste forta pentru a calcula daunele fizice
        int damage = (this.activeStrength * 2) + 3;
        return damage;
    }
    @Override
    public int calculateMagicDamage() {
       
        return 0;
    }

    public void attackPlayer(PlayerCharacter target) {
        target.takeDamage(this.calculatePhysicalDamage());
    }

    // --- Gettere pentru Recompense ---

    public int getExperienceReward() {
        return this.experienceReward;
    }

    public int getGoldReward() {
        return this.goldReward;
    }
    
    
}
