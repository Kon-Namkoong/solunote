package com.vol.solunote.config;

import lombok.Getter;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.Locale;
@Getter
public class JPANamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply( name, jdbcEnvironment );
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply( name, jdbcEnvironment );
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply( name, jdbcEnvironment );
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply( name, jdbcEnvironment );
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return apply( name, jdbcEnvironment );
    }

    private Identifier apply(final Identifier name, final JdbcEnvironment jdbcEnvironment) {
        if ( name == null ) {
            return null;
        }
        if ( name.isQuoted() ) {
            return name;
        }
        StringBuilder builder = new StringBuilder( name.getText().replace( '.', '_' ) );
        for ( int i = 1; i < builder.length() - 1; i++ ) {
            if ( isUnderscoreRequired( builder.charAt( i - 1 ), builder.charAt( i ), builder.charAt( i + 1 ) ) ) {
                builder.insert( i++, '_' );
            }
        }
        return getIdentifier( builder.toString(), name.isQuoted(), jdbcEnvironment );
    }

    /**
     * Get an identifier for the specified details. By default this method will return an identifier
     * with the name adapted based on the result of {@link #isCaseInsensitive(JdbcEnvironment)}
     *
     * @param name the name of the identifier
     * @param quoted if the identifier is quoted
     * @param jdbcEnvironment the JDBC environment
     *
     * @return an identifier instance
     */
    protected Identifier getIdentifier(String name, final boolean quoted, final JdbcEnvironment jdbcEnvironment) {
        if ( isCaseInsensitive( jdbcEnvironment ) ) {
            name = name.toLowerCase( Locale.ROOT );
        }
        return new Identifier( name, quoted );
    }

    /**
     * Specify whether the database is case sensitive.
     *
     * @param jdbcEnvironment the JDBC environment which can be used to determine case
     *
     * @return true if the database is case insensitive sensitivity
     */
    protected boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment) {
        return true;
    }

    private boolean isUnderscoreRequired(final char before, final char current, final char after) {
        return Character.isLowerCase( before ) && Character.isUpperCase( current ) && Character.isLowerCase( after );
    }

}