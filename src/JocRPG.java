// Importurile necesare (inclusiv cele noi)
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane; // <-- IMPORT NOU
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

import java.util.HashMap; // <-- IMPORT NOU
import java.util.ArrayList; // <-- IMPORT NOU (dacă nu e deja)
import java.util.Arrays;  // <-- IMPORT NOU
import javafx.scene.control.TextArea;

import javafx.application.Platform;

public class JocRPG extends Application {

    // --- Variabile Globale ---
    // Aici este locul corect pentru TOATE variabilele globale
    private Stage fereastraPrincipala;
    private String numeJucator;
    private String trasaturaJucator;
    private Enemy currentEnemy;

    // Dimensiunile ferestrei
    private final int LATIME_FEREASTRA = 800;
    private final int INALTIME_FEREASTRA = 600;

    // --- Variabilele NOI pentru motorul jocului ---
    private PlayerCharacter jucator; // Va stoca obiectul jucătorului nostru
    private HashMap<Integer, Location> worldMap; // Va stoca toate locațiile


    public static void main(String[] args) {
        // --- TEST BAZĂ DE DATE ---
        DatabaseManager.createNewDatabase();
        // -------------------------

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        fereastraPrincipala = primaryStage;
        fereastraPrincipala.setTitle("Regele Cenușiu - RPG");

        // Începem cu Meniul Principal, nu cu Prologul!
        showMainMenu();
    }

    // --- MENIUL PRINCIPAL (START SCREEN) ---
    private void showMainMenu() {
        // 1. Layout simplu, centrat
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #0d0d0d;"); // Aproape negru, elegant

        // 2. Titlul Jocului
        Label titleLabel = new Label("REGATUL CENUȘIU");
        titleLabel.setFont(new Font("Georgia", 48));
        titleLabel.setTextFill(Color.web("#a3a3a3")); // Un gri argintiu
        titleLabel.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        // 3. Subtitlu (opțional)
        Label subtitleLabel = new Label("O poveste despre alegere și sacrificiu");
        subtitleLabel.setFont(new Font("Arial", 16));
        subtitleLabel.setTextFill(Color.GRAY);

        // 4. Spațiu gol
        Label spacer = new Label("");
        spacer.setMinHeight(30);

        // 5. Buton: CONTINUĂ
        Button btnContinue = createContinueButton("CONTINUĂ");
        // Verificăm dacă există o salvare validă în baza de date
        DatabaseManager.SaveData data = DatabaseManager.loadGame();
        if (data.exists) {
            btnContinue.setOnAction(e -> loadGameFromDB());
        } else {
            // Dacă nu există salvare, dezactivăm butonul și schimbăm textul
            btnContinue.setDisable(true);
            btnContinue.setText("CONTINUĂ (Nu există salvare)");
            btnContinue.setStyle("-fx-background-color: #333333; -fx-text-fill: #555555;");
        }

        // 6. Buton: JOC NOU
        Button btnNewGame = createContinueButton("JOC NOU");
        btnNewGame.setOnAction(e -> {
            // Aici am putea pune o alertă: "Ești sigur? Vei șterge salvarea veche!"
            // Dar momentan, pornim direct prologul

            // Resetăm variabilele globale ca să fim siguri că începem de la zero
            this.jucator = null;
            this.currentEnemy = null;
            this.worldMap = null;

            showScene1_Harta();
        });

        // 7. Buton: IEȘIRE
        Button btnExit = createContinueButton("IEȘIRE");
        btnExit.setOnAction(e -> {
            Platform.exit(); // Închide aplicația JavaFX corect
            System.exit(0);  // Oprește tot procesul Java
        });

        // Adăugăm totul în scenă
        root.getChildren().addAll(titleLabel, subtitleLabel, spacer, btnContinue, btnNewGame, btnExit);

        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
        fereastraPrincipala.show();
    }

    // --- (METODĂ MODIFICATĂ) FIȘA PERSONAJULUI (ACUM POP-UP) ---
    // --- (METODĂ ACTUALIZATĂ) FIȘA PERSONAJULUI CU BUTOANE [+] ---
    // --- (METODĂ NOUĂ - FIXED) FIȘA PERSONAJULUI ---
    private void showCharacterScreen(Location lastLocation) {
        // 1. Creăm fereastra O SINGURĂ DATĂ
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(fereastraPrincipala);
        popupStage.setTitle("Fișa Personajului");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1E1E1E;");
        root.setPadding(new Insets(15));

        // 2. Desenăm conținutul inițial
        drawStatsContent(root, popupStage);

        // 3. Afișăm fereastra (O singură dată!)
        Scene charScene = new Scene(root, 600, 650);
        popupStage.setScene(charScene);
        popupStage.showAndWait();
    }

    // --- (METODĂ NOUĂ) Desenează conținutul (se apelează la fiecare [+]) ---
    private void drawStatsContent(BorderPane root, Stage popupStage) {
        // CURĂȚĂM TOT ce era înainte în fereastră
        root.setTop(null);
        root.setCenter(null);
        root.setBottom(null);

        // --- Partea de Sus ---
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.TOP_LEFT);

        Label title = createNaratorLabel(jucator.getName() + " - Nivel " + jucator.getLevel() + " " + jucator.getClass().getSimpleName());
        title.setFont(new Font("Georgia", 24));

        Label xpLabel = createNaratorLabel(String.format("XP: %d / %d", jucator.getExperience(), jucator.getExpNeeded()));
        Label traitLabel = createNaratorLabel("Trăsătură: " + jucator.getTrasatura());
        traitLabel.setStyle("-fx-text-fill: #e0aaff;");

        Label skillPointsLabel = createNaratorLabel("PUNCTE DE ALOCAT: " + jucator.getSkillPoints());
        skillPointsLabel.setFont(new Font("Georgia", 20));
        skillPointsLabel.setStyle("-fx-text-fill: #FFD700;"); // Auriu

        topBox.getChildren().addAll(title, xpLabel, traitLabel, skillPointsLabel, createNaratorLabel("\n--- Atribute ---"));
        root.setTop(topBox);

        // --- Partea de Mijloc (Grid cu Butoane) ---
        GridPane attributeGrid = new GridPane();
        attributeGrid.setHgap(15);
        attributeGrid.setVgap(10);
        attributeGrid.setPadding(new Insets(10, 0, 10, 0));

        boolean hasPoints = jucator.getSkillPoints() > 0;

        // Adăugăm rândurile manual aici pentru a evita confuzia cu alte metode
        addStatRow(attributeGrid, 0, "Viață (HP):", String.format("%d / %d", jucator.getHealthPoints(), jucator.getMaxHealth()), "HEALTH", hasPoints, root, popupStage);
        addStatRow(attributeGrid, 1, "Mana:", String.format("%d / %d", jucator.getActiveMana(), jucator.getBaseMana()), "MANA", hasPoints, root, popupStage);
        addStatRow(attributeGrid, 2, "Stamina:", String.format("%d / %d", jucator.getActiveStamina(), jucator.getBaseStamina()), "STAMINA", hasPoints, root, popupStage);
        addStatRow(attributeGrid, 3, "Forță:", String.valueOf(jucator.getActiveStrength()), "STRENGTH", hasPoints, root, popupStage);
        addStatRow(attributeGrid, 4, "Inteligență:", String.valueOf(jucator.getActiveInteligence()), "INTELLIGENCE", hasPoints, root, popupStage);
        addStatRow(attributeGrid, 5, "Apărare:", String.valueOf(jucator.getActiveDefense()), "DEFENSE", hasPoints, root, popupStage);
        addStatRow(attributeGrid, 6, "Viteză:", String.valueOf(jucator.getActiveSpeed()), "SPEED", hasPoints, root, popupStage);

        root.setCenter(attributeGrid);

        // --- Partea de Jos ---
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);

        Label statsGold = createNaratorLabel("Aur: " + jucator.getGold());
        Button backButton = createContinueButton("Închide");

        // AICI se închide fereastra cu adevărat
        backButton.setOnAction(e -> popupStage.close());

        bottomBox.getChildren().addAll(statsGold, backButton);
        root.setBottom(bottomBox);
    }

    // --- (METODĂ AJUTĂTOARE) Creează un rând și butonul [+] ---
    private void addStatRow(GridPane grid, int row, String labelText, String value, String statName, boolean hasPoints, BorderPane root, Stage popupStage) {
        Label statLabel = createNaratorLabel(labelText + " " + value);
        statLabel.setTextAlignment(TextAlignment.LEFT);

        Button addButton = new Button("[+]");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        addButton.setDisable(!hasPoints);

        addButton.setOnAction(e -> {
            // 1. Cheltuim punctul
            boolean success = jucator.spendSkillPoint(statName);

            if(success) {
                // 2. NU închidem fereastra!
                // 3. Doar redesenăm conținutul pe aceiași fereastră
                drawStatsContent(root, popupStage);
            }
        });

        grid.add(statLabel, 0, row);
        grid.add(addButton, 1, row);
    }


    // --- METODĂ AJUTĂTOARE PENTRU TABELUL DE STATISTICI ---
    private void addAttributeRow(GridPane grid, int row, String labelText, String value, String statName, Location lastLocation, boolean hasPoints, Stage currentStage) {
        // Textul atributului
        Label statLabel = createNaratorLabel(labelText + " " + value);
        statLabel.setTextAlignment(TextAlignment.LEFT);

        // Butonul [+]
        Button addButton = new Button("[+]");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        // Dacă nu ai puncte, butonul e dezactivat (gri)
        addButton.setDisable(!hasPoints);

        // Acțiunea butonului
        addButton.setOnAction(e -> {
            boolean success = jucator.spendSkillPoint(statName);
            if(success) {
                // Dacă am cheltuit punctul cu succes, închidem fereastra și o redeschidem
                // pentru a vedea noile valori actualizate
                currentStage.close();
                showCharacterScreen(lastLocation);
            }
        });

        // Adăugăm în tabel: Coloana 0 (Text), Coloana 1 (Buton)
        grid.add(statLabel, 0, row);
        grid.add(addButton, 1, row);
    }

    // --- (METODĂ AJUTĂTOARE NOUĂ) Reîmprospătează lista de obiecte din inventar ---
    private void refreshInventoryList(VBox itemsBox, Location lastLocation, Stage popupStage) {

        // 1. GOLIM lista veche de butoane
        itemsBox.getChildren().clear();

        // 2. Luăm lista actualizată de obiecte
        ArrayList<Item> inventar = jucator.getInventory();

        if (inventar.isEmpty()) {
            itemsBox.getChildren().add(createNaratorLabel("Inventarul este gol."));
        } else {
            // 3. Re-creăm butoanele pentru fiecare obiect
            for (Item item : inventar) {
                String itemText = item.getName();

                if (item instanceof Equipment) {
                    Equipment eq = (Equipment) item;
                    if (eq == jucator.getEquippedMainhand() || eq == jucator.getEquippedOffhand() ||
                            eq == jucator.getEquippedHelmet() || eq == jucator.getEquippedChest() ||
                            eq == jucator.getEquippedBoots() || eq == jucator.getEquippedAccessory1() ||
                            eq == jucator.getEquippedAccessory2())
                    {
                        itemText += "  - ECHIPAT";
                    }
                }

                Button itemButton = new Button(itemText);
                itemButton.setFont(new Font("Arial", 16));
                itemButton.setMaxWidth(Double.MAX_VALUE);
                itemButton.setAlignment(Pos.CENTER_LEFT);
                itemButton.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-cursor: hand;");

                // 4. Definim acțiunea butonului (AICI E SCHIMBAREA)
                itemButton.setOnAction(e -> {
                    // ACUM, în loc să folosim obiectul,
                    // ARĂTĂM ECRANUL DE DETALII pentru acel obiect
                    showItemDetailView(item, itemsBox, lastLocation, popupStage);
                });

                itemsBox.getChildren().add(itemButton);
            }
        }
    }

    // --- (METODĂ NOUĂ) Arată detaliile unui singur obiect ---
    private void showItemDetailView(Item item, VBox itemsBox, Location lastLocation, Stage popupStage) {

        // 1. GOLIM lista de obiecte pentru a face loc detaliilor
        itemsBox.getChildren().clear();

        // 2. Creăm etichete pentru informațiile de bază
        Label nameLabel = createNaratorLabel(item.getName());
        nameLabel.setFont(new Font("Georgia", 22));
        nameLabel.setTextAlignment(TextAlignment.LEFT);

        Label descLabel = createNaratorLabel(item.getDescription());
        descLabel.setTextAlignment(TextAlignment.LEFT);

        Label valueLabel = createNaratorLabel("Valoare: " + item.getValue() + " aur");
        valueLabel.setTextAlignment(TextAlignment.LEFT);

        itemsBox.getChildren().addAll(nameLabel, descLabel, valueLabel, createNaratorLabel("---")); // Adăugăm un separator

        // 3. Verificăm tipul obiectului și adăugăm butoane + statistici specifice

        if (item instanceof Equipment) {
            Equipment eq = (Equipment) item;

            // Creăm un GridPane pentru a alinia frumos statisticile
            GridPane statsGrid = new GridPane();
            statsGrid.setHgap(10);
            statsGrid.setVgap(5);

            // Adăugăm doar statisticile care nu sunt 0
            int row = 0;
            if (eq.getHealthBonus() != 0) statsGrid.add(createNaratorLabel("Viață: +" + eq.getHealthBonus()), 0, row++);
            if (eq.getManaBonus() != 0) statsGrid.add(createNaratorLabel("Mana: +" + eq.getManaBonus()), 0, row++);
            if (eq.getStaminaBonus() != 0) statsGrid.add(createNaratorLabel("Stamină: +" + eq.getStaminaBonus()), 0, row++);
            if (eq.getStrengthBonus() != 0) statsGrid.add(createNaratorLabel("Forță: +" + eq.getStrengthBonus()), 0, row++);
            if (eq.getIntelligenceBonus() != 0) statsGrid.add(createNaratorLabel("Inteligență: +" + eq.getIntelligenceBonus()), 0, row++);
            if (eq.getDefenseBonus() != 0) statsGrid.add(createNaratorLabel("Apărare: +" + eq.getDefenseBonus()), 0, row++);
            if (eq.getSpeedBonus() != 0) statsGrid.add(createNaratorLabel("Viteză: +" + eq.getSpeedBonus()), 0, row++);

            itemsBox.getChildren().add(statsGrid); // Adăugăm tabelul de statistici

            // Verificăm dacă e echipat pentru a schimba textul butonului
            boolean isEquipped = (eq == jucator.getEquippedMainhand() || eq == jucator.getEquippedOffhand() ||
                    eq == jucator.getEquippedHelmet() || eq == jucator.getEquippedChest() ||
                    eq == jucator.getEquippedBoots() || eq == jucator.getEquippedAccessory1() ||
                    eq == jucator.getEquippedAccessory2());

            String buttonText = isEquipped ? "Dezechipați" : "Echipați";
            Button equipButton = createContinueButton(buttonText);

            equipButton.setOnAction(e -> {
                item.useItem(jucator); // Folosim logica de toggle din PlayerCharacter
                refreshInventoryList(itemsBox, lastLocation, popupStage); // Ne întoarcem la listă
            });
            itemsBox.getChildren().add(equipButton);

        } else if (item instanceof Potion) {
            // Pentru poțiuni, adăugăm doar butonul "Folosește"
            Button useButton = createContinueButton("Folosește");
            useButton.setOnAction(e -> {
                item.useItem(jucator); // Poțiunea va fi consumată
                refreshInventoryList(itemsBox, lastLocation, popupStage); // Ne întoarcem la listă
            });
            itemsBox.getChildren().add(useButton);
        }

        // 4. Adăugăm un buton de "Înapoi"
        Button backButton = createContinueButton("Înapoi la Inventar");
        backButton.setOnAction(e -> {
            // Doar reîmprospătăm lista, ceea ce ne duce înapoi
            refreshInventoryList(itemsBox, lastLocation, popupStage);
        });
        itemsBox.getChildren().add(backButton);
    }

    // --- (METODĂ ACTUALIZATĂ) ECRANUL DE INVENTAR (Pop-up) ---
    private void showInventoryScreen(Location lastLocation) {

        // 1. Creăm fereastra pop-up (o singură dată)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(fereastraPrincipala);
        popupStage.setTitle("Inventar");

        // 2. Creăm layout-ul principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1E1E1E;");
        root.setPadding(new Insets(15));

        Label title = createNaratorLabel("Inventar");
        title.setFont(new Font("Georgia", 24));
        root.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        // 3. Creăm containerul pentru Scroll și containerul pentru Iteme
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1E1E1E; -fx-background-color: #1E1E1E;");

        VBox itemsBox = new VBox(10); // Aceasta este "cutia" pe care o vom reîmprospăta
        itemsBox.setPadding(new Insets(10));
        itemsBox.setStyle("-fx-background-color: #1E1E1E;");

        // 4. Punem cutia în scroll și scroll-ul în centrul ferestrei
        scrollPane.setContent(itemsBox);
        root.setCenter(scrollPane);

        // 5. Creăm butonul de Închidere
        Button backButton = createContinueButton("Închide");
        backButton.setOnAction(e -> popupStage.close()); // Acțiunea e simplă: închide fereastra
        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);

        // 6. CHEMAREA INIȚIALĂ: Umplem inventarul pentru prima dată
        refreshInventoryList(itemsBox, lastLocation, popupStage);

        // 7. Afișăm fereastra
        Scene scene = new Scene(root, 600, 550);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    // --- SCENA 1: HARTA ---
    private void showScene1_Harta() {
        VBox root = createDefaultVBox();

        Label naratorLabel = createNaratorLabel(
                "(Pe ecran apare o hartă veche, arsă pe margini. O voce gravă se aude.)\n\n" +
                        "Narator: Regatul Eldoriei. Timp de o mie de ani, o epocă de aur clădită pe o minciună."
        );

        Button continueButton = createContinueButton("...");
        continueButton.setOnAction(e -> showScene2_Regatul());

        root.getChildren().addAll(naratorLabel, continueButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
        fereastraPrincipala.show();
    }

    // --- SCENA 2: DEZVOLTAREA REGATULUI ---
    private void showScene2_Regatul() {
        VBox root = createDefaultVBox();

        Label naratorLabel = createNaratorLabel(
                "(Camera face zoom pe hartă, arătând Regatul în detaliu.)\n\n" +
                        "Narator: Oamenii și-au ridicat orașe de marmură și turnuri de fildeș, convinși că pacea le aparține. Au uitat de sacrificiul pe care a fost clădită."
        );

        Button continueButton = createContinueButton("...");
        continueButton.setOnAction(e -> showScene3_Dizolvarea());

        root.getChildren().addAll(naratorLabel, continueButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- SCENA 3: DIZOLVAREA ÎN CÂRCIUMĂ ---
    private void showScene3_Dizolvarea() {
        VBox root = createDefaultVBox();

        Label naratorLabel = createNaratorLabel(
                "(Imaginea hărții se dizolvă lent, tranziționând spre interiorul zgomotos și plin de fum al unei cârciumi. E noapte, plouă.)\n\n" +
                        "Narator: Ei au uitat. Dar pământul... pământul nu uită niciodată."
        );

        Button continueButton = createContinueButton("...");
        continueButton.setOnAction(e -> showScene4_Taverna());

        root.getChildren().addAll(naratorLabel, continueButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- SCENA 4: TAVERNA ---
    private void showScene4_Taverna() {
        VBox root = createDefaultVBox();

        Text naratorText1 = createText("Narator: În taverna \"La Ulciorul Spintecat\", la marginea lumii civilizate, poveștile vechi încă se mai spun în șoaptă.\n\n", Color.LIGHTGRAY);
        Text batranText = createText("Bătrân (lovind cu pumnul în masă):", Color.YELLOW);
        Text batranDialog = createText(" Vă spun eu! Sigiliul slăbește! Regele Cenușiu s-a agitat în mormântul lui de piatră!\n\n", Color.WHITE);
        Text mercenarText = createText("Mercenar (râzând):", Color.CYAN);
        Text mercenarDialog = createText(" Iar bei povești, Hrolf? Regele Cenușiu e o sperietoare pentru copii. Sigiliul ne-a dat o mie de ani de...\n\n", Color.WHITE);
        Text naratorText2 = createText("(Mercenarul îngheață. Râsul i se oprește în gât. Toată cârciuma tace.)\n(Mirosul. Un miros de praf vechi și putregai uscat...)", Color.LIGHTGRAY);

        TextFlow textFlow = new TextFlow(naratorText1, batranText, batranDialog, mercenarText, mercenarDialog, naratorText2);
        textFlow.setTextAlignment(TextAlignment.CENTER);

        Button continueButton = createContinueButton("Ce se întâmplă?!");
        continueButton.setOnAction(e -> showScene5_Viziunea());

        root.getChildren().addAll(textFlow, continueButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- SCENA 5: VIZIUNEA VIITORULUI (FLASH-FORWARD) ---
    private void showScene5_Viziunea() {
        VBox root = createDefaultVBox();

        Label naratorLabel = createNaratorLabel(
                "(Dintr-o dată, ecranul arată imagini rapide, violente: câmpuri arzând, orașe în ruină, oameni transformați urlând spre un cer cenușiu.)\n\n" +
                        "Narator: O viziune a viitorului... frică... neputință... masacru."
        );

        Button continueButton = createContinueButton("...");
        continueButton.setOnAction(e -> showScene6_EcranNegru());

        root.getChildren().addAll(naratorLabel, continueButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- SCENA 6: ECRANUL NEGRU (Tranziția) ---
    private void showScene6_EcranNegru() {
        VBox root = createDefaultVBox();
        root.setStyle("-fx-background-color: #000000;"); // Ecran complet negru

        Label naratorLabel = createNaratorLabel(
                "(Ecranul se stinge.)\n\n" +
                        "(...o respirație...)\n\n" +
                        "(...ciripit de păsări...)"
        );

        Button continueButton = createContinueButton("...");
        continueButton.setOnAction(e -> showScene7_Satul());

        root.getChildren().addAll(naratorLabel, continueButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- SCENA 7: SATUL (Crearea Personajului) ---
    private void showScene7_Satul() {
        fereastraPrincipala.setTitle("Cine ești tu?");
        VBox root = createDefaultVBox();
        root.setAlignment(Pos.CENTER_LEFT);

        Label storyLabel = createNaratorLabel(
                "(Imaginea se aprinde. Soare. Un sat liniștit, \"Valea Iepurelui\".)\n\n" +
                        "Narator: Dar nu aici. Încă nu. Aici, în cel mai liniștit sat din regat, trăiești tu.\n\n" +
                        "Ești cunoscut în sat ca vânătorul de recompense \"împiedicat\". Săptămâna trecută, ai fost trimis să prinzi niște goblini și te-ai întors cu trei găini furioase și un stup de albine."
        );
        storyLabel.setTextAlignment(TextAlignment.LEFT);

        Label nameLabel = createNaratorLabel("Narator: Care este numele tău?");
        nameLabel.setTextAlignment(TextAlignment.LEFT);

        TextField nameInput = new TextField();
        nameInput.setMaxWidth(300);
        nameInput.setFont(new Font("Arial", 14));
        nameInput.setPromptText("Introdu numele aici...");

        Label traitLabel = createNaratorLabel("Narator: ...Și care este trăsătura ta cea mai... \"cunoscută\"?");
        traitLabel.setTextAlignment(TextAlignment.LEFT);

        ToggleGroup traitGroup = new ToggleGroup();
        RadioButton trait1 = createRadioButton("Ghinionist Cronic (-1 Noroc, +XP din eșecuri)", "Ghinionist Cronic");
        trait1.setToggleGroup(traitGroup);
        RadioButton trait2 = createRadioButton("Gurmandul Satului (+5 Viață, vindecare dublă)", "Gurmandul Satului");
        trait2.setToggleGroup(traitGroup);
        RadioButton trait3 = createRadioButton("Inima Zdrobită (Imun la \"farmec\")", "Inima Zdrobită");
        trait3.setToggleGroup(traitGroup);
        trait1.setSelected(true);

        Button confirmButton = createContinueButton("Confirmă");

        confirmButton.setOnAction(e -> {
            numeJucator = nameInput.getText();
            if (numeJucator.isEmpty()) {
                numeJucator = "Necunoscutul";
            }

            RadioButton selectedTrait = (RadioButton) traitGroup.getSelectedToggle();
            trasaturaJucator = selectedTrait.getUserData().toString();

            System.out.println("Nume Jucător: " + numeJucator);
            System.out.println("Trăsătură: " + trasaturaJucator);

            // Trecem la scena cu nestemata
            showScene8_GemSecret();
        });

        root.getChildren().addAll(storyLabel, nameLabel, nameInput, traitLabel, trait1, trait2, trait3, confirmButton);
        Scene creationScene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(creationScene);
    }

    // --- SCENA 8: SECRETUL (Nestemata) ---
    private void showScene8_GemSecret() {
        fereastraPrincipala.setTitle("Secretul Tău");
        VBox root = createDefaultVBox();

        Label naratorLabel = createNaratorLabel(
                "Narator: Dar ai un secret. Acum o lună, în timp ce urmăreai un bursuc (care ți-a furat prânzul), ai alunecat și ai căzut într-o grotă ascunsă.\n\n" +
                        "Era o ruină. Și pe un altar mic, prăfuit, ai găsit-o: o nestemată ciudată, ca un cristal lăptos, care părea să pulseze cu o lumină caldă.\n\n" +
                        "Ai păstrat-o. O porți la gât, sub cămașă. A fost singurul tău noroc real."
        );

        Button continueButton = createContinueButton("Acum stai în fața casei tale...");
        continueButton.setOnAction(e -> showScene9_BlightAttack());

        root.getChildren().addAll(naratorLabel, continueButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- SCENA 9: ATACUL (Molima) ---
    private void showScene9_BlightAttack() {
        fereastraPrincipala.setTitle("A Sosit!");
        VBox root = createDefaultVBox();

        Label naratorLabel = createNaratorLabel(
                "Narator: Te afli în fața casei tale. Mirosul de cenușă ajunge și în Valea Iepurelui. O ceață gri, nenaturală, se rostogolește pe uliță.\n\n" +
                        "Molima Cenușie. Te-a găsit.\n\n" +
                        "(Ceața te învăluie. O durere cumplită îți sfâșie trupul. Simți cum sângele îți fierbe și cum mintea începe să ți se rupă... Transformarea începe...)"
        );

        Button continueButton = createContinueButton("...!!!...");
        continueButton.setOnAction(e -> showScene10_PrimaryChoice());

        root.getChildren().addAll(naratorLabel, continueButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- SCENA 10: ALEGEREA PRIMARĂ (Magie vs Oțel) ---
    private void showScene10_PrimaryChoice() {
        fereastraPrincipala.setTitle("Alegerea");
        VBox root = createDefaultVBox();

        Label naratorLabel = createNaratorLabel(
                "Narator: ...DAR!\n\n" +
                        "(Un sunet clar, ca de cristal. Nestemata de la gâtul tău explodează într-o lumină albă, pură! Piatra luptă cu molima... o filtrează. Puterea demonică este curățată de răutate, lăsând în urmă doar energia brută.)\n\n" +
                        "Narator: Simți cum puterea haotică se stabilizează în tine, așteptând o comandă. Ți-ai păstrat umanitatea, dar transformarea este inevitabilă. \n\nTREBUIE SĂ ALEGI cum te va schimba această energie!"
        );

        Button spiritButton = createContinueButton("[Calea Spiritului] (Îmbrățișezi magia)");
        spiritButton.setOnAction(e -> startGame("Mage"));

        Button steelButton = createContinueButton("[Calea Oțelului] (Te bazezi pe trup)");
        steelButton.setOnAction(e -> showScene11_WarriorSpecialization());

        root.getChildren().addAll(naratorLabel, spiritButton, steelButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- SCENA 11: SPECIALIZAREA (Warrior) ---
    private void showScene11_WarriorSpecialization() {
        fereastraPrincipala.setTitle("Specializarea");
        VBox root = createDefaultVBox();

        Label naratorLabel = createNaratorLabel(
                "Narator: Ai ales calea fizică. Puterea îți inundă trupul, făcându-ți oasele mai puternice și reflexele mai ascuțite.\n\n" +
                        "Dar simți că această putere brută este nefinisată... Cum o vei rafina?"
        );

        Button knightButton = createContinueButton("[Calea Cavalerului] (Mă voi baza pe rezistență și apărare.)");
        knightButton.setOnAction(e -> startGame("Knight"));

        Button assassinButton = createContinueButton("[Calea Asasinului] (Mă voi baza pe viteză și viclenie.)");
        assassinButton.setOnAction(e -> startGame("Assassin"));

        Button archerButton = createContinueButton("[Calea Arcașului] (Mă voi baza pe precizie și distanță.)");
        archerButton.setOnAction(e -> startGame("Archer"));

        // --- BUTONUL NOU DE ÎNTOARCERE ---
        Button backButton = createContinueButton("«« Înapoi (Vreau Calea Spiritului)");
        backButton.setOnAction(e -> showScene10_PrimaryChoice()); // Se întoarce la scena anterioară
        // --- SFÂRȘIT COD NOU ---

        // Adăugăm toate butoanele, inclusiv cel nou
        root.getChildren().addAll(
                naratorLabel,
                knightButton,
                assassinButton,
                archerButton,
                createNaratorLabel("---"), // Un separator vizual
                backButton
        );
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // ==================================================================
    // --- AICI ÎNCEPE NOUL COD ---
    // ==================================================================

    // --- SCENA FINALĂ: ÎNCEPUTUL JOCULUI (Metoda ÎNLOCUITĂ) ---
    private void startGame(String finalClass) {
        // În metoda startGame din JocRPG.java

        // 1. CREAREA OBIECTULUI JUCĂTOR + SKILL-URI DE START
        switch (finalClass) {
            case "Mage":
                this.jucator = new Mage(numeJucator);
                // Magul primește atac magic și un heal
                jucator.learnSkill(new Skill("Mingea de Foc", "O sferă de foc pur.", 15, 0, 30, "MAGICAL"));
                jucator.learnSkill(new Skill("Vindecare", "Reface viața.", 20, 0, 25, "HEAL"));
                break;

            case "Knight":
                this.jucator = new Knight(numeJucator);
                // Cavalerul lovește tare fizic
                jucator.learnSkill(new Skill("Lovitură de Scut", "O lovitură brutală.", 0, 15, 20, "PHYSICAL"));
                jucator.learnSkill(new Skill("Atac Rapid", "O lovitură iute.", 0, 5, 10, "PHYSICAL"));
                break;

            case "Assassin":
                this.jucator = new Assassin(numeJucator);
                // Asasinul consumă multă stamina pentru damage mare
                jucator.learnSkill(new Skill("Înjunghiere", "Atac letal.", 0, 25, 40, "PHYSICAL"));
                break;

            case "Archer":
                this.jucator = new Archer(numeJucator);
                // Arcașul e echilibrat
                jucator.learnSkill(new Skill("Săgeată Țintită", "Lovitură precisă.", 0, 10, 15, "PHYSICAL"));
                break;

            default:
                this.jucator = new Warrior(numeJucator);
                jucator.learnSkill(new Skill("Lovitură", "Atac de bază.", 0, 5, 8, "PHYSICAL"));
        }

        // 2. SETAREA TRĂSĂTURII
        // (Asigură-te că în PlayerCharacter.java ai metoda 'setTrasatura')
        this.jucator.setTrait(trasaturaJucator);

        // --- ADĂUGĂM OBIECTE DE START PENTRU TEST ---

        // O poțiune
        Potion startPotion = new Potion("Poțiune Mică de Viață", "Vindecă 30 HP", 10, "HP", 30);

        // O armă cu 1 MÂNĂ
        Equipment startSword = new Equipment("Sabie Ruginită", "O sabie veche.", 5,
                "MAINHAND", "ONE_HANDED", // <-- AICI E MODIFICAREA
                0, 0, 0, 2, 0, 0, 0); // +2 Forță

        // Un obiect OFF_HAND
        Equipment startShield = new Equipment("Scut de Lemn", "Câteva scânduri.", 3,
                "OFFHAND", "OFF_HAND", // <-- OBIECT NOU
                5, 0, 0, 0, 0, 3, 0); // +5 Viață, +3 Apărare

        // O armă cu 2 MÂINI
        Equipment startSpear = new Equipment("Lance de Vânătoare", "Un băț ascuțit.", 8,
                "MAINHAND", "TWO_HANDED", // <-- OBIECT NOU
                0, 0, 5, 3, 0, 0, 1); // +5 Stamină, +3 Forță, +1 Viteză

        jucator.addItem(startPotion);
        jucator.addItem(startSword);
        jucator.addItem(startShield);
        jucator.addItem(startSpear);
        // --- SFÂRȘIT OBIECTE DE START ---


        // VERIFICARE în consolă
        System.out.println("Jucător creat: " + this.jucator.getName() + " - Clasa: " + this.jucator.getClass().getSimpleName());

        // 3. CREAREA LUMII
        createWorldMap();

        // 4. PORNIREA ECRANULUI DE JOC
        // Începem jocul prin a încărca Locația 0
        Location primaLocatie = worldMap.get(0);

        // (Vom avea nevoie de gettere în GameEntity/PlayerCharacter pentru statistici)
        showGameScreen(primaLocatie);
    }

    // --- (METODĂ NOUĂ) CREAREA LUMII ---
    private void createWorldMap() {
        this.worldMap = new HashMap<>();

        // --- DEFINIȚIA LUMII ---

        // LOCAȚIA 0: Satul (Punctul de start)
        ArrayList<String> options0 = new ArrayList<>(Arrays.asList(
                "1. Mergi spre pădurea de la marginea satului.",
                "2. Mergi spre primăria incendiată."
        ));
        ArrayList<Integer> links0 = new ArrayList<>(Arrays.asList(1, 2));
        Location loc0 = new Location(0,
                "Ești în piața centrală din \"Valea Iepurelui\". Mirosul de cenușă este peste tot. Fum se ridică dinspre primărie. Majoritatea sătenilor au fugit sau... s-au transformat. Ești singur.",
                options0, links0
        );

        // LOCAȚIA 1: Intrarea în Pădure
        ArrayList<String> options1 = new ArrayList<>(Arrays.asList(
                "1. Urmează cărarea spre nord.",
                "2. Întoarce-te în sat."
        ));
        ArrayList<Integer> links1 = new ArrayList<>(Arrays.asList(3, 0)); // Link spre loc 3 și 0
        Location loc1 = new Location(1,
                "Ai ajuns la marginea pădurii. Copacii par bolnavi, acoperiți de o mâzgă cenușie. O cărare abia vizibilă se afundă în întuneric.",
                options1, links1
        );

        // LOCAȚIA 2: Primăria
        ArrayList<String> options2 = new ArrayList<>(Arrays.asList(
                "1. Întoarce-te în piața centrală."
        ));
        ArrayList<Integer> links2 = new ArrayList<>(Arrays.asList(0)); // Doar link înapoi
        Location loc2 = new Location(2,
                "Clădirea primăriei este doar un morman de ruine fumegânde. Nu este nimic de valoare aici. Se pare că molima a lovit rapid.",
                options2, links2
        );

        // LOCAȚIA 3: Cărarea din Pădure (Spre un Inamic)
        ArrayList<String> options3 = new ArrayList<>(Arrays.asList(
                "1. ATACĂ creatura!",
                "2. Încearcă să fugi înapoi la intrarea în pădure."
        ));
        ArrayList<Integer> links3 = new ArrayList<>(Arrays.asList(9001, 1)); // 9001 = ID special pt luptă
        Location loc3 = new Location(3,
                "Mergi pe cărare când, deodată, un sunet gutural se aude din tufișuri. O Bestie Cenușie, un fost lup, cu ochii roșii și blana plină de țepi de os, îți blochează calea!",
                options3, links3
        );


        // Adăugăm locațiile create în Harta lumii
        worldMap.put(loc0.getID(), loc0);
        worldMap.put(loc1.getID(), loc1);
        worldMap.put(loc2.getID(), loc2);
        worldMap.put(loc3.getID(), loc3);
    }

    // --- (METODĂ NOUĂ) ECRANUL PRINCIPAL DE JOC ---
    private void showGameScreen(Location currentLocation) {
        fereastraPrincipala.setTitle("Regatul Cenușiu - " + currentLocation.getDescription().substring(0, 20) + "...");

        // 1. Layout-ul Principal (sus: statistici, centru: text, jos: alegeri)
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1E1E1E;");
        root.setPadding(new Insets(15));

        // 2. SUS: Afișarea Statisticilor Jucătorului
        Label statsLabel = new Label();
        statsLabel.setFont(new Font("Arial", 16));
        statsLabel.setStyle("-fx-text-fill: #A9A9A9;");

        // (Textul a fost corectat pentru a avea doar 8 locații pentru 8 variabile)
        statsLabel.setText(String.format(
                "Nume: %s | Clasa: %s \nHP: %d/%d | Mana: %d/%d | Stamina: %d/%d",
                this.jucator.getName(),
                this.jucator.getClass().getSimpleName(),
                this.jucator.getHealthPoints(), this.jucator.getMaxHealth(),
                this.jucator.getActiveMana(), this.jucator.getBaseMana(),
                this.jucator.getActiveStamina(), this.jucator.getBaseStamina()
        ));
        root.setTop(statsLabel);
        BorderPane.setAlignment(statsLabel, Pos.CENTER_LEFT);

        // 3. CENTRU: Descrierea Locației
        Label descriptionLabel = createNaratorLabel(currentLocation.getDescription());
        descriptionLabel.setTextAlignment(TextAlignment.LEFT);
        root.setCenter(descriptionLabel);
        BorderPane.setMargin(descriptionLabel, new Insets(20, 0, 20, 0)); // Spațiu sus/jos

        // 4. JOS: Butoanele pentru Alegeri
        VBox choicesBox = new VBox(10); // Container pentru butoane
        choicesBox.setAlignment(Pos.CENTER_LEFT);

        ArrayList<String> optionsText = currentLocation.getOptionsText();
        ArrayList<Integer> optionsLinks = currentLocation.getOptionsLinks();

        for (int i = 0; i < optionsText.size(); i++) {
            String optionString = optionsText.get(i);
            int nextLocationID = optionsLinks.get(i);

            Button choiceButton = createContinueButton(optionString);

            choiceButton.setOnAction(e -> {
                // --- AICI E NOUA LOGICĂ DE JOC ---
                if (nextLocationID == 9001) {
                    // ID Special pentru luptă (din Locația 3)

                    // 1. CREĂM INAMICUL
                    // Folosim noul tău constructor din Enemy.java
                    this.currentEnemy = new Enemy(
                            "Lup Rănit",  // Nume
                            50,           // maxHealth
                            4,            // baseDefense
                            12,           // baseSpeed
                            8,            // baseStrength
                            0,            // baseIntelligence
                            50,           // xpReward
                            10,           // goldReward
                            0, 0          // dropReward (ignorăm deocamdată)
                    );
                    System.out.println("Un " + currentEnemy.getName() + " apare!");

                    // 2. Apelăm noul ecran de luptă
                    // Îi spunem unde să se întoarcă DUPĂ luptă (Locația 1)
                    showBattleScreen(1);

                } else {
                    // Logică normală de navigare
                    Location nextLocation = worldMap.get(nextLocationID);
                    showGameScreen(nextLocation);
                }
            });

            choicesBox.getChildren().add(choiceButton);
        }

        root.setBottom(choicesBox);

        // 5. DREAPTA: Meniul Jocului (SIMPLIFICAT)

        // 1. Creăm meniul și îl salvăm în variabila 'menuBox'
        VBox menuBox = createSideMenu();

        // 2. Îl punem în dreapta
        root.setRight(menuBox);

        // 3. Acum linia asta va funcționa, pentru că 'menuBox' există din nou!
        BorderPane.setMargin(menuBox, new Insets(0, 0, 0, 20));

        // 6. Creăm și afișăm Scena (Asta e piesa ta de cod, acum la locul ei)
        Scene gameScene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(gameScene);
    }

    // ==================================================================
    // --- NOUL MOTOR DE LUPTĂ ---
    // ==================================================================

    // --- (METODĂ NOUĂ) ECRANUL DE LUPTĂ ---
    // --- (METODĂ ACTUALIZATĂ - DINAMICĂ) ECRANUL DE LUPTĂ ---
    private void showBattleScreen(int victoryLocationId) {
        fereastraPrincipala.setTitle("ÎN LUPTĂ CU: " + currentEnemy.getName());

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #3B1212;"); // Fundal roșu-închis
        root.setPadding(new Insets(15));

        // Jurnalul
        TextArea battleLog = new TextArea();
        battleLog.setEditable(false);
        battleLog.setFont(new Font("Monospaced", 14));
        battleLog.setStyle("-fx-text-fill: #E0E0E0; -fx-control-inner-background: #2B2B2B;");
        battleLog.setText("Lupta a început! Un " + currentEnemy.getName() + " se uită la tine!\n");
        root.setCenter(battleLog);

        // Statistici Inamic (Sus)
        Label enemyLabel = createNaratorLabel(
                currentEnemy.getName() + "\nHP: " + currentEnemy.getHealthPoints() + " / " + currentEnemy.getMaxHealth()
        );
        root.setTop(enemyLabel);

        // Container pentru butoane (Jos)
        VBox actionsBox = new VBox(10);
        actionsBox.setAlignment(Pos.CENTER);

        // Statistici Jucător
        Label playerStats = createNaratorLabel(String.format(
                "HP: %d/%d | Mana: %d/%d | Stamina: %d/%d",
                jucator.getHealthPoints(), jucator.getMaxHealth(),
                jucator.getActiveMana(), jucator.getBaseMana(),
                jucator.getActiveStamina(), jucator.getBaseStamina()
        ));
        playerStats.setFont(new Font("Arial", 16));
        actionsBox.getChildren().add(playerStats);

        // --- GENERAREA DINAMICĂ A BUTOANELOR DE SKILL ---
        // Aici este marea schimbare!
        for (Skill skill : jucator.getSkills()) {

            // Calculăm textul butonului (Nume + Cost)
            String buttonText = skill.getName();
            if (skill.getManaCost() > 0) {
                buttonText += " (" + skill.getManaCost() + " Mana)";
            }
            if (skill.getStaminaCost() > 0) {
                buttonText += " (" + skill.getStaminaCost() + " Stamina)";
            }

            Button skillButton = createContinueButton(buttonText);

            // Acțiunea butonului: Execută skill-ul specific
            skillButton.setOnAction(e -> {
                executeSkillTurn(skill, battleLog, victoryLocationId);
            });

            actionsBox.getChildren().add(skillButton);
        }
        // -------------------------------------------------

        // Butonul de Odihnă (rămâne fix, e mereu disponibil)
        Button restButton = createContinueButton("Odihnă (Reface resursele)");
        restButton.setOnAction(e -> {
            performRestTurn(battleLog, victoryLocationId);
        });
        actionsBox.getChildren().add(restButton);

        root.setBottom(actionsBox);

        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- (METODĂ NOUĂ) Logica pentru TURA JUCĂTORULUI ---
    // --- (METODĂ NOUĂ) Execută o tură folosind un Skill ---
    private void executeSkillTurn(Skill skill, TextArea log, int victoryId) {

        // 1. Verificăm Costurile
        if (skill.getManaCost() > 0 && !jucator.canUseMana(skill.getManaCost())) {
            log.appendText(">>> Nu ai destulă Mana pentru " + skill.getName() + "!\n");
            return; // Oprim tura, jucătorul trebuie să aleagă altceva
        }
        if (skill.getStaminaCost() > 0 && !jucator.canUseStamina(skill.getStaminaCost())) {
            log.appendText(">>> Nu ai destulă Stamina pentru " + skill.getName() + "!\n");
            return;
        }

        // 2. Plătim Costul
        if (skill.getManaCost() > 0) jucator.useMana(skill.getManaCost());
        if (skill.getStaminaCost() > 0) jucator.useStamina(skill.getStaminaCost());

        // 3. Aplicăm Efectul
        if (skill.getType().equals("HEAL")) {
            // Dacă e vrajă de vindecare, valoarea e pozitivă (cât vindecă)
            int healAmount = skill.getValue();
            jucator.heal(healAmount);
            log.appendText("Ai folosit " + skill.getName() + " și te-ai vindecat cu " + healAmount + " HP.\n");

        } else {
            // E atac (PHYSICAL sau MAGICAL)
            int damage = skill.getValue();
            // Aici am putea adăuga bonusuri din stats (ex: damage + strenght)
            // Deocamdată folosim valoarea de bază a skill-ului
            currentEnemy.takeDamage(damage);
            log.appendText("Ai folosit " + skill.getName() + "! Inamicul ia " + damage + " daune.\n");
        }

        // 4. Verificăm Victorie/Înfrângere și Tura Inamicului
        checkEndOfTurn(log, victoryId);
    }

    // --- (METODĂ NOUĂ) Execută tura de Odihnă ---
    private void performRestTurn(TextArea log, int victoryId) {
        jucator.rest();
        log.appendText("Te-ai odihnit și ai recuperat energie.\n");

        // Odihna tot consumă o tură, deci inamicul atacă
        checkEndOfTurn(log, victoryId);
    }

    // --- (METODĂ AJUTĂTOARE) Verifică finalul turei ---
    private void checkEndOfTurn(TextArea log, int victoryId) {
        // A murit inamicul?
        if (currentEnemy.getHealthPoints() <= 0) {
            log.appendText(currentEnemy.getName() + " a fost învins!\n");
            showBattleVictory(victoryId);
            return;
        }

        // Tura Inamicului
        enemyTurn(log); // Aceasta apelează logica veche a inamicului, care e ok

        // A murit jucătorul?
        if (jucator.getHealthPoints() <= 0) {
            log.appendText("Ai fost învins...\n");
            showGameOverScreen();
            return;
        }

        // Refresh vizual (foarte important pentru a vedea scăderea HP/Mana)
        // Re-apelăm showBattleScreen pentru a redesena barele de viață și butoanele
        showBattleScreen(victoryId);
    }

    // --- (METODĂ NOUĂ) Logica pentru TURA INAMICULUI ---
    private void enemyTurn(TextArea log) {
        log.appendText("\n--- Tura Inamicului ---\n");
        // Folosim metoda din clasa ta Enemy!
        currentEnemy.attackPlayer(jucator);

        // Sincronizăm mesajul din consola (de la 'takeDamage') cu jurnalul de luptă
        log.appendText(currentEnemy.getName() + " te atacă!\n");
        log.appendText("Ai rămas cu " + jucator.getHealthPoints() + " HP.\n");
        log.appendText("-----------------------\n\n");
    }

    // --- (METODĂ NOUĂ) Ecranul de Victorie ---
    // --- (METODĂ ACTUALIZATĂ) Ecranul de Victorie ---
    private void showBattleVictory(int locationId) {
        fereastraPrincipala.setTitle("Victorie!");

        VBox root = createDefaultVBox();
        Label victoryLabel = createNaratorLabel("Ai învins " + currentEnemy.getName() + "!");

        // 1. Luăm recompensele din inamic
        int xpGained = currentEnemy.getExperienceReward();
        int goldGained = currentEnemy.getGoldReward();

        // 2. Aplicăm recompensele jucătorului
        jucator.gainExperience(xpGained);
        // (Dacă ai implementat metoda addGold în PlayerCharacter, decomentează linia de mai jos)
        // jucator.addGold(goldGained);

        // 3. RECUPERARE AUTOMATĂ (AICI E SCHIMBAREA)
        // Refacem stamina și mana ca să fii gata de următoarea luptă
        jucator.recoverAfterCombat();

        // 4. Afișăm mesajul
        Label rewardLabel = createNaratorLabel(String.format(
                "Ai câștigat %d XP și %d Aur!\n\n(Te-ai tras sufletul și ți-ai recăpătat puterile.)",
                xpGained, goldGained
        ));

        Button continueButton = createContinueButton("Continuă");
        continueButton.setOnAction(e -> {
            showGameScreen(worldMap.get(locationId)); // Ne întoarcem la locația specificată
        });

        root.getChildren().addAll(victoryLabel, rewardLabel, continueButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- (METODĂ NOUĂ) Ecranul de Game Over ---
    private void showGameOverScreen() {
        fereastraPrincipala.setTitle("Ai Murit!");

        VBox root = createDefaultVBox();
        Label title = createNaratorLabel("MOARTEA TE-A AJUNS");
        title.setStyle("-fx-text-fill: #FF0000;"); // Roșu
        title.setFont(new Font("Georgia", 30));

        Label description = createNaratorLabel(
                "Molima te-a consumat. Viziunea ta se întunecă,\n" +
                        "iar ultimul tău gând este la lumea pe care ai eșuat să o salvezi."
        );

        Button quitButton = createContinueButton("Părăsește Jocul");
        quitButton.setOnAction(e -> fereastraPrincipala.close()); // Închide aplicația

        root.getChildren().addAll(title, description, quitButton);
        Scene scene = new Scene(root, LATIME_FEREASTRA, INALTIME_FEREASTRA);
        fereastraPrincipala.setScene(scene);
    }

    // --- METODE AJUTĂTOARE (Neschimbate) ---

    // Creează un VBox standard pentru scenele noastre
    private VBox createDefaultVBox() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #1E1E1E;");
        return vbox;
    }

    // Creează un Label standard pentru narator
    private Label createNaratorLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font("Georgia", 18));
        label.setStyle("-fx-text-fill: #E0E0E0;");
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.CENTER);
        return label;
    }

    // Creează un Text standard (pentru TextFlow)
    private Text createText(String text, Color color) {
        Text t = new Text(text);
        t.setFont(new Font("Georgia", 18));
        t.setFill(color);
        return t;
    }

    // Creează un buton de "Continuă" standard
    private Button createContinueButton(String text) {
        Button button = new Button(text);
        button.setFont(new Font("Arial", 14));
        button.setPadding(new Insets(10, 15, 10, 15));
        button.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-cursor: hand;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #777777; -fx-text-fill: white; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-cursor: hand;"));
        return button;
    }

    // Creează un RadioButton standard
    private RadioButton createRadioButton(String text, String userData) {
        RadioButton rb = new RadioButton(text);
        rb.setUserData(userData);
        rb.setFont(new Font("Arial", 14));
        rb.setStyle("-fx-text-fill: #E0E0E0;");
        return rb;
    }

    // ==========================================================
    // --- METODE NOI PENTRU MENIU ȘI SALVARE (Adăugate acum) ---
    // ==========================================================

    // 1. Logica de Încărcare
    // --- (METODĂ ACTUALIZATĂ) Logica de Încărcare ---
    private void loadGameFromDB() {
        System.out.println("Încercare încărcare joc...");
        DatabaseManager.SaveData data = DatabaseManager.loadGame();

        if (data.exists) {
            // 1. Recreăm obiectul corect
            this.numeJucator = data.name;
            switch (data.classType) {
                case "Mage": jucator = new Mage(numeJucator); break;
                case "Knight": jucator = new Knight(numeJucator); break;
                case "Assassin": jucator = new Assassin(numeJucator); break;
                case "Archer": jucator = new Archer(numeJucator); break;
                default: jucator = new Warrior(numeJucator); break;
            }

            // 2. Restaurăm datele salvate (folosind setterii noi)
            jucator.setTrait(data.trait);
            jucator.setLevel(data.level);
            jucator.setExperience(data.exp);
            jucator.setGold(data.gold);
            jucator.setSkillPoints(data.skillPoints);

            jucator.setHealthPoints(data.hp);
            jucator.setMaxHealth(data.maxHp);

            jucator.setActiveMana(data.mana);
            jucator.setBaseMana(data.maxMana);

            jucator.setActiveStamina(data.stamina);
            jucator.setBaseStamina(data.maxStamina);

            jucator.setActiveStrength(data.str);
            jucator.setActiveIntelligence(data.intel);
            jucator.setActiveDefense(data.def);
            jucator.setActiveSpeed(data.spd);

            // 3. Reconstruim harta lumii (ca să avem acces la locații)
            createWorldMap();

            // 4. Determinăm unde erai
            int targetLoc = data.locationId;

            // --- FIX PENTRU EROAREA 9001 ---
            // Verificăm dacă locația există în hartă.
            // Dacă am salvat în timpul unei lupte (9001), harta nu conține acest ID.
            // În acest caz, te trimitem la o locație sigură (ex: 1 - Pădurea).
            if (!worldMap.containsKey(targetLoc)) {
                System.out.println("⚠️ Locație salvată invalidă sau de luptă (" + targetLoc + "). Resetare la locația 1.");
                targetLoc = 1;
            }
            // -------------------------------

            System.out.println("Joc încărcat! Refresh la locația: " + targetLoc);

            // 5. REFRESH AUTOMAT
            showGameScreen(worldMap.get(targetLoc));

        } else {
            System.out.println("Nu există salvare.");
        }
    }

    // 2. Meniul Lateral Universal (apare în dreapta)
    private VBox createSideMenu() {
        VBox menuBox = new VBox(10);
        menuBox.setAlignment(Pos.TOP_CENTER);
        menuBox.setPadding(new Insets(10));
        menuBox.setStyle("-fx-border-color: #555555; -fx-border-width: 2; -fx-background-color: #2b2b2b;");

        Label menuLabel = createNaratorLabel("Meniu");
        menuLabel.setFont(new Font("Georgia", 20));

        // Butoane
        Button statsButton = createContinueButton("Stats");
        statsButton.setOnAction(e -> {
            if (jucator != null) {
                Location loc = (worldMap != null && worldMap.containsKey(0)) ? worldMap.get(0) : null;
                showCharacterScreen(loc);
            }
        });

        Button inventoryButton = createContinueButton("Inventar");
        inventoryButton.setOnAction(e -> {
            if (jucator != null) {
                showInventoryScreen(null);
            }
        });

        Button saveButton = createContinueButton("Salvează");
        saveButton.setOnAction(e -> {
            if (jucator != null) {
                int locID = (currentEnemy != null) ? 9001 : 0;
                DatabaseManager.saveGame(jucator, locID);
                saveButton.setText("Salvat!");
            }
        });

        Button loadButton = createContinueButton("Încarcă");
        loadButton.setOnAction(e -> loadGameFromDB());

        // Dezactivăm butoanele dacă nu suntem în joc propriu-zis (doar Load merge mereu)
        boolean isPlayerActive = (jucator != null);
        statsButton.setDisable(!isPlayerActive);
        inventoryButton.setDisable(!isPlayerActive);
        saveButton.setDisable(!isPlayerActive);

        menuBox.getChildren().addAll(menuLabel, statsButton, inventoryButton, saveButton, loadButton);
        return menuBox;
    }
}