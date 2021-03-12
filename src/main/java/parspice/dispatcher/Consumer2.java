package parspice.dispatcher;

@FunctionalInterface
public interface Consumer2<In1, In2> {
    public void apply(In1 in1, In2 in2) throws InterruptedException;
}
