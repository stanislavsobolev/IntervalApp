package com.sobolev;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobolev.model.Interval;

public class Jackson2Example {

    public static void main(String[] args) {
        Jackson2Example obj = new Jackson2Example();
        obj.run();
    }

    private void run() {
        ObjectMapper mapper = new ObjectMapper();

        Interval staff2 = new Interval(3,4);
        Interval staff = new Interval(1,2);

        List<Interval> intervals = new ArrayList<Interval>();
        intervals.add(staff);
        intervals.add(staff2);

        try {
            // Convert object to JSON string and save into a file directly
            mapper.writeValue(new File("F:\\staff.json"), intervals);

            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(staff);
            System.out.println(jsonInString);

            // Convert object to JSON string and pretty print
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(staff);
            System.out.println(jsonInString);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
