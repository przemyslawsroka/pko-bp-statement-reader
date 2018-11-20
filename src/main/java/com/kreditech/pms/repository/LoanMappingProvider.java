package com.kreditech.pms.repository;

import com.kreditech.pms.model.LoanMapping;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class LoanMappingProvider {

    public List<LoanMapping> getMappings() throws IOException {
        List<LoanMapping> mappings = new LinkedList<>();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("UuidToLoanIdMapping.csv").getFile());

        Scanner sc = new Scanner(file);

        while (sc.hasNextLine())
        {
            String[] line = sc.nextLine().split(",");
            if (line.length == 2)
            {
                if (line[0] == "UUID") // Skip header
                    continue;
                mappings.add(new LoanMapping(line[0].toLowerCase(), line[1]));
            }
        }
        return mappings;
    }
}
