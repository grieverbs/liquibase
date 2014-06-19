package liquibase.sqlgenerator.core;

import liquibase.database.Database;
import liquibase.database.core.DB2Database;
import liquibase.database.core.OracleDatabase;
import liquibase.database.core.PostgresDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutionOptions;
import liquibase.action.Sql;
import liquibase.action.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.SetColumnRemarksStatement;

public class SetColumnRemarksGenerator extends AbstractSqlGenerator<SetColumnRemarksStatement> {
	@Override
	public int getPriority() {
		return PRIORITY_DEFAULT;
	}

	@Override
	public boolean supports(SetColumnRemarksStatement statement, ExecutionOptions options) {
        Database database = options.getRuntimeEnvironment().getTargetDatabase();

        return database instanceof OracleDatabase || database instanceof PostgresDatabase || database instanceof DB2Database;
	}

	@Override
    public ValidationErrors validate(SetColumnRemarksStatement setColumnRemarksStatement, ExecutionOptions options, SqlGeneratorChain sqlGeneratorChain) {
		ValidationErrors validationErrors = new ValidationErrors();
		validationErrors.checkRequiredField("tableName", setColumnRemarksStatement.getTableName());
		validationErrors.checkRequiredField("columnName", setColumnRemarksStatement.getColumnName());
		validationErrors.checkRequiredField("remarks", setColumnRemarksStatement.getRemarks());
		return validationErrors;
	}

	@Override
    public Sql[] generateSql(SetColumnRemarksStatement statement, ExecutionOptions options, SqlGeneratorChain sqlGeneratorChain) {
        Database database = options.getRuntimeEnvironment().getTargetDatabase();

        return new Sql[] { new UnparsedSql("COMMENT ON COLUMN " + database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName())
				+ "." + database.escapeColumnName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName(), statement.getColumnName()) + " IS '"
				+ database.escapeStringForDatabase(statement.getRemarks()) + "'") };
	}
}
