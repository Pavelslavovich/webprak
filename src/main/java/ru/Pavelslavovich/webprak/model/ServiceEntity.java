package ru.Pavelslavovich.webprak.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "base_cost", nullable = false)
    private BigDecimal baseCost;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServiceContract> contracts = new ArrayList<>();

    public ServiceEntity() {
    }

    public ServiceEntity(String name, BigDecimal baseCost) {
        this.name = name;
        this.baseCost = baseCost;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    public List<ServiceContract> getContracts() {
        return contracts;
    }
}
