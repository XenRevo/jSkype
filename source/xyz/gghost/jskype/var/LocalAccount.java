package xyz.gghost.jskype.var;

import lombok.Data;

@Data
public class LocalAccount {
    private String location = "";
    private String displayName;
    private String name;
    private Gender gender; //TODO:
    private String email;
    private String DOB;
    private String phoneNumber;
    private String mood;
    private String site;
    private String avatar;
    private String firstLoginIP;
    private String language;
    private int creationTime;
    private String microsoftRank;
}
