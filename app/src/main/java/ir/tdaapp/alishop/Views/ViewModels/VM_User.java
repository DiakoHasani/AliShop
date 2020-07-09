package ir.tdaapp.alishop.Views.ViewModels;

public class VM_User {
    private String UserName;
    private int Kind;
    private String PalceName;
    private int FkPlace;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getKind() {
        return Kind;
    }

    public void setKind(int kind) {
        Kind = kind;
    }

    public String getPalceName() {
        return PalceName;
    }

    public void setPalceName(String palceName) {
        PalceName = palceName;
    }

    public int getFkPlace() {
        return FkPlace;
    }

    public void setFkPlace(int fkPlace) {
        FkPlace = fkPlace;
    }
}
