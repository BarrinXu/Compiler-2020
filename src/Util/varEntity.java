package Util;

public class varEntity extends Entity{
    private Type type;
    private boolean isGlobal, isMember;
    public varEntity(String name, Type type, boolean isGlobal){
        super(name);
        this.type=type;
        this.isGlobal=isGlobal;
        this.isMember=false;
    }
    public Type type(){
        return type;
    }
    public boolean isGlobal(){
        return isGlobal;
    }
    public boolean isMember(){
        return isMember;
    }
    public void setIsMember(){
        isMember=true;
    }
}
