package ru.Pavelslavovich.webprak.test;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.EnumJdbcType;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;

public class H2NamedEnumDialect extends H2Dialect {
    @Override
    protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.registerColumnTypes(typeContributions, serviceRegistry);
        typeContributions.getTypeConfiguration()
                .getDdlTypeRegistry()
                .addDescriptor(SqlTypes.NAMED_ENUM, new DdlTypeImpl(SqlTypes.NAMED_ENUM, "varchar(32)", this));
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        typeContributions.getTypeConfiguration()
                .getJdbcTypeRegistry()
                .addDescriptor(SqlTypes.NAMED_ENUM, EnumJdbcType.INSTANCE);
    }
}