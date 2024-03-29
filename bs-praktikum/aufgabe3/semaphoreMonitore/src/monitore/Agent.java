package monitore;

import java.util.*;

import static monitore.Dinge.*;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 28.11.12
 * Time: 11:24
 */
public class Agent implements Runnable {
    String name;

    List<Dinge> dinges;

    public Agent(String name) {
        this.name = name;
        this.dinges = new ArrayList<Dinge>(Arrays.asList(TOBACCO, MATCHES, PAPER));
    }

    //Methods - @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public void legeDingeAufTisch(List<Dinge> topf) throws InterruptedException {
        synchronized (topf){
            Collections.shuffle(dinges);

            topf.addAll(dinges.subList(0,2));
            System.out.println(name+ ": der Agent hat neue Zutaten "+topf+" auf dem Tisch plaziert");
            topf.notifyAll();
            topf.wait();

        }
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                legeDingeAufTisch(Holztisch.topf);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }
}
