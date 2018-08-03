package com.cfy.suanfa;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/23
 * Time: 22:00
 * Work contact: Astion_Leo@163.com
 */


public class BubbleSort {

    public static void main(String[] args) {
        int[] ints = bubbleSort(new int[]{2, 55, 2, 53, 6, 634, 432, 43});
        for (int num : ints){
            System.out.println(num + " ");
        }
    }


    public static int[] bubbleSort(int[] array) {
        for (int x=0; x<array.length; x++){
            for (int y=1; y<array.length-x; y++){
                if (array[y-1] > array[y]){
                    int temp = array[y];
                    array[y] = array[y-1];
                    array[y-1] = temp;
                }
            }
        }
        return array;
    }
}
