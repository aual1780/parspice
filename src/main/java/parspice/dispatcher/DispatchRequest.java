package parspice.dispatcher;

public class DispatchRequest<T_Request extends com.google.protobuf.MessageOrBuilder> {
    private T_Request request;
    private int size;
    private boolean isJobComplete;

    /**
     * Create new DispatchRequest
     * @param Request gRPC request object
     * @param Size Number of user arguments contained in this request
     * @param IsJobComplete Flag to signal whether the job contains more user arguments.  False if no arguments remain
     */
    public DispatchRequest(T_Request Request, int Size, boolean IsJobComplete)
    {
        this.request = Request;
        this.size = Size;
        this.isJobComplete = IsJobComplete;
    }

    public T_Request getRequest() {return request; }
    public int getSize() { return size; }
    public boolean getCompletionState() { return isJobComplete; }
}
