package parspice.dispatcher;

@FunctionalInterface
public interface Function2<In1, In2, Out> {
    public Out apply(In1 in1, In2 in2) throws InterruptedException;
}
