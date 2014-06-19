package liquibase.sqlgenerator.core;

import liquibase.database.Database;
import liquibase.database.core.DerbyDatabase;
import liquibase.database.core.H2Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.core.MSSQLDatabase;
import liquibase.executor.ExecutionOptions;
import liquibase.datatype.DataTypeFactory;
import liquibase.exception.ValidationErrors;
import liquibase.action.Sql;
import liquibase.action.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.AddAutoIncrementStatement;

public class AddAutoIncrementGenerator extends AbstractSqlGenerator<AddAutoIncrementStatement> {

    @Override
    public int getPriority() {
        return PRIORITY_DEFAULT;
    }

    @Override
    public boolean supports(AddAutoIncrementStatement statement, ExecutionOptions options) {
        Database database = options.getRuntimeEnvironment().getTargetDatabase();

        return (database.supportsAutoIncrement()
                && !(database instanceof DerbyDatabase)
                && !(database instanceof MSSQLDatabase)
                && !(database instanceof HsqlDatabase)
                && !(database instanceof H2Database));
    }

    @Override
    public ValidationErrors validate(
    		AddAutoIncrementStatement statement,
    		ExecutionOptions options,
    		SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors validationErrors = new ValidationErrors();

        validationErrors.checkRequiredField("columnName", statement.getColumnName());
        validationErrors.checkRequiredField("tableName", statement.getTableName());
        validationErrors.checkRequiredField("columnDataType", statement.getColumnDataType());


        return validationErrors;
    }

    @Override
    public Sql[] generateSql(
    		AddAutoIncrementStatement statement,
    		ExecutionOptions options,
    		SqlGeneratorChain sqlGeneratorChain) {
        Database database = options.getRuntimeEnvironment().getTargetDatabase();

        String sql = "ALTER TABLE "
            + database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName())
            + " MODIFY "
            + database.escapeColumnName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName(), statement.getColumnName())
            + " "
            + DataTypeFactory.getInstance().fromDescription(statement.getColumnDataType() + "{autoIncrement:true}", database).toDatabaseDataType(database)
            + " " 
            + database.getAutoIncrementClause(statement.getStartWith(), statement.getIncrementBy());

        return new Sql[]{
            new UnparsedSql(sql)
        };
    }
}
