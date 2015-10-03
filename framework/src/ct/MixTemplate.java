package mobcon.ct;

public interface MixTemplate{

    //ct2 is merged into ct1 and ct1 is also returned
    public ClassTemplate mixClassTemplates(ClassTemplate ct1, ClassTemplate ct2);

    //mt2 is merged into mt1 and mt1 is also returned
    public MethodTemplate mixMethodTemplates(MethodTemplate mt1, MethodTemplate mt2);
}