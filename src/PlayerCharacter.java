import java.util.ArrayList;


public abstract class PlayerCharacter extends GameEntity {
    //1. Resurse
    protected int baseMana;
    protected int activeMana;
    protected int baseStamina;
    protected int activeStamina;
    protected int experiencePoints;
    protected int level;
    protected int gold;
    protected String trait;
    protected int skillPoints;

    private ArrayList<Skill> knownSkills;    // Toate abilitățile învățate (Cartea de vrăji)
    private ArrayList<Skill> equippedSkills; // Doar cele active în luptă (Max 5)
    //2. Sloturi de echipament
    //2.1. Arme si scut
    protected Equipment equippedMainhand;
    protected Equipment equippedOffhand;
    //2.2. Armura
    protected Equipment equippedHelmet;
    protected Equipment equippedChest;
    protected Equipment equippedBoots;
    //2.3. Accesorii
    protected Equipment equippedAccessory1;
    protected Equipment equippedAccessory2;

    protected ArrayList<Item> inventory;
    protected ArrayList<Skill> skills;

    //3. Constructorul primeste valoare maxima a atributelor
    public PlayerCharacter (String name) {
        super(name);

        this.gold = 10;
        this.experiencePoints = 0;
        this.level = 1;
        this.skillPoints = 0;

        this.inventory = new ArrayList<>();
        this.skills = new ArrayList<>();

        // Inițializăm listele
        this.knownSkills = new ArrayList<>();
        this.equippedSkills = new ArrayList<>();

        // --- REGULA DE AUR: Atacul de Bază e mereu învățat și echipat ---
        Skill basicAttack = new Skill("Atac de Bază", "Lovitură simplă", 0, 0, 5, "PHYSICAL");
        this.knownSkills.add(basicAttack);
        this.equippedSkills.add(basicAttack);
    }


    // --- 3. METODĂ NOUĂ PENTRU A ÎNVĂȚA SKILL-URI ---
    public void learnSkill(Skill newSkill) {
        // Verificăm dacă îl știm deja ca să nu avem duplicate
        for(Skill s : knownSkills) {
            if(s.getName().equals(newSkill.getName())) {
                System.out.println("Deja cunoști abilitatea: " + newSkill.getName());
                return;
            }
        }

        this.knownSkills.add(newSkill);
        System.out.println("Ai învățat o nouă abilitate: " + newSkill.getName());

        // AUTO-ECHIPARE: Dacă ai loc liber pe bară, îl punem direct!
        if (equippedSkills.size() < 5) {
            equipSkill(newSkill);
        }
    }

    // 2. ECHIPARE (Mută din "Known" în "Equipped")
    public boolean equipSkill(Skill skill) {
        // Nu poți echipa ce nu știi
        if (!knownSkills.contains(skill)) {
            System.out.println("Eroare: Nu cunoști abilitatea " + skill.getName());
            return false;
        }

        // Nu poți avea mai mult de 5
        if (equippedSkills.size() >= 5) {
            System.out.println("Sloturi pline! Scoate o abilitate înainte să adaugi alta.");
            return false;
        }

        // Nu poți echipa de două ori același skill
        if (equippedSkills.contains(skill)) {
            System.out.println("Este deja echipată.");
            return false;
        }

        equippedSkills.add(skill);
        System.out.println("Ai echipat: " + skill.getName());
        return true;
    }

    // 3. DEZECHIPARE (Scoate din luptă, dar rămâne învățat)
    public void unequipSkill(Skill skill) {
        if (skill.getName().equals("Atac de Bază")) {
            System.out.println("Nu poți scoate Atacul de Bază!");
            return;
        }
        if (equippedSkills.remove(skill)) {
            System.out.println("Ai dezechipat: " + skill.getName());
        }
    }

    public ArrayList<Skill> getSkills() {
        return this.skills;
    }

    // METODA PENTRU A PRIMI XP
    public void gainExperience(int amount) {
        this.experiencePoints += amount;
        checkLevelUp();
    }

    // Verifică dacă am crescut în nivel
    private void checkLevelUp() {
        // Formula simplă: XP necesar = level * 100 (ex: 100 pt lvl 2, 200 pt lvl 3)
        int xpNeeded = this.level * 100;
        while (this.experiencePoints >= xpNeeded) {
            this.experiencePoints -= xpNeeded;
            this.level++;
            this.skillPoints += 5; // Primim 5 puncte per nivel!
            System.out.println(this.name + " a ajuns la nivelul " + this.level + "! (+5 Skill Points)");
            xpNeeded = this.level * 100; // Recalculăm pentru următorul nivel
        }
    }

    // TRASATURA
    public void setTrait(String trait){
        this.trait = trait;
    }

    // Inventar
    public void addItem(Item item){
        this.inventory.add(item);
        // Afisam o confirmare
        System.out.println(this.name + " a adaugat in inventar " + item.getName());
    }
    public void removeItem(Item item){
        // Verificam daca obiectul exista in inventar
        if(this.inventory.contains(item)){
            this.inventory.remove(item);
            System.out.println(this.name + " a eliminat din inventar" + item.getName());
        } else {
            // Daca obiectul nu este gasit
            System.out.println("Eroare " + this.name + " nu are " + item.getName() + " in inventar");
        }
    }

    // Metode pentru echipare si dezechipare a echipamentului
    public void equipItem(Equipment itemToEquip) {

        // --- Logica de Echipare/Dezechipare (Toggle) ---
        if (itemToEquip == equippedMainhand || itemToEquip == equippedOffhand ||
                itemToEquip == equippedHelmet || itemToEquip == equippedChest ||
                itemToEquip == equippedBoots || itemToEquip == equippedAccessory1 ||
                itemToEquip == equippedAccessory2)
        {
            // --------- CAZUL 1: DEZECHIPARE (Obiectul e deja echipat) ---------
            System.out.println("Dezechipare " + itemToEquip.getName());
            unequipStats(itemToEquip);

            // Golim slotul corect
            if (itemToEquip == equippedMainhand) this.equippedMainhand = null;
            if (itemToEquip == equippedOffhand) this.equippedOffhand = null;
            if (itemToEquip == equippedHelmet) this.equippedHelmet = null;
            if (itemToEquip == equippedChest) this.equippedChest = null;
            if (itemToEquip == equippedBoots) this.equippedBoots = null;
            if (itemToEquip == equippedAccessory1) this.equippedAccessory1 = null;
            if (itemToEquip == equippedAccessory2) this.equippedAccessory2 = null;

        } else {
            // --------- CAZUL 2: ECHIPARE (Obiectul e nou) ---------

            String slot = itemToEquip.getSlotType().toUpperCase();
            String hand = itemToEquip.getHandType().toUpperCase();

            // Verificăm mai întâi dacă este o armă (logica specială)
            if (slot.equals("MAINHAND")) {

                if (hand.equals("TWO_HANDED")) {
                    // REGULA PENTRU 2 MÂINI (ex: Arc)
                    System.out.println("Echipare armă cu 2 mâini: " + itemToEquip.getName());

                    // Dăm jos orice era în ambele mâini
                    if (this.equippedMainhand != null) unequipStats(this.equippedMainhand);
                    if (this.equippedOffhand != null) unequipStats(this.equippedOffhand);

                    this.equippedMainhand = itemToEquip; // Echipăm arma
                    this.equippedOffhand = null; // Blocăm mâna stângă
                    applyStats(itemToEquip);

                } else if (hand.equals("ONE_HANDED")) {
                    // REGULA PENTRU 1 MÂNĂ (ex: Sabie)
                    System.out.println("Echipare armă cu 1 mână: " + itemToEquip.getName());

                    // Dăm jos doar ce era în mâna principală
                    if (this.equippedMainhand != null) unequipStats(this.equippedMainhand);

                    this.equippedMainhand = itemToEquip;
                    applyStats(itemToEquip);
                    // Mâna stângă (Offhand) rămâne neschimbată
                }

            } else if (slot.equals("OFFHAND")) {
                // REGULA PENTRU MÂNA STÂNGĂ (ex: Scut)

                // Verificăm dacă mâna principală este compatibilă
                if (this.equippedMainhand != null && this.equippedMainhand.getHandType().equals("TWO_HANDED")) {
                    // EROARE: Nu poți echipa scut dacă ai armă cu 2 mâini
                    System.out.println("Eroare: Nu poți echipa " + itemToEquip.getName() + " cât timp folosești o armă cu 2 mâini!");
                    // TODO: Afișează această eroare și în JavaFX
                } else {
                    // Echipare reușită
                    System.out.println("Echipare obiect OffHand: " + itemToEquip.getName());
                    if (this.equippedOffhand != null) unequipStats(this.equippedOffhand);
                    this.equippedOffhand = itemToEquip;
                    applyStats(itemToEquip);
                }

            } else {
                // REGULA PENTRU ARMURĂ (Helmet, Chest, Boots, etc.)
                System.out.println("Echipare armură: " + itemToEquip.getName());

                switch (slot) {
                    case "HELMET":
                        if (this.equippedHelmet != null) unequipStats(this.equippedHelmet);
                        this.equippedHelmet = itemToEquip;
                        break;
                    case "CHEST":
                        if (this.equippedChest != null) unequipStats(this.equippedChest);
                        this.equippedChest = itemToEquip;
                        break;
                    case "BOOTS":
                        if (this.equippedBoots != null) unequipStats(this.equippedBoots);
                        this.equippedBoots = itemToEquip;
                        break;
                    case "ACCESSORY1":
                        if (this.equippedAccessory1 != null) unequipStats(this.equippedAccessory1);
                        this.equippedAccessory1 = itemToEquip;
                        break;
                    case "ACCESSORY2":
                        if (this.equippedAccessory2 != null) unequipStats(this.equippedAccessory2);
                        this.equippedAccessory2 = itemToEquip;
                        break;
                    default:
                        System.out.println("Slot necunoscut: " + slot);
                        return; // Oprim metoda dacă nu știm slotul
                }

                applyStats(itemToEquip); // Aplicăm statisticile armurii
            }
        }
    }
    private void applyStats(Equipment item) {
        // Aplicam bonsusuri la statistici ACTIVE
        this.activeStrength += item.getStrengthBonus();
        this.activeInteligence += item.getIntelligenceBonus();
        this.activeDefense += item.getDefenseBonus();
        this.activeSpeed += item.getSpeedBonus();

        // HP, MANA, STAMINA aplicam la maxim si vindecam
        this.maxHealth += item.getHealthBonus();
        this.healthPoints += item.getHealthBonus(); // Bonusul de HP te și vindecă
    
        this.baseMana += item.getManaBonus();
        this.activeMana += item.getManaBonus();
    
        this.baseStamina += item.getStaminaBonus();
        this.activeStamina += item.getStaminaBonus();
    }

    private void unequipStats(Equipment item) {
        // Eliminăm bonusurile din statisticile ACTIVE
        this.activeStrength -= item.getStrengthBonus();
        this.activeInteligence -= item.getIntelligenceBonus();
        this.activeDefense -= item.getDefenseBonus();
        this.activeSpeed -= item.getSpeedBonus();

        // Pentru HP, Mana, Stamina, scădem din MAXIM
        // (Aici logica devine complexă dacă HP-ul curent e mai mic, dar simplificăm)
        this.maxHealth -= item.getHealthBonus();
        if (this.healthPoints > this.maxHealth) {
            this.healthPoints = this.maxHealth; // Ajustăm HP-ul dacă depășește noul maxim
        }
    
        this.baseMana -= item.getManaBonus();
        if (this.activeMana > this.baseMana) {
            this.activeMana = this.baseMana;
        }

        this.baseStamina -= item.getStaminaBonus();
        if (this.activeStamina > this.baseStamina) {
            this.activeStamina = this.baseStamina;
        }
    }

    // Adaugare metode "heal", "restoreMana", "restoreStamina"
    public void heal(int amount) {
        this.healthPoints += amount;
        if (this.healthPoints > this.maxHealth) {
            this.healthPoints = this.maxHealth;
        }
    }
    public void restoreMana(int amount) {
        this.activeMana += amount;
        if (this.activeMana > this.baseMana) {
            this.activeMana = this.baseMana;
        }
    }
    public void restoreStamina(int amount) {
        this.activeStamina += amount;
        if (this.activeStamina > this.baseStamina) {
            this.activeStamina = this.baseStamina;
        }
    }

    // --- METODE NOI PENTRU LUPTĂ ---

    // O metodă de a verifica dacă avem destulă resursă
    public boolean canUseStamina(int amount) {
        return this.activeStamina >= amount;
    }

    // Metodă de a consuma stamina
    public void useStamina(int amount) {
        this.activeStamina -= amount;
        if (this.activeStamina < 0) {
            this.activeStamina = 0;
        }
    }

    public boolean canUseMana(int amount) {
        return this.activeMana >= amount;
    }

    public void useMana(int amount) {
        this.activeMana -= amount;
        if (this.activeMana < 0) {
            this.activeMana = 0;
        }
    }

    // Metoda de "Odihnă" (Rest)
    // Reîncarcă un procent din stamina și mana maximă, dar costă o tură.
    public void rest() {
        int staminaGain = (int) (this.baseStamina * 0.25); // Câștigă 25% din stamina max
        int manaGain = (int) (this.baseMana * 0.10);     // Câștigă 10% din mana max

        restoreStamina(staminaGain);
        restoreMana(manaGain);
    }

    // Metodă pentru a primi aur
    public void addGold(int amount) {
        this.gold += amount;
        if (this.gold < 0) {
            this.gold = 0; // Prevent negative gold
        }
    }

    // --- (METODĂ NOUĂ) Pentru a cheltui puncte de skill ---
    // În PlayerCharacter.java

    public boolean spendSkillPoint(String stat) {
        // 1. Verificăm dacă avem puncte de cheltuit
        if (this.skillPoints <= 0) {
            System.out.println("Nu mai ai puncte de alocat!");
            return false;
        }

        // 2. Aplicăm punctul la atributul corect
        switch (stat.toUpperCase()) {
            case "HEALTH":
                this.maxHealth += 10;       // Creștem maximul
                this.healthPoints += 10;    // Creștem curentul DOAR cu 10 (nu full heal)
                break;
            case "MANA":
                this.baseMana += 5;
                this.activeMana += 5;       // Adăugăm doar bonusul
                break;
            case "STAMINA":
                this.baseStamina += 5;
                this.activeStamina += 5;    // Adăugăm doar bonusul
                break;
            case "STRENGTH":
                this.baseStrength += 1;
                this.activeStrength += 1;   // Creștem și valoarea activă cu 1
                break;
            case "INTELLIGENCE":
                this.baseInteligence += 1;
                this.activeInteligence += 1;
                break;
            case "DEFENSE":
                this.baseDefense += 1;
                this.activeDefense += 1;
                break;
            case "SPEED":
                this.baseSpeed += 1;
                this.activeSpeed += 1;
                break;
            default:
                System.out.println("Eroare: Atribut necunoscut: " + stat);
                return false;
        }

        // 3. Scădem punctul cheltuit
        this.skillPoints--;
        return true;
    }

    // --- (METODĂ NOUĂ) Recuperare după luptă ---
    public void recoverAfterCombat() {
        // 1. Refacem complet Stamina și Mana (Gameplay fluid)
        this.activeStamina = this.baseStamina;
        this.activeMana = this.baseMana;

        // 2. Refacem puțin viața (10% din maxim), ca un mic bonus de victorie
        int healthRegen = (int) (this.maxHealth * 0.10);
        heal(healthRegen); // Folosim metoda heal care verifică deja să nu depășim maximul

        System.out.println("Te-ai odihnit după luptă. Stamina și Mana refăcute complet.");
    }

    public int getHealthPoints() {
        return this.healthPoints;
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public int getActiveMana() {
        return this.activeMana;
    }

    public int getBaseMana() {
        return this.baseMana;
    }

    public int getActiveStamina() {
        return this.activeStamina;
    }

    public int getBaseStamina() {
        return this.baseStamina;
    }

    // --- GETTERE PENTRU INVENTAR ȘI ECHIPAMENT ---

    public ArrayList<Item> getInventory() {
        return this.inventory;
    }

    public Equipment getEquippedMainhand() {
        return this.equippedMainhand;
    }

    public Equipment getEquippedOffhand() {
        return this.equippedOffhand;
    }

    public Equipment getEquippedHelmet() {
        return this.equippedHelmet;
    }

    public Equipment getEquippedChest() {
        return this.equippedChest;
    }

    public Equipment getEquippedBoots() {
        return this.equippedBoots;
    }

    public Equipment getEquippedAccessory1() {
        return this.equippedAccessory1;
    }

    public Equipment getEquippedAccessory2() {
        return this.equippedAccessory2;
    }

    // Getteri noi
    public int getLevel() { return this.level; }
    public int getExperience() { return this.experiencePoints; }
    public int getExpNeeded() { return this.level * 100; }
    public int getSkillPoints() { return this.skillPoints; }
    public String getTrasatura() { return this.trait; } // Getter simplu
    public int getGold() {
        return this.gold;
    }

    // --- SETTERI PENTRU LOAD GAME ---

    public void setLevel(int level) { this.level = level; }
    public void setExperience(int exp) { this.experiencePoints = exp; }
    public void setGold(int gold) { this.gold = gold; }

    public void setHealthPoints(int hp) { this.healthPoints = hp; }
    public void setMaxHealth(int maxHp) { this.maxHealth = maxHp; }

    public void setActiveMana(int mana) { this.activeMana = mana; }
    public void setBaseMana(int maxMana) { this.baseMana = maxMana; }

    public void setActiveStamina(int stamina) { this.activeStamina = stamina; }
    public void setBaseStamina(int maxStamina) { this.baseStamina = maxStamina; }

    public void setActiveStrength(int str) { this.activeStrength = str; }
    public void setActiveIntelligence(int intel) { this.activeInteligence = intel; }
    public void setActiveDefense(int def) { this.activeDefense = def; }
    public void setActiveSpeed(int spd) { this.activeSpeed = spd; }

    public void setSkillPoints(int points) { this.skillPoints = points; }

    // Getters pentru a accesa listele din exterior
    public ArrayList<Skill> getKnownSkills() { return knownSkills; }
    public ArrayList<Skill> getEquippedSkills() { return equippedSkills; }
}
