package Util;

public class varEntity extends Entity{
    private Type type;

    public varEntity(String name, Type type){
        super(name);
        this.type=type;
    }
    public Type type(){
        return type;
    }

}
