import java.util.ArrayList;

public class Location {
    private int id; // identificarea locației
    private String description; // descrierea locației
    private ArrayList<String> optionsText; // optiuni de dialog
    private ArrayList<Integer> optionsLinks; // lista Id-ului locatiei pentru ficare alegere

    //Constructor
    public Location(int id, String description,
     ArrayList<String> optionsText, 
     ArrayList<Integer> optionsLinks) {
        this.id = id;
        this.description = description;
        this.optionsText = optionsText;
        this.optionsLinks = optionsLinks;
    }

    //Getters
    // Permit altor clase sa citeasca datele private

    public String getDescription() {
        return this.description;
    }
    public ArrayList<String> getOptionsText() {
        return this.optionsText;
    }
    public ArrayList<Integer> getOptionsLinks() {
        return this.optionsLinks;
    }
    public int getID() {
        return this.id;
    }
    
}
