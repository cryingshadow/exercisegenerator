package exercisegenerator.structures.sorting;

import java.util.*;

import exercisegenerator.io.*;

public record CountingSortSolution(
    List<ItemWithTikZInformation<Integer>> countArray,
    List<ItemWithTikZInformation<Integer>> solutionArray
) {}
