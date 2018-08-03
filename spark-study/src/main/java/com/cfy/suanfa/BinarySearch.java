package com.cfy.suanfa;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/23
 * Time: 21:21
 * Work contact: Astion_Leo@163.com
 */


public class BinarySearch {

    public static void main(String[] args) {

        Integer integer = binarySearch(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 3);
        System.out.println(integer);

    }



    public static Integer binarySearch(int[] arrays, int num) {

        int left = 0;
        int right = arrays.length - 1;
        int count = 0;

        while (left <= right){

            int mid = (left + right) / 2;
            int target = arrays[mid];

            if (target == num){
                return mid;
            } else if (target > num){
                right = mid - 1;
            } else if (target < num){
                left = mid + 1;
            }
            count ++;
            System.out.println("查找了" + count);
        }

        return -1;

    }
}
