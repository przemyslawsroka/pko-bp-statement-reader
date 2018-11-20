package com.kreditech.pms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Operation {
    Date execDate;
    Date orderDate;
    String type;
    String description;
    BigDecimal amount;
    BigDecimal endingBalance;
}
