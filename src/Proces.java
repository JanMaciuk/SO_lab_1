public class Proces {
    protected int length;  //Całkowita długość
    protected int timeLeft; //Ile czasu zostało do zakończenia
    protected int waitingTime; //Jak długo czekał niewykonywany
    protected int arrivalTime;  //Kiedy zaczniemy go wykonywać

    public Proces(int length, int arrivalTime) {
        this.length = length;
        this.timeLeft = length;
        this.waitingTime = 0;
        this.arrivalTime = arrivalTime;
    }


}
