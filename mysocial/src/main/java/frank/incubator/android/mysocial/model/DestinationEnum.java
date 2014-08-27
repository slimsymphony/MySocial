package frank.incubator.android.mysocial.model;

/**
 * Enumeration for Destination.
 * Created by f78wang on 8/22/14.
 */
public enum DestinationEnum {
    WEIBO,WECHAT,FACEBOOK,TWITTER;
    public static DestinationEnum parse(String word){
        if(word == null)
            return null;
        for(DestinationEnum de : DestinationEnum.values()){
            if(de.name().equalsIgnoreCase(word.trim()))
                return de;
        }
        return null;
    }
}
