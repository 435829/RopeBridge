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

    private static final int NR_OF_PEOPLE = 20;
    private static final int BRIDGE_CAPACITY = 3;
    private static final int NR_OF_PEOPLE_ALLOWED_FROM_ONE_SIDE = 1;
    private boolean acquiredBridgeLeft = false;
    private boolean acquiredBridgeRight = false;
    private int peopleWalkedOver = 0;
    private int personenOpBrug = 0;
    private static int LINKS = 1;
    private static int RECHTS = 2;
    private Semaphore mutex, bridge, peopleOnBridge;

    private Person[] person = new Person[NR_OF_PEOPLE];


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
                        acquiredBridgeLeft = true;

                        peopleOnBridge.acquire();
                        System.out.println(getName() + " Loopt de burg op vanaf links");

                        mutex.acquire();
                        personenOpBrug++;
                        mutex.release();

                        assert personenOpBrug <= 3 : "Too many people on bridge";

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
                        acquiredBridgeRight = true;

                        peopleOnBridge.acquire();
                        System.out.println(getName() + " Loopt de burg op vanaf rechts");

                        mutex.acquire();
                        personenOpBrug++;
                        mutex.release();

                        assert personenOpBrug <= 3 : "Too many people on bridge";

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

        private void walk() {
            try {
                System.out.println(getName() + " loopt over de brug");
                Thread.sleep((int) (Math.random() *
                        100));
            } catch (InterruptedException e) {
            }
        }


        private void justLive() {
            try {
                System.out.println(getName() + " working/getting education");
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
            }
        }
    }
}


