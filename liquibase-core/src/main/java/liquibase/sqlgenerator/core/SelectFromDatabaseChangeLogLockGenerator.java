package liquibase.sqlgenerator.core;

import liquibase.database.Database;
import liquibase.database.core.OracleDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutionOptions;
import liquibase.action.Sql;
import liquibase.action.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.SelectFromDatabaseChangeLogLockStatement;
import liquibase.util.StringUtils;

public class SelectFromDatabaseChangeLogLockGenerator extends AbstractSqlGenerator<SelectFromDatabaseChangeLogLockStatement> {

    @Override
    public ValidationErrors validate(SelectFromDatabaseChangeLogLockStatement statement, ExecutionOptions options, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors errors = new ValidationErrors();
        errors.checkRequiredField("columnToSelect", statement.getColumnsToSelect());

        return errors;
    }

    @Override
    public Sql[] generateSql(SelectFromDatabaseChangeLogLockStatement statement, ExecutionOptions options, SqlGeneratorChain sqlGeneratorChain) {
        Database database = options.getRuntimeEnvironment().getTargetDatabase();
    	String liquibaseSchema;
   		liquibaseSchema = database.getLiquibaseSchemaName();
		
		String[] columns = statement.getColumnsToSelect();
		int numberOfColumns = columns.length;
		String[] escapedColumns = new String[numberOfColumns];
		for (int i=0; i<numberOfColumns; i++) {
			escapedColumns[i] = database.escapeColumnName(database.getLiquibaseCatalogName(), liquibaseSchema, database.getDatabaseChangeLogLockTableName(), columns[i]);
		}
		
        String sql = "SELECT " + StringUtils.join(escapedColumns, ",") + " FROM " +
                database.escapeTableName(database.getLiquibaseCatalogName(), liquibaseSchema, database.getDatabaseChangeLogLockTableName()) +
                " WHERE " + database.escapeColumnName(database.getLiquibaseCatalogName(), liquibaseSchema, database.getDatabaseChangeLogLockTableName(), "ID") + "=1";

        if (database instanceof OracleDatabase) {
            sql += " FOR UPDATE";
        }
        return new Sql[] {
                new UnparsedSql(sql)
        };
    }
}
