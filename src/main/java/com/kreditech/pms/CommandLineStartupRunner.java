package com.kreditech.pms;

import com.kreditech.pms.model.LoanMapping;
import com.kreditech.pms.model.Operation;
import com.kreditech.pms.model.OperationMapping;
import com.kreditech.pms.repository.LoanMappingProvider;
import com.kreditech.pms.statement.StatementReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;

@Component
public class CommandLineStartupRunner implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(CommandLineStartupRunner.class);

    @Override
    public void run(String... args) throws Exception {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start("Get UUID Dictionary");
        LoanMappingProvider provider = new LoanMappingProvider();
        List<LoanMapping> loanMappings = provider.getMappings();
        stopWatch.stop();

        stopWatch.start("Get Statements");
        StatementReader statementReader = new StatementReader();
        List<Operation> operations = statementReader.readOperations();
        stopWatch.stop();

        for (StopWatch.TaskInfo taskInfo : stopWatch.getTaskInfo())
        {
            System.out.println("Task: " + taskInfo.getTaskName() + " Duration:" + taskInfo.getTimeMillis() + "ms.");
        }

        stopWatch.start("Assign Statements To Loan ID");
        List<OperationMapping> operationMappings = getAdvancedMapping(loanMappings, operations);
        stopWatch.stop();

        for (StopWatch.TaskInfo taskInfo : stopWatch.getTaskInfo())
        {
            System.out.println("Task: " + taskInfo.getTaskName() + " Duration:" + taskInfo.getTimeMillis() + "ms.");
        }

        int mapped = 0;
        for (OperationMapping mapping : operationMappings)
            if (mapping.getLoanId() != null)
                mapped++;
        System.out.println("Match rate: " + mapped + " out of: " + operationMappings.size());
    }

    public List<OperationMapping> getAdvancedMapping(List<LoanMapping> loanMappings, List<Operation> operations) {

        // Build hash map with UUID to Loan IDs
        List<OperationMapping> operationMappings = new LinkedList<>();
        HashMap<String, String> uuids = new HashMap<>();
        for (LoanMapping mapping : loanMappings)
            uuids.put(mapping.getUuid(), mapping.getLoanId());

        int counter = 0;
        for (Operation operation : operations)
        {
            OperationMapping operationMapping = new OperationMapping(operation, null);
            String description = operation.getDescription().toLowerCase();

            // Go through letters of description and search for 6 letter string that is in UUIDs map
            for (int i = 0; i < description.length() - 6; i++) {
                String sixLetters = description.substring(i, i + 6);
                if (uuids.containsKey(sixLetters))
                {
                    operationMapping.setLoanId(uuids.get(sixLetters));
                    break;
                }
            }
            operationMappings.add(operationMapping);

            counter++;
            if (counter % 100 == 0)
                System.out.println("Processed: " + counter + " out of: " + operations.size());
        }

        return operationMappings;
    }

    public List<OperationMapping> getBasicMapping(List<LoanMapping> loanMappings, List<Operation> operations) {

        List<OperationMapping> operationMappings = new LinkedList<>();

        int counter = 0;
        for (Operation operation : operations)
        {
            OperationMapping operationMapping = new OperationMapping(operation, null);
            for (LoanMapping mapping : loanMappings) {
                if (operation.getDescription().toLowerCase().contains(mapping.getUuid())) {
                    operationMapping.setLoanId(mapping.getLoanId());
                    break;
                }
            }
            operationMappings.add(operationMapping);

            counter++;
            if (counter % 100 == 0)
                System.out.println("Processed: " + counter + " out of: " + operations.size());
        }

        return operationMappings;
    }
}
