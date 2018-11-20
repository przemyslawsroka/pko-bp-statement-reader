package com.kreditech.pms.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OperationMapping {
    Operation operation;
    String loanId;
}
