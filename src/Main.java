import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import static java.lang.Math.max;
import static java.lang.Math.min;

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
    protected static ArrayList<SimulationResult> resultsFCFS = new ArrayList<>();
    protected static ArrayList<SimulationResult> resultsSJF = new ArrayList<>();
    protected static ArrayList<SimulationResult> resultsRR = new ArrayList<>();



    //Konfiguracja parametrów symulacji:
    protected static int RRtimeQuant = 5;
    protected static int processNumber = 1000/2; // Usunąć potem to dzielenie
    protected static int runAmount = 20;

    public static void main(String[] args) {

        for(int i = 0; i < runAmount; i++) {
            processesGenerator();
            resultsFCFS.add(FCFS());
            resultsSJF.add(SJF());
            resultsRR.add(RR(RRtimeQuant));
        }
        System.out.println("FCFS:");
        printResults(resultsFCFS);
        System.out.println("SJF:");
        printResults(resultsSJF);
        System.out.println("RR:");
        printResults(resultsRR);

    }

    public static void printResults(ArrayList<SimulationResult> results) {
        int switchNumber = 0;
        int averageWaitTime = 0;
        int longestWaitTime = 0;
        for (SimulationResult result:results) {
            switchNumber += result.switchNumber;
            averageWaitTime += result.averageWaitTime;
            longestWaitTime = max(result.longestWaitTime,longestWaitTime);
        }
        System.out.println("Średnia liczba przełączeń: " + switchNumber/runAmount);
        System.out.println("Średni czas oczekiwania: " + averageWaitTime/runAmount);
        System.out.println("Najdłuższy czas oczekiwania: " + longestWaitTime);
    }
    private static void processesGenerator() {
        if(!waitingListFCFS.isEmpty()|| !waitingListSJF.isEmpty() || !waitingListRR.isEmpty() || !activeListFCFS.isEmpty() || !activeListSJF.isEmpty() || !activeListRR.isEmpty()) {
            //Listy powinny być puste, ale better safe than sorry.
            waitingListFCFS.clear();
            waitingListSJF.clear();
            waitingListRR.clear();
            activeListFCFS.clear();
            activeListSJF.clear();
            activeListRR.clear();

        }
        doneListFCFS.clear();
        doneListSJF.clear();
        doneListRR.clear();

        for (int i = 0; i < processNumber; i++) {// generuje procesy
        int time;
        int randomProbability = ThreadLocalRandom.current().nextInt(1,11);
        // Rozkład prawdopodobieństwa czasu wykonywania:
        if (randomProbability < 3) {time = ThreadLocalRandom.current().nextInt(1,5);}
        else if (randomProbability < 8) {time = ThreadLocalRandom.current().nextInt(5,15); }
        else  {time = ThreadLocalRandom.current().nextInt(15,35);}
        // Mała część procesów będzie wykonywana od razu, reszta dojdzie w losowym momencie:
        if (ThreadLocalRandom.current().nextBoolean() && ThreadLocalRandom.current().nextBoolean()) {
            waitingListFCFS.add(new Proces(time,0));
            waitingListSJF.add(new Proces(time,0));
            waitingListRR.add(new Proces(time,0));

        }
        else {
            int timeToAppear = ThreadLocalRandom.current().nextInt(0,processNumber*2);
            waitingListFCFS.add(new Proces(time,timeToAppear));
            waitingListSJF.add(new Proces(time,timeToAppear));
            waitingListRR.add(new Proces(time,timeToAppear));
        }
    }}

    private static SimulationResult FCFS() {


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
        averageWaitTime = averageWaitTime / processNumber;
        return new SimulationResult(switchNumber,averageWaitTime,longestWaitTime);
}

    private static SimulationResult SJF() {
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
        averageWaitTime = averageWaitTime / processNumber;
        return new SimulationResult(switchNumber,averageWaitTime,longestWaitTime);
    }

    private static SimulationResult RR(int timeQuant) {
        int currentTime = 0;
        int switchNumber = 0;

        while(!activeListRR.isEmpty() || !waitingListRR.isEmpty()) {

            //Aktywuje procesy, których czas nadszedł :)
            for (Proces proces : waitingListRR) {
                if (proces.arrivalTime <= currentTime) {
                    activeListRR.add(proces);
                }
            }

            //Przenoszę skończone procesy do archiwum
            if (activeListRR.isEmpty()) {
                currentTime += 1;
            }
            else {
            for (Proces proces : activeListRR) {
                if (proces.timeLeft <= 0) {
                    doneListRR.add(proces);
                } else {
                    for (Proces proces2 : activeListRR) {
                        if (proces2.timeLeft >0) {
                            //Jeżeli ten proces jest obecnie wykonywany, to czeka tylko tak długo ile potrzeba na jego wykonanie.
                            proces2.waitingTime += min(timeQuant, proces.timeLeft);
                        }
                    }
                    proces.timeLeft -= min(timeQuant,proces.timeLeft);// Za każdym razem, kiedy wykonam jedną jednostkę procesu, wszystkie nne czekają.
                    switchNumber += 1;
                }
                waitingListRR.remove(proces);
                currentTime += 1;

            }}
            for (Proces proces : doneListRR) {
                activeListRR.remove(proces);// Usuwam podczas iteorwania po innej liście, żeby uniknąć concurrent modification exception
            }


        }
            int longestWaitTime = 0;
            int averageWaitTime = 0;
            for (Proces proces:doneListRR) {
                if (proces.waitingTime > longestWaitTime) {
                    longestWaitTime = proces.waitingTime;
                }
                averageWaitTime += proces.waitingTime;
            }
            averageWaitTime = averageWaitTime / processNumber;
            return new SimulationResult(switchNumber,averageWaitTime,longestWaitTime);
    }
}
