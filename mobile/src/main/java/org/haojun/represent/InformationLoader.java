package org.haojun.represent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class loads the information from server and parses them
 * Created by Haojun on 2/29/16.
 */
public class InformationLoader {

    public static List<Map<String, String>> getCandidates(int zip) {
        return getCandidatesDummy(zip);
    }

    public static Map<String, String> getDetailedCandidate(String name) {
        return getDetailedCandidateDummy(name);
    }

    public static Map<String, String> getDetailedCandidateDummy(String name) {
        Map<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("email", "foo@us.gov");
        result.put("website", "foo.us.gov");
        result.put("party", "Democrat");
        result.put("picuri", "https://stampaspot.com/Pics/default.jpeg");
        result.put("term", "0 - POSITIVE_INFINITY");
        result.put("committee", "1. this committee\n2. that committee");
        result.put("bills", "1. Bill No. 202020 Free Pony for all\n2. Bill No. 20202 No Death Sentence");
        return result;
    }

    public static List<Map<String, String>> getCandidatesDummy(int zip) {
        List<Map<String, String>> result = new ArrayList<>();

        HashMap<String, String> candidate1 = new HashMap<>();
        candidate1.put("name", "Foo");
        candidate1.put("email", "foo@us.gov");
        candidate1.put("website", "foo.us.gov");
        candidate1.put("party", "Democrat");
        candidate1.put("tweet", "I will win");
        candidate1.put("picuri", "https://stampaspot.com/Pics/default.jpeg");

        HashMap<String, String> candidate2 = new HashMap<>();
        candidate2.put("name", "Bar");
        candidate2.put("email", "bar@us.gov");
        candidate2.put("website", "bar.us.gov");
        candidate2.put("party", "Republican");
        candidate2.put("tweet", "I must win");
        candidate2.put("picuri", "https://stampaspot.com/Pics/default.jpeg");


        HashMap<String, String> candidate3 = new HashMap<>();
        candidate3.put("name", "FooBar");
        candidate3.put("email", "foobar@us.gov");
        candidate3.put("website", "foobar.us.gov");
        candidate3.put("party", "Independent");
        candidate3.put("tweet", "I can win");
        candidate3.put("picuri", "https://stampaspot.com/Pics/default.jpeg");


        result.add(candidate1);
        result.add(candidate2);
        result.add(candidate3);
        return result;
    }


}
