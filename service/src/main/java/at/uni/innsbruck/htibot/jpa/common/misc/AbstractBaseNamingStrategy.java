package at.uni.innsbruck.htibot.jpa.common.misc;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public abstract class AbstractBaseNamingStrategy implements PhysicalNamingStrategy {

  @Override
  public Identifier toPhysicalCatalogName(final Identifier name, final JdbcEnvironment jdbcEnvironment) {
    return this._handle(name);
  }

  @Override
  public Identifier toPhysicalSchemaName(final Identifier name, final JdbcEnvironment jdbcEnvironment) {
    return this._handle(name);
  }

  @Override
  public Identifier toPhysicalTableName(final Identifier name, final JdbcEnvironment jdbcEnvironment) {
    return this._handle(name);
  }

  @Override
  public Identifier toPhysicalSequenceName(final Identifier name, final JdbcEnvironment jdbcEnvironment) {
    return this._handle(name);
  }

  @Override
  public Identifier toPhysicalColumnName(final Identifier name, final JdbcEnvironment jdbcEnvironment) {
    return this._handle(name);
  }

  abstract Identifier handle(Identifier identifier);

  private Identifier _handle(final Identifier identifier) {

    return identifier != null ? this.handle(identifier) : identifier;

  }

}
