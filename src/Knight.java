public class Knight extends Warrior {

    public Knight(String name) {
        // 1. Apelăm constructorul clasei părinte (Warrior)
        // Acest 'super(name)' rulează TOT codul din constructorul Warrior:
        // setează HP=150, Strength=12, Defense=10, Speed=10, etc.
        super(name);

        // 2. Acum, MODIFICĂM statisticile moștenite pentru a reflecta specializarea
        
        // Creștem Apărarea (Tanc)
        this.baseDefense += 4; // Total 14 (10 din Warrior + 4)
        this.activeDefense = this.baseDefense; // Actualizăm și statistica activă

        // Scădem Viteza (Armură grea)
        this.baseSpeed -= 2; // Total 8 (10 din Warrior - 2)
        this.activeSpeed = this.baseSpeed;
        
        // O mică ajustare pentru a reflecta rolul
        this.maxHealth += 10; // Bonus mic de viață (Total 160)
        this.healthPoints = this.maxHealth;
    }

    // --- DESPRE METODELE DE ATAC ---
    // Nu este nevoie să suprascriem (override) 'calculatePhysicalDamage()'
    // sau 'calculateMagicDamage()'.
    // Cavalerul va folosi automat aceleași metode de atac
    // pe care le-a moștenit direct de la 'Warrior'.
}