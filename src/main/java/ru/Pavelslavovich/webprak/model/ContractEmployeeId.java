package ru.Pavelslavovich.webprak.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ContractEmployeeId implements Serializable {
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "employee_id")
    private Long employeeId;

    public ContractEmployeeId() {
    }

    public ContractEmployeeId(Long contractId, Long employeeId) {
        this.contractId = contractId;
        this.employeeId = employeeId;
    }

    public Long getContractId() {
        return contractId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContractEmployeeId that)) {
            return false;
        }
        return Objects.equals(contractId, that.contractId) && Objects.equals(employeeId, that.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractId, employeeId);
    }
}
