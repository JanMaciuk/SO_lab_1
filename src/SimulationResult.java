public class SimulationResult {
    protected int switchNumber;
    protected int averageWaitTime;
    protected int longestWaitTime;
    public SimulationResult(int switchNumber, int averageWaitTime, int longestWaitTime) {
        this.switchNumber = switchNumber;
        this.averageWaitTime = averageWaitTime;
        this.longestWaitTime = longestWaitTime;
    }
}
