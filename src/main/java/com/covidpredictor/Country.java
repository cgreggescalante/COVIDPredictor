package com.covidpredictor;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class Country extends Region {
    @Getter
    private Map<String, Region> regions;

    public Country(String name) {
        super(name);

        regions = new TreeMap<>();
    }

    public void addDay(String[] entry) {
        LocalDate localDate = LocalDate.parse(entry[3]);
        int cases = (int) Float.parseFloat(entry[4]);
        int fatalities = (int) Float.parseFloat(entry[5]);

        if (getCumulativeCases().containsKey(localDate)) {
            cases += getCumulativeCases().get(localDate);
            fatalities += getCumulativeFatalities().get(localDate);
        }

        getCumulativeCases().put(localDate, cases);
        getCumulativeFatalities().put(localDate, fatalities);

        if (regions.containsKey(entry[1])) {
            regions.get(entry[1]).addDay(localDate, cases, fatalities);
        } else {
            int finalFatalities = fatalities;
            int finalCases = cases;
            regions.put(entry[1],
                    new Region(entry[1]) {{
                        addDay(localDate, finalCases, finalFatalities);
                    }}
            );
        }
    }
}
