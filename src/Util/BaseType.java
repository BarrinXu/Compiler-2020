package Util;

abstract public class BaseType extends Type{
    public String name;
    public BaseType(String name){
        super();
        this.name=name;
    }
    public String name(){
        return name;
    }
    @Override
    public int dim(){
        return 0;
    }

}
