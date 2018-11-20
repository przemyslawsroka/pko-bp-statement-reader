package com.kreditech.pms.statement;

import com.kreditech.pms.model.Operation;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

@Component
public class StatementReader {

    public List<Operation> readOperations() throws Exception {

        List<Operation> operations = new LinkedList<>();

        try {
            File fXmlFile = new File("C:\\Users\\przemyslaw.sroka\\Desktop\\PKORepayments\\AccountHistory.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("operation");

            // Create date formatter
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            // Create a DecimalFormat that fits your requirements
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
            String pattern = "##0.0#";
            DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
            decimalFormat.setParseBigDecimal(true);

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    Operation operation = new Operation();

                    String execDateString = eElement.getElementsByTagName("exec-date").item(0).getTextContent();
                    String orderDateString = eElement.getElementsByTagName("order-date").item(0).getTextContent();


                    operation.setExecDate(dateFormatter.parse(execDateString));
                    operation.setOrderDate(dateFormatter.parse(orderDateString));

                    operation.setType(eElement.getElementsByTagName("type").item(0).getTextContent());
                    operation.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());

                    // parse the string
                    String amountString = eElement.getElementsByTagName("amount").item(0).getTextContent();
                    String endingBalanceString = eElement.getElementsByTagName("ending-balance").item(0).getTextContent();

                    String amountSign = amountString.substring(0,1);
                    String endingBalanceSign = amountString.substring(0,1);

                    BigDecimal amount = (BigDecimal) decimalFormat.parse(amountString.substring(1));
                    BigDecimal endingBalance = (BigDecimal) decimalFormat.parse(endingBalanceString.substring(1));

                    if (amountSign == "-") {
                        amount = amount.multiply(new BigDecimal(-1));
                    }

                    if (endingBalanceSign == "-") {
                        endingBalance = endingBalance.multiply(new BigDecimal(-1));
                    }

                    operation.setAmount(amount);
                    operation.setEndingBalance(endingBalance);

                    operations.add(operation);
                }

            }
        } catch (Exception e) {
            throw new Exception("Cannot read list of operations", e);
        }
        return operations;
    }
}
