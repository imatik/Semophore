package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

enum Flower{
    Roses("Розы"),Filas("Фиалки"),Pions("Пионы");
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
    private boolean occupied;

    Flowers(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void Occupy(){
        occupied = true;
    }
    public void free(){
        occupied = false;
    }
}

class Store {
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
    public static boolean flag;
    private Store store;
    public Flower flower1;
    public Flower flower2;
    private int value_f1;
    private int value_f2;

    Provider(Store store,Flower flower1,int value_f1,Flower flower2,int value_f2){
        this.store = store;
        this.flower1 = flower1;
        this.flower2 = flower2;
        this.value_f1 = value_f1;
        this.value_f2 = value_f2;
    }

    @Override
    public void run() {

        long temp = 0;
        for (String key : store.store_map.keySet()) {
            if(store.store_map.get(key) == 0) {
                temp++;
            }
        }

        try {
            Thread.sleep((long) ThreadLocalRandom.current().nextDouble());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(temp>1 && store.getSemaphore().tryAcquire()) {

            System.out.println("В очереди "+ name);
            try {
                System.out.println("Работает "+ name);

                store.put_flowers(flower1.getName(), value_f1);
                store.put_flowers(flower2.getName(), value_f2);

                Thread.sleep(300);

                System.out.println("Закончил работать "+ name);
                store.getSemaphore().release();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("Не нужен "+ name);
            Thread.currentThread().interrupt();
        }
    }

}

class Bouquet_Creater extends Thread{

    public String name;
    private Flower inf_flower;
    private Store store;

    Bouquet_Creater(Store store,Flower inf_flower){
        this.store = store;
        this.inf_flower = inf_flower;
    }

    @Override
    public void run() {

        boolean work = true;
        for (String key : store.store_map.keySet()) {
            if(key != inf_flower.getName() && store.store_map.get(key) == 0) {
                work = false;
            }
        }

        try {
            Thread.sleep((long) ThreadLocalRandom.current().nextDouble());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(work && store.getSemaphore().tryAcquire()) {
            System.out.println("В очереди " + name);
            try {
                System.out.println("Работает " + name);
                store.store_map.forEach((key, value) -> {
                    if (key == inf_flower.getName())
                        store.put_flowers(inf_flower.getName(), 0);
                    else
                        store.put_flowers(key, -1);
                });
                Thread.sleep(300);
                System.out.println("Закончил работать " + name);
                store.getSemaphore().release();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("Не нужен "+ name);
            Thread.currentThread().interrupt();
        }

    }
}


public class Main {
    public static void main(String[] args) throws InterruptedException {

        Store store = new Store();
        store.store_map.put(Flower.Pions.getName(), 0);
        store.store_map.put(Flower.Roses.getName(), 0);
        store.store_map.put(Flower.Filas.getName(), 0);


        Bouquet_Creater S1;
        Bouquet_Creater S2;
        Bouquet_Creater S3;

        Provider V1;
        Provider V2;
        Provider V3;

        S1 = new Bouquet_Creater(store,Flower.Pions);
        S1.name = "S1";
/*            S2 = new Bouquet_Creater(store,Flower.Filas);
        S2.name = "S2";
        S3 = new Bouquet_Creater(store,Flower.Roses);
        S3.name = "S3";*/

        V1 = new Provider(store,Flower.Roses,4,Flower.Filas,5);
        V1.name = "V1";
        V2 = new Provider(store,Flower.Pions,2,Flower.Roses,3);
        V2.name = "V2";
        V3 = new Provider(store,Flower.Pions,5,Flower.Filas,1);
        V3.name = "V3";

        V1.start();
        V2.start();
        V3.start();

        V1.join();
        V2.join();
        V3.join();


        S1.start();
        S1.join();


/*            S2.start();
        S3.start();*/
        store.show_store();

/*            S2.join();
        S3.join();*/

    }
}