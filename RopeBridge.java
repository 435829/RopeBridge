package nl.saxion.act.ip;

import java.util.concurrent.Semaphore;

/**
 * Created by bolin on 21-9-2017.
 */
public class RopeBridge {

    /**
     * your s(hared) data structures to garantee correct behaviour of the people
     * in passing the rope bridgeLeftSide
     **/

    /**
     * Variabele die regelt hoeveel personen er aangemaakt worden door het programma
     */
    private static final int NR_OF_PEOPLE = 20;

    /**
     * Variabele die gebruikt wordt om de capaciteit van de brug mee te geven
     */
    private static final int BRIDGE_CAPACITY = 3;

    /**
     * Boolean die bijhoud of de brug bezet is vanaf de linkerkant
     */
    private boolean acquiredBridgeLeft = false;

    /**
     * Boolean die bijhoud of de brug bezet is vanaf de rechterkant
     */
    private boolean acquiredBridgeRight = false;

    /**
     * Variabele die bijhoud hoeveel mensen er op de brug aanwezig zijn
     */
    private int personenOpBrug = 0;

    /**
     * Static variabele die gebruikt wordt om aan te geven vanaf welke kant iemand komt
     */
    private static int LINKS = 1;

    /**
     * Static variabele die gebruikt wordt om aan te geven vanaf welke kant iemand komt
     */
    private static int RECHTS = 2;

    /**
     * De synchronisatie punten die gebruikt zijn
     */
    private Semaphore mutex, bridge, peopleOnBridge;

    /**
     * Array waar alle personen in worden opgeslagen
     */
    private Person[] person = new Person[NR_OF_PEOPLE];


    /**
     * Constructor waar de synchronisatie punten en personen aangemaakt worden
     */
    public RopeBridge() {

        // bridgeLeftSide semaphore heeft een maximum van 3 (BRIDGE_CAPACITY), er kunnen dus
        // maximaal 3 mensen tegelijk de brug op
        mutex = new Semaphore(1);
        bridge = new Semaphore(1, true);
        peopleOnBridge = new Semaphore(BRIDGE_CAPACITY, true);



        for (int i = 0; i < NR_OF_PEOPLE; i++) {
            person[i] = new Person("p" + i); /* argument list can be extended */
            person[i].start();
        }

    }

    /**
     * Thread voor een persoon, deze thread regelt alles van de brug en de personen
     */
    class Person extends Thread {

        public Person(String name) {
            super(name);

        }

        public void run() {

            int kant = (int) (Math.random() * 2) + 1;

            while (true) {
                justLive();
                try {
                    if (kant == LINKS) {
                        if (!acquiredBridgeLeft) {
                            bridge.acquire();
                        }
                        assert !acquiredBridgeRight : "Persons on other side of bridge";
                        acquiredBridgeLeft = true;

                        peopleOnBridge.acquire();
                        System.out.println(getName() + " Loopt de burg op vanaf links");

                        mutex.acquire();
                        personenOpBrug++;
                        mutex.release();

                        assert personenOpBrug <= BRIDGE_CAPACITY : "Too many people on bridge";

                        walk();

                        System.out.println(getName() + " Loopt de burg af vanaf links");

                        peopleOnBridge.release();

                        mutex.acquire();
                        personenOpBrug--;
                        mutex.release();

                        assert personenOpBrug >= 0 : "Negative people on bridge";

                        if (personenOpBrug == 0) {
                            acquiredBridgeLeft = false;
                            bridge.release();

                        }

                        kant = RECHTS;
                    } else if (kant == RECHTS) {
                        if (!acquiredBridgeRight) {
                            bridge.acquire();
                        }
                        assert !acquiredBridgeLeft : "Persons on other side of bridge";
                        acquiredBridgeRight = true;

                        peopleOnBridge.acquire();
                        System.out.println(getName() + " Loopt de burg op vanaf rechts");

                        mutex.acquire();
                        personenOpBrug++;
                        mutex.release();

                        assert personenOpBrug <= BRIDGE_CAPACITY : "Too many people on bridge";

                        walk();

                        System.out.println(getName() + " Loopt de burg af vanaf rechts");

                        peopleOnBridge.release();

                        mutex.acquire();
                        personenOpBrug--;
                        mutex.release();

                        assert personenOpBrug >= 0 : "Negative people on bridge";

                        if (personenOpBrug == 0) {
                            acquiredBridgeRight = false;
                            bridge.release();
                        }

                        kant = LINKS;
                    } else {
                        mutex.release();
                    }


                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        /**
         * Methode om het lopen te simuleren qua tijd
         */
        private void walk() {
            try {
                System.out.println(getName() + " loopt over de brug");
                Thread.sleep((int) (Math.random() *
                        1000));
            } catch (InterruptedException e) {
            }
        }

        /**
         * Methode om het "leven" van de mensen te simuleren
         */
        private void justLive() {
            try {
                System.out.println(getName() + " working/getting education");
                Thread.sleep((int) (Math.random() * 10000));
            } catch (InterruptedException e) {
            }
        }
    }
}


