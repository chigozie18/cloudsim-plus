package org.cloudsimplus.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
 
public class MergeTest 
{
    public static void main(String[] args) throws Exception 
    {
        ArrayList<String> listOne = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e"));
         
        ArrayList<String> listTwo = new ArrayList<>(Arrays.asList("1", "2", "3"));
         
        List<String> combinedList = Stream.of(listOne, listTwo)
                                        .flatMap(x -> x.stream())
                                        .collect(Collectors.toList());
        System.out.println(combinedList);
    }
}