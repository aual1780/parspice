package parspice.dispatcher;

@FunctionalInterface
public interface Consumer3<In1, In2, In3> {
    public void apply(In1 in1, In2 in2, In3 in3) throws InterruptedException;
}