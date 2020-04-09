package com.covidpredictor;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CaseData {
    private Map<String, Country> countries;

    public CaseData() {
        countries = new TreeMap<>();

        try {
            FileReader reader = new FileReader("src/main/resources/data/train.csv");

            CSVReader csvReader = new CSVReader(reader, ',', '"', 1);

            List<String[]> entries = csvReader.readAll();

            for (String[] entry : entries) {
                if (countries.containsKey(entry[2])) {
                    countries.get(entry[2]).addDay(entry);
                } else {
                    System.out.println(Arrays.toString(entry));
                    countries.put(
                            entry[2],
                            new Country(entry[2])
                    );
                }
            }

            for (Country country : countries.values()) {
                System.out.println(country.getRegions().size());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new CaseData();
    }
}
