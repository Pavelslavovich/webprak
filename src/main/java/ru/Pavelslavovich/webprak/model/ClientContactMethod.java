package ru.Pavelslavovich.webprak.model;

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
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "client_contact_methods")
public class ClientContactMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private ClientContact contact;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "method_type", nullable = false)
    private ContactMethodType methodType;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    public ClientContactMethod() {
    }

    public ClientContactMethod(ContactMethodType methodType, String value, boolean primary) {
        this.methodType = methodType;
        this.value = value;
        this.primary = primary;
    }

    public Long getId() {
        return id;
    }

    public ClientContact getContact() {
        return contact;
    }

    public void setContact(ClientContact contact) {
        this.contact = contact;
    }

    public ContactMethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(ContactMethodType methodType) {
        this.methodType = methodType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
