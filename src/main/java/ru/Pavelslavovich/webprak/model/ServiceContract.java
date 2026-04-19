package ru.Pavelslavovich.webprak.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service_contracts")
public class ServiceContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;

    @Column(name = "contract_number", nullable = false, unique = true)
    private String contractNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(name = "signed_on", nullable = false)
    private LocalDate signedOn;

    @Column(name = "service_start", nullable = false)
    private LocalDate serviceStart;

    @Column(name = "service_end")
    private LocalDate serviceEnd;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private ContractStatus status;

    @Column(name = "agreed_cost", nullable = false)
    private BigDecimal agreedCost;

    @Column(name = "comment")
    private String comment;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ContractEmployee> employees = new HashSet<>();

    public ServiceContract() {
    }

    public ServiceContract(
            String contractNumber,
            Client client,
            ServiceEntity service,
            LocalDate signedOn,
            LocalDate serviceStart,
            LocalDate serviceEnd,
            ContractStatus status,
            BigDecimal agreedCost,
            String comment
    ) {
        this.contractNumber = contractNumber;
        this.client = client;
        this.service = service;
        this.signedOn = signedOn;
        this.serviceStart = serviceStart;
        this.serviceEnd = serviceEnd;
        this.status = status;
        this.agreedCost = agreedCost;
        this.comment = comment;
    }

    public void addEmployee(Employee employee, String role) {
        ContractEmployee contractEmployee = new ContractEmployee(this, employee, role);
        employees.add(contractEmployee);
        employee.getContracts().add(contractEmployee);
    }

    public Long getId() {
        return id;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public LocalDate getSignedOn() {
        return signedOn;
    }

    public void setSignedOn(LocalDate signedOn) {
        this.signedOn = signedOn;
    }

    public LocalDate getServiceStart() {
        return serviceStart;
    }

    public void setServiceStart(LocalDate serviceStart) {
        this.serviceStart = serviceStart;
    }

    public LocalDate getServiceEnd() {
        return serviceEnd;
    }

    public void setServiceEnd(LocalDate serviceEnd) {
        this.serviceEnd = serviceEnd;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public BigDecimal getAgreedCost() {
        return agreedCost;
    }

    public void setAgreedCost(BigDecimal agreedCost) {
        this.agreedCost = agreedCost;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<ContractEmployee> getEmployees() {
        return employees;
    }
}
