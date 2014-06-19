package liquibase.sqlgenerator.core;

import liquibase.change.ColumnConfig;
import liquibase.database.core.SQLiteDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.executor.ExecutionOptions;
import liquibase.action.Sql;
import liquibase.action.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.CopyRowsStatement;

public class CopyRowsGenerator extends AbstractSqlGenerator<CopyRowsStatement> {

    @Override
    public boolean supports(CopyRowsStatement statement, ExecutionOptions options) {
        return (options.getRuntimeEnvironment().getTargetDatabase() instanceof SQLiteDatabase);
    }

    @Override
    public ValidationErrors validate(CopyRowsStatement copyRowsStatement, ExecutionOptions options, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors validationErrors = new ValidationErrors();
        validationErrors.checkRequiredField("targetTable", copyRowsStatement.getTargetTable());
        validationErrors.checkRequiredField("sourceTable", copyRowsStatement.getSourceTable());
        validationErrors.checkRequiredField("copyColumns", copyRowsStatement.getCopyColumns());
        return validationErrors;
    }

    @Override
    public Sql[] generateSql(CopyRowsStatement statement, ExecutionOptions options, SqlGeneratorChain sqlGeneratorChain) {
        StringBuffer sql = new StringBuffer();
        if (options.getRuntimeEnvironment().getTargetDatabase() instanceof SQLiteDatabase) {
            sql.append("INSERT INTO `").append(statement.getTargetTable()).append("` (");

            for (int i = 0; i < statement.getCopyColumns().size(); i++) {
                ColumnConfig column = statement.getCopyColumns().get(i);
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("`").append(column.getName()).append("`");
            }

            sql.append(") SELECT ");
            for (int i = 0; i < statement.getCopyColumns().size(); i++) {
                ColumnConfig column = statement.getCopyColumns().get(i);
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("`").append(column.getName()).append("`");
            }
            sql.append(" FROM `").append(statement.getSourceTable()).append("`");
        }

        return new Sql[]{
                new UnparsedSql(sql.toString())
        };
    }
}
