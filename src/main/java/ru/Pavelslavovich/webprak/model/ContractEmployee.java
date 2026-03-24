package ru.Pavelslavovich.webprak.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "contract_employees")
public class ContractEmployee {
    @EmbeddedId
    private ContractEmployeeId id = new ContractEmployeeId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("contractId")
    @JoinColumn(name = "contract_id", nullable = false)
    private ServiceContract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "role")
    private String role;

    public ContractEmployee() {
    }

    public ContractEmployee(ServiceContract contract, Employee employee, String role) {
        this.contract = contract;
        this.employee = employee;
        this.role = role;
    }

    public ContractEmployeeId getId() {
        return id;
    }

    public ServiceContract getContract() {
        return contract;
    }

    public void setContract(ServiceContract contract) {
        this.contract = contract;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
