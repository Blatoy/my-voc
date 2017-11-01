package cifom_et.myvoc.data.api.objects;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public class Language {
    private int id;
    private String name;

    /**
     * @param id   The id of the language
     * @param name The name of the language
     */
    public Language(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @return The id of the language
     */
    // Getter and setter
    public int getId() {
        return id;
    }

    /**
     * Set the id for this language
     *
     * @param id -
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The name of this language
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this language
     *
     * @param name The name of the language
     */
    public void setName(String name) {
        this.name = name;
    }
}
