package fr.umlv.thaw.user;

/**
 * Project :Thaw
 * Created by Narex on 12/10/2016.
 */
public class Bot extends UserImpl {

    //    private final String name;
    private final String filePropertiesName;

    public Bot(String name, String filePropertiesName) {
        super(name);
//        this.name = name;
        this.filePropertiesName = filePropertiesName;
    }


    @Override
    public String toString() {
        return "Bot{" +
                "name='" + name + '\'' +
                ", filePropertiesName='" + filePropertiesName + '\'' +
                '}';
    }


}
