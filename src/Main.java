import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
public class Main {
    //Muszę deklarować osobne ArrayListy, bo inaczej kopiują się referencje.
    protected static ArrayList<Proces> waitingListFCFS = new ArrayList<>();
    protected static ArrayList<Proces> waitingListSJF = new ArrayList<>();
    protected static ArrayList<Proces> activeListFCFS = new ArrayList<>();
    protected static ArrayList<Proces> activeListSJF = new ArrayList<>();
    protected static ArrayList<Proces> doneListFCFS = new ArrayList<>();
    protected static ArrayList<Proces> doneListSJF = new ArrayList<>();

    public static void main(String[] args) { // generuje 20 procesów

        for (int i = 0; i < 20; i++) {
            int time;
            int randomProbability = ThreadLocalRandom.current().nextInt(1,11);
            // Rozkład prawdopodobieństwa czasu wykonywania:
            if (randomProbability < 3) {time = ThreadLocalRandom.current().nextInt(1,5);}
            else if (randomProbability < 8) {time = ThreadLocalRandom.current().nextInt(5,15); }
            else  {time = ThreadLocalRandom.current().nextInt(15,35);}
            // Około połowa procesów będzie wykonywana od razu, druga połowa dojdzie w losowym momencie:
            if (ThreadLocalRandom.current().nextBoolean()) {
                waitingListFCFS.add(new Proces(time,0));
                waitingListSJF.add(new Proces(time,0));

            }
            else {
                int timeToAppear = ThreadLocalRandom.current().nextInt(0,100);
                waitingListFCFS.add(new Proces(time,timeToAppear));
                waitingListSJF.add(new Proces(time,timeToAppear));
            }
        }
        FCFS();
        SJF();

    }

    private static void FCFS() {


        int currentTime = 0;
        int switchNumber = 0;

        while(!activeListFCFS.isEmpty() || !waitingListFCFS.isEmpty()) {

            //Aktywuje procesy, których czas nadszedł :)
            for (Proces proces:waitingListFCFS) {
                if (proces.arrivalTime == currentTime) {
                    activeListFCFS.add(proces);
                }}

            //Przenoszę skończone procesy do archiwum
            for(Proces proces:activeListFCFS) {
                if (proces.timeLeft <= 0) {
                    doneListFCFS.add(proces);
                    switchNumber += 1;// Następuje przełączenie
                }
                proces.waitingTime += 1;
                waitingListFCFS.remove(proces);
            }

            System.out.println(activeListFCFS.get(0).timeLeft);
            activeListFCFS.get(0).waitingTime -= 1;// zwiększyłem czas oczekiwania wszystkim procesom poza pierwszym.
            activeListFCFS.get(0).timeLeft -= 1;
            for (Proces proces:doneListFCFS) {
                activeListFCFS.remove(proces);// Usuwam podczas iteorwania po innej liście, żeby uniknąć concurrent modification exception
            }
            currentTime += 1;
    }
        int longestWaitTime = 0;
        for (Proces proces:doneListFCFS) {
            if (proces.waitingTime > longestWaitTime) {
                longestWaitTime = proces.waitingTime;
            }
        }
        int averageWaitTime = 0;
        for (Proces proces:doneListFCFS) {
            averageWaitTime += proces.waitingTime;
        }
        averageWaitTime = averageWaitTime / doneListFCFS.size();
        System.out.println("FCFS wykonany ");
        System.out.println("Liczba przełączeń: " + switchNumber);
        System.out.println("Średni czas oczekiwania: " + averageWaitTime);
        System.out.println("Najdłuższy czas oczekiwania: " + longestWaitTime);
}

    private static void SJF() {
        int currentTime = 0;
        int switchNumber = 0;

        while(!activeListSJF.isEmpty() || !waitingListSJF.isEmpty()) {

            //Aktywuje procesy, których czas nadszedł :)
            for (Proces proces:waitingListSJF) {
                if (proces.arrivalTime == currentTime) {
                    activeListSJF.add(proces);
                }}

            //Przenoszę skończone procesy do archiwum
            for(Proces proces:activeListSJF) {
                if (proces.timeLeft <= 0) {
                    doneListSJF.add(proces);
                    switchNumber += 1;// Następuje przełączenie
                }
                proces.waitingTime += 1;
                waitingListSJF.remove(proces);
            }
            TimeRemainingComparator komparator = new TimeRemainingComparator();
            activeListSJF.sort(komparator);//sortuje po czasie wykonania, tak żeby osiągnąć SJF
            System.out.println(activeListSJF.get(0).timeLeft);
            activeListSJF.get(0).waitingTime -= 1;// zwiększyłem czas oczekiwania wszystkim procesom poza pierwszym.
            activeListSJF.get(0).timeLeft -= 1;
            for (Proces proces:doneListSJF) {
                activeListSJF.remove(proces);// Usuwam podczas iteorwania po innej liście, żeby uniknąć concurrent modification exception
            }
            currentTime += 1;
        }
        int longestWaitTime = 0;
        for (Proces proces:doneListSJF) {
            if (proces.waitingTime > longestWaitTime) {
                longestWaitTime = proces.waitingTime;
            }
        }
        int averageWaitTime = 0;
        for (Proces proces:doneListSJF) {
            averageWaitTime += proces.waitingTime;
        }
        averageWaitTime = averageWaitTime / doneListSJF.size();
        System.out.println("SJF wykonany ");
        System.out.println("Liczba przełączeń: " + switchNumber);
        System.out.println("Średni czas oczekiwania: " + averageWaitTime);
        System.out.println("Najdłuższy czas oczekiwania: " + longestWaitTime);
    }
}
