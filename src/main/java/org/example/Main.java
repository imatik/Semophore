package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

enum Flower{
    Roses("Розы"),Violets("Фиалки"),Pions("Пионы");
    private String name;

    Flower(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Flowers{
    private String name;

    Flowers(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

}

class Store {

    public static int necessary;
    public static int bouquet;
    private Semaphore semaphore;
    public Map<String,Integer> store_map;

    public Store(){
        store_map = new HashMap<>();
        semaphore = new Semaphore(1);
    }

    public void put_flowers(String flower, int volume){
        if(store_map.containsKey(flower)){
            int temp = store_map.get(flower);
            volume +=temp;
            store_map.put((new Flowers(flower)).getName(), volume);
        }
        else{
            store_map.put((new Flowers(flower)).getName(), volume);
        }
    }

    public Semaphore getSemaphore(){return semaphore;}
    public void show_store(){
        if(store_map.isEmpty())
            System.out.println("В цветочном ничего нету!");
        else{
            System.out.println(store_map);
        }

    }
}

class Provider extends Thread {
    public String name;
    private Store store;
    Semaphore sem_P;
    public static boolean flag_P = true;
    public Flower flower1;
    public Flower flower2;
    private int value_f1;
    private int value_f2;

    Provider(Semaphore sem_P,Store store,Flower flower1,int value_f1,Flower flower2,int value_f2){
        this.store = store;
        this.flower1 = flower1;
        this.flower2 = flower2;
        this.value_f1 = value_f1;
        this.value_f2 = value_f2;
        this.sem_P = sem_P;
    }

    @Override
    public void run() {
        while (Store.bouquet < Store.necessary) {
            try {

                Thread.sleep((long) ThreadLocalRandom.current().nextDouble()%100);
                sem_P.acquire();
                {

                    if (flag_P == true) {
                        flag_P = false;

                        store.getSemaphore().acquire();
                        {
                            System.out.println(this.name + " в семофоре");

                            store.put_flowers(flower1.getName(), value_f1);
                            store.put_flowers(flower2.getName(), value_f2);

                            Bouquet_Creater.flag_C = true;

                            System.out.println(this.name + " вышел из семофора");
                            store.show_store();
                            System.out.println();
                        }
                        store.getSemaphore().release();

                    }
                }
                sem_P.release();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

class Bouquet_Creater extends Thread{

    public String name;
    Semaphore sem_C;
    public static boolean flag_C = false;
    private Flower inf_flower;
    private Store store;

    Bouquet_Creater(Semaphore sem_C,Store store,Flower inf_flower){
        this.store = store;
        this.inf_flower = inf_flower;
        this.sem_C = sem_C;
    }


    @Override
    public void run() {

        while (Store.bouquet<Store.necessary) {
            try {
                sem_C.acquire();
                {
                    if(flag_C == true && Store.bouquet < Store.necessary)
                    {
                        boolean work = true;
                        for (String key : store.store_map.keySet()) {
                            if (key != inf_flower.getName() && store.store_map.get(key) == 0) {
                                work = false;
                            }
                        }
                        if (work) {
                            store.getSemaphore().acquire();
                            {
                                System.out.println(this.name + " в семофоре");
                                store.store_map.forEach((key, value) -> {
                                    if (key == inf_flower.getName())
                                        store.put_flowers(inf_flower.getName(), 0);
                                    else
                                        store.put_flowers(key, -1);
                                });
                                Thread.sleep(300);

                                Store.bouquet++;

                                int temp = 0;
                                for (String key : store.store_map.keySet()) {
                                    if (store.store_map.get(key) == 0) {
                                        temp++;
                                    }
                                }
                                if (temp > 1) {
                                    Provider.flag_P = true;
                                    flag_C = false;
                                }

                                System.out.println(this.name + " вышел из семофора");
                                store.show_store();
                                System.out.println("Сделано букетов " + Store.bouquet + "\n");
                            }
                            store.getSemaphore().release();

                        }
                    }
                }
                sem_C.release();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


public class Main {
    public static void main(String[] args) throws InterruptedException {


        Store store = new Store();
        store.store_map.put(Flower.Pions.getName(), 0);
        store.store_map.put(Flower.Roses.getName(), 0);
        store.store_map.put(Flower.Violets.getName(), 0);

        Semaphore sem_P = new Semaphore(1);
        Semaphore sem_C = new Semaphore(1);

        System.out.println("Какое количество букетов должно быть сделано?\nHow many bouquets should be made?");
        System.out.print("-");
        Scanner scanner = new Scanner(System.in);
        Store.necessary = scanner.nextInt();

        Bouquet_Creater S1;
        Bouquet_Creater S2;
        Bouquet_Creater S3;

        Provider V1;
        Provider V2;
        Provider V3;

        S1 = new Bouquet_Creater(sem_C,store, Flower.Pions);
        S1.name = "S1";
        S2 = new Bouquet_Creater(sem_C,store, Flower.Violets);
        S2.name = "S2";
        S3 = new Bouquet_Creater(sem_C,store, Flower.Roses);
        S3.name = "S3";

        V1 = new Provider(sem_P,store, Flower.Roses, 4, Flower.Violets, 5);
        V1.name = "V1";
        V2 = new Provider(sem_P,store, Flower.Pions, 2, Flower.Roses, 3);
        V2.name = "V2";
        V3 = new Provider(sem_P,store, Flower.Pions, 5, Flower.Violets, 1);
        V3.name = "V3";

        V1.start();
        V2.start();
        V3.start();

        S1.start();
        S2.start();
        S3.start();

        V1.join();
        V2.join();
        V3.join();

        S1.join();
        S2.join();
        S3.join();

        store.show_store();
        System.out.println("Сделано букетов "+ Store.bouquet+"\n");

    }
}