import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import static java.lang.Math.max;

public class Main {
    //Muszę deklarować osobne ArrayListy, bo inaczej kopiują się referencje.
    protected static ArrayList<Proces> waitingListFCFS = new ArrayList<>();
    protected static ArrayList<Proces> waitingListSJF = new ArrayList<>();
    protected static ArrayList<Proces> waitingListRR = new ArrayList<>();
    protected static ArrayList<Proces> activeListFCFS = new ArrayList<>();
    protected static ArrayList<Proces> activeListSJF = new ArrayList<>();
    protected static ArrayList<Proces> activeListRR = new ArrayList<>();
    protected static ArrayList<Proces> doneListFCFS = new ArrayList<>();
    protected static ArrayList<Proces> doneListSJF = new ArrayList<>();
    protected static ArrayList<Proces> doneListRR = new ArrayList<>();

    public static void main(String[] args) { // generuje 50 procesów

        for (int i = 0; i < 50; i++) {
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
                waitingListRR.add(new Proces(time,0));

            }
            else {
                int timeToAppear = ThreadLocalRandom.current().nextInt(0,100);
                waitingListFCFS.add(new Proces(time,timeToAppear));
                waitingListSJF.add(new Proces(time,timeToAppear));
                waitingListRR.add(new Proces(time,timeToAppear));
            }
        }
        //TODO: better way to store results, store each run as a class object?
        FCFS();
        SJF();
        RR(5);

    }

    private static ArrayList<Integer> FCFS() {


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
            for (Proces proces:doneListFCFS) {
                activeListFCFS.remove(proces);// Usuwam podczas iteorwania po innej liście, żeby uniknąć concurrent modification exception
            }

            //System.out.println(activeListFCFS.get(0).timeLeft);
            try {
                activeListFCFS.get(0).timeLeft -= 1;
            }
            catch (IndexOutOfBoundsException e) { // koniec listy procesów
                break;
            }

            currentTime += 1;
    }
        int longestWaitTime = 0;
        int averageWaitTime = 0;
        for (Proces proces:doneListFCFS) {
            if (proces.waitingTime > longestWaitTime) {
                longestWaitTime = proces.waitingTime;
            }
            averageWaitTime += proces.waitingTime;
        }
        averageWaitTime = averageWaitTime / doneListFCFS.size();
        System.out.println("FCFS wykonany ");
        System.out.println("Liczba przełączeń: " + switchNumber);
        System.out.println("Średni czas oczekiwania: " + averageWaitTime);
        System.out.println("Najdłuższy czas oczekiwania: " + longestWaitTime + "\n");
        ArrayList<Integer> statistics = new ArrayList<>();
        statistics.add(switchNumber);
        statistics.add(averageWaitTime);
        statistics.add(longestWaitTime);
        return statistics;
}

    private static ArrayList<Integer> SJF() {
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
                else {
                    proces.waitingTime += 1;
                    waitingListSJF.remove(proces);
                }

            }
            for (Proces proces:doneListSJF) {
                activeListSJF.remove(proces);// Usuwam podczas iteorwania po innej liście, żeby uniknąć concurrent modification exception
            }

            TimeRemainingComparator komparator = new TimeRemainingComparator();
            activeListSJF.sort(komparator);//sortuje po czasie wykonania, SJF
            //System.out.println(activeListSJF.get(0).timeLeft);
            try {
                activeListSJF.get(0).timeLeft -= 1;
            }
            catch (IndexOutOfBoundsException e) { // koniec listy procesów
                break;
            }

            currentTime += 1;
        }
        int longestWaitTime = 0;
        int averageWaitTime = 0;
        for (Proces proces:doneListSJF) {
            if (proces.waitingTime > longestWaitTime) {
                longestWaitTime = proces.waitingTime;
            }
            averageWaitTime += proces.waitingTime;
        }
        averageWaitTime = averageWaitTime / doneListSJF.size();
        System.out.println("SJF wykonany ");
        System.out.println("Liczba przełączeń: " + switchNumber);
        System.out.println("Średni czas oczekiwania: " + averageWaitTime);
        System.out.println("Najdłuższy czas oczekiwania: " + longestWaitTime + "\n");
        ArrayList<Integer> statistics = new ArrayList<>();
        statistics.add(switchNumber);
        statistics.add(averageWaitTime);
        statistics.add(longestWaitTime);
        return statistics;
    }

    private static ArrayList<Integer> RR(int timeQuant) {
        int currentTime = 0;
        int switchNumber = 0;

        while(!activeListRR.isEmpty() || !waitingListRR.isEmpty()) {

            //Aktywuje procesy, których czas nadszedł :)
            for (Proces proces : waitingListRR) {
                if (proces.arrivalTime == currentTime) {
                    activeListRR.add(proces);
                }
            }

            //Przenoszę skończone procesy do archiwum
            for (Proces proces : activeListRR) {
                if (proces.timeLeft <= 0) {
                    doneListRR.add(proces);
                } else {
                    for (Proces proces2 : activeListRR) {
                        if (proces2.timeLeft >0) {
                            //Jeżeli ten proces jest obecnie wykonywany, to czeka tylko tak długo ile potrzeba na jego wykonanie.
                            if(proces2.equals(proces)) {proces2.waitingTime += max(timeQuant, proces2.timeLeft);}
                            else {proces2.waitingTime += timeQuant;}
                        }
                    }
                    proces.timeLeft -= timeQuant;// Za każdym razem, kiedy wykonam jedną jednostkę procesu, wszystkie nne czekają.
                    switchNumber += 1;
                }
                waitingListRR.remove(proces);

            }
            for (Proces proces : doneListRR) {
                activeListRR.remove(proces);// Usuwam podczas iteorwania po innej liście, żeby uniknąć concurrent modification exception
            }

            currentTime += 1;
        }
            int longestWaitTime = 0;
            int averageWaitTime = 0;
            for (Proces proces:doneListRR) {
                if (proces.waitingTime > longestWaitTime) {
                    longestWaitTime = proces.waitingTime;
                }
                averageWaitTime += proces.waitingTime;
            }
            averageWaitTime = averageWaitTime / doneListRR.size();
            System.out.println("RR wykonany ");
            System.out.println("Liczba przełączeń: " + switchNumber);
            System.out.println("Średni czas oczekiwania: " + averageWaitTime);
            System.out.println("Najdłuższy czas oczekiwania: " + longestWaitTime + "\n");
            ArrayList<Integer> statistics = new ArrayList<>();
            statistics.add(switchNumber);
            statistics.add(averageWaitTime);
            statistics.add(longestWaitTime);
            return statistics;
    }
}
