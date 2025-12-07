public abstract class Item {
    protected String name;
    protected String description;
    protected int value;
    
    public Item(String name, String description, int value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }
    // Metoda abstracta care oblica subclasele sa implementeze cum se foloseste itemul\
    public abstract void useItem(PlayerCharacter player);

    // Adauga Getters pentru a putea citi atributele (ex: name) din alte clase
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public int getValue(){
        return value;
    }
}
