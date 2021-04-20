package MIR.IRType;

import java.util.ArrayList;

public class ClassType extends IRBaseType{
    public String name;
    public int size=0;
    public ArrayList<IRBaseType> members=new ArrayList<>();
    public ClassType(String name){
        super();
        this.name=name;
    }

    public void addMember(IRBaseType member){
        members.add(member);
        size+=member.size();
    }

    public int getElementBitOffset(int kth){
        int offset=0;
        for(int i=0; i<kth; i++)
            offset+=members.get(i).size();
        return offset;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean sameType(IRBaseType rhs) {
        return rhs instanceof ClassType&&((ClassType) rhs).name.equals(name);
    }
}
