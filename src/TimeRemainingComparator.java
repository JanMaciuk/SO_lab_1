import java.util.Comparator;
public class TimeRemainingComparator implements Comparator<Proces> {
public int compare(Proces proc1, Proces proc2) { //Komparator po pozostałym czasie
    if (proc1.timeLeft < proc2.timeLeft) {
        return -1;
    } else if (proc1.timeLeft > proc2.timeLeft) {
        return 1;
    } else {
        return 0;
    }}

}
