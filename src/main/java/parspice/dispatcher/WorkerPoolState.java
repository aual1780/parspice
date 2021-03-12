package parspice.dispatcher;

import util.validation.NotNull;
import io.grpc.Channel;
import parspice.rpc.ParSPICEGrpc;

public class WorkerPoolState {
    private final int workerCount;
    private final int maxBatchSize;
    private final Process[] processCollection;
    private final Channel[] channelCollection;
    private final ParSpiceStub[] parClients;

    public WorkerPoolState(
            int workerCount,
            int maxBatchSize,
            @NotNull Process[] processCollection,
            @NotNull Channel[] channelCollection,
            @NotNull ParSpiceStub[] parClients) //TODO: change to proper worker type
    {
        this.workerCount = workerCount;
        this.maxBatchSize = maxBatchSize;
        this.processCollection = processCollection;
        this.channelCollection = channelCollection;
        this.parClients = parClients;
    }

    @NotNull
    public int getWorkerCount()
    {
        return workerCount;
    }

    @NotNull
    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    @NotNull
    public Process[] getProcessCollection()
    {
        return processCollection;
    }

    @NotNull
    public Channel[] getChannelCollection() {
        return channelCollection;
    }

    @NotNull
    public ParSpiceStub[] getParClients()
    {
        return parClients;
    }
}
