import java.sql.*;

public class DatabaseManager {

    // Numele fișierului de salvare
    private static final String DB_URL = "jdbc:sqlite:rpg_save.db";

    // 1. CONECTAREA
    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Eroare conexiune: " + e.getMessage());
        }
        return conn;
    }

    // 2. INIȚIALIZAREA (Creează tabelul complet)
    public static void createNewDatabase() {
        // Salvăm TOATE atributele personajului
        String sql = "CREATE TABLE IF NOT EXISTS player_save (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text,\n"
                + " class_type text,\n"
                + " trait text,\n"
                + " level integer,\n"
                + " exp integer,\n"
                + " gold integer,\n"
                + " current_hp integer,\n"
                + " max_hp integer,\n"
                + " current_mana integer,\n"
                + " max_mana integer,\n"
                + " current_stamina integer,\n"
                + " max_stamina integer,\n"
                + " strength integer,\n"
                + " intelligence integer,\n"
                + " defense integer,\n"
                + " speed integer,\n"
                + " skill_points integer,\n"
                + " location_id integer\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Baza de date (re)inițializată cu succes.");
        } catch (SQLException e) {
            System.out.println("❌ Eroare creare tabele: " + e.getMessage());
        }
    }

    // 3. SALVAREA JOCULUI
    public static void saveGame(PlayerCharacter player, int locationId) {
        String sql = "INSERT OR REPLACE INTO player_save(id, name, class_type, trait, level, exp, gold, " +
                "current_hp, max_hp, current_mana, max_mana, current_stamina, max_stamina, " +
                "strength, intelligence, defense, speed, skill_points, location_id) " +
                "VALUES(1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Completăm semnele de întrebare (?) cu datele din obiectul player
            pstmt.setString(1, player.getName());
            pstmt.setString(2, player.getClass().getSimpleName()); // "Mage", "Knight" etc.
            pstmt.setString(3, player.getTrasatura());
            pstmt.setInt(4, player.getLevel());
            pstmt.setInt(5, player.getExperience());
            pstmt.setInt(6, player.getGold());

            pstmt.setInt(7, player.getHealthPoints());
            pstmt.setInt(8, player.getMaxHealth());
            pstmt.setInt(9, player.getActiveMana());
            pstmt.setInt(10, player.getBaseMana());
            pstmt.setInt(11, player.getActiveStamina());
            pstmt.setInt(12, player.getBaseStamina());

            // Accesăm atributele active
            pstmt.setInt(13, player.getActiveStrength());
            pstmt.setInt(14, player.getActiveInteligence());
            pstmt.setInt(15, player.getActiveDefense());
            pstmt.setInt(16, player.getActiveSpeed());

            pstmt.setInt(17, player.getSkillPoints());
            pstmt.setInt(18, locationId);

            pstmt.executeUpdate();
            System.out.println("💾 Joc Salvat cu Succes!");

        } catch (SQLException e) {
            System.out.println("❌ Eroare la salvare: " + e.getMessage());
        }
    }

    // 4. CLASA PENTRU ÎNCĂRCAREA DATELOR (Un simplu container)
    public static class SaveData {
        public boolean exists = false;
        public String name;
        public String classType;
        public String trait;
        public int level, exp, gold, hp, maxHp, mana, maxMana, stamina, maxStamina;
        public int str, intel, def, spd, skillPoints, locationId;
    }

    // 5. ÎNCĂRCAREA JOCULUI
    public static SaveData loadGame() {
        SaveData data = new SaveData();
        String sql = "SELECT * FROM player_save WHERE id = 1";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                data.exists = true;
                data.name = rs.getString("name");
                data.classType = rs.getString("class_type");
                data.trait = rs.getString("trait");
                data.level = rs.getInt("level");
                data.exp = rs.getInt("exp");
                data.gold = rs.getInt("gold");
                data.hp = rs.getInt("current_hp");
                data.maxHp = rs.getInt("max_hp");
                data.mana = rs.getInt("current_mana");
                data.maxMana = rs.getInt("max_mana");
                data.stamina = rs.getInt("current_stamina");
                data.maxStamina = rs.getInt("max_stamina");
                data.str = rs.getInt("strength");
                data.intel = rs.getInt("intelligence");
                data.def = rs.getInt("defense");
                data.spd = rs.getInt("speed");
                data.skillPoints = rs.getInt("skill_points");
                data.locationId = rs.getInt("location_id");
                System.out.println("📂 Date încărcate pentru: " + data.name);
            }

        } catch (SQLException e) {
            System.out.println("❌ Eroare la încărcare: " + e.getMessage());
        }
        return data;
    }
}