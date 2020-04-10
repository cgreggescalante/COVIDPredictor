package com.covidpredictor;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

@Getter
public class Region {
    private String name;

    private Map<LocalDate, Integer> cumulativeCases;
    private Map<LocalDate, Integer> cumulativeFatalities;

    private Map<LocalDate, Integer> dailyCases;
    private Map<LocalDate, Integer> dailyFatalities;

    public Region(String name) {
        this.name = name;

        cumulativeCases = new TreeMap<>();
        cumulativeFatalities = new TreeMap<>();

        dailyCases = new TreeMap<>();
        dailyFatalities = new TreeMap<>();
    }

    public void addDay(LocalDate localDate, int cases, int fatalities) {
        cumulativeCases.put(localDate, cases);
        cumulativeFatalities.put(localDate, fatalities);

        LocalDate previousDate = localDate.minusDays(1);

        if (cumulativeCases.containsKey(previousDate)) {
            if (name.equals("New York")) {
                System.out.println(cases + " " + cumulativeCases.get(previousDate));
            }
            dailyCases.put(localDate, cases - cumulativeCases.get(previousDate));
            dailyFatalities.put(localDate, fatalities - cumulativeFatalities.get(previousDate));
        } else {
            dailyCases.put(localDate, cases);
            dailyFatalities.put(localDate, fatalities);
        }
    }

    public int getCurrentCases() {
        int size = cumulativeCases.size();

        LocalDate date = (LocalDate) cumulativeCases.keySet().toArray()[size];

        return cumulativeCases.get(date);
    }
}
